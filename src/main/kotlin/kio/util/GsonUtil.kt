/*
 * Copyright 2017-2018 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kio.util

import com.google.common.io.BaseEncoding
import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.File
import java.io.Reader
import java.lang.reflect.Type
import kotlin.collections.set
import kotlin.reflect.KClass

/** @author Kotcrab */

val stdGson = createStdGson()

fun createStdGson(prettyPrint: Boolean = true, byteArrayAsBase64: Boolean = true, configure: GsonBuilder.() -> Unit = {}): Gson {
    val builder = GsonBuilder()
    if (prettyPrint) {
        builder.setPrettyPrinting()
    }
    if (byteArrayAsBase64) {
        builder.registerTypeAdapter(ByteArray::class.java, ByteArrayAsBase64TypeAdapter())
    }
    builder.configure()
    return builder.create()
}

private class ByteArrayAsBase64TypeAdapter : JsonSerializer<ByteArray>, JsonDeserializer<ByteArray> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ByteArray {
        return BaseEncoding.base64().decode(json.asString)
    }

    override fun serialize(src: ByteArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(BaseEncoding.base64().encode(src))
    }
}

inline fun <reified T> Gson.fromJson(reader: Reader) = this.fromJson<T>(reader, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

fun File.writeJson(src: Any) {
    writeJson(stdGson, src)
}

fun File.writeJson(gson: Gson, src: Any) {
    bufferedWriter().use { gson.toJson(src, it) }
}

inline fun <reified T> File.readJson(gson: Gson = stdGson): T {
    return bufferedReader().use { gson.fromJson(it) }
}

fun <C : Any> runtimeTypeAdapter(base: KClass<C>,
                                 subTypes: Array<KClass<out C>>,
                                 legacySubTypes: Array<Pair<String, KClass<out C>>> = emptyArray()): RuntimeTypeAdapterFactory<C> {
    val adapter = RuntimeTypeAdapterFactory(base, "_type")
    subTypes.forEach { subClass ->
        adapter.registerSubtype(subClass)
    }
    legacySubTypes.forEach { subClass ->
        adapter.registerLegacySubtype(subClass.second, subClass.first)
    }
    return adapter
}

class RuntimeTypeAdapterFactory<T : Any>(
        private val baseType: KClass<T>,
        private val typeFieldName: String = "_type"
) : TypeAdapterFactory {
    private val labelToSubtype = mutableMapOf<String, KClass<out T>>()
    private val legacyLabelToSubtype = mutableMapOf<String, KClass<out T>>()
    private val subtypeToLabel = mutableMapOf<KClass<out T>, String>()
    private var cachedTypeAdapter: TypeAdapter<*>? = null

    fun <R : T> registerSubtype(type: KClass<R>) {
        val label = type.simpleName ?: error("type does not provide simple name")
        registerSubtype(type, label)
    }

    fun <R : T> registerSubtype(type: KClass<R>, label: String) {
        if (cachedTypeAdapter != null) {
            error("can't add subtype, this factory has already created and cached its type adapter")
        }
        if (subtypeToLabel.contains(type) || labelToSubtype.contains(label)) {
            error("types and labels must be unique")
        }
        subtypeToLabel[type] = label
        labelToSubtype[label] = type
    }

    fun <R : T> registerLegacySubtype(type: KClass<R>, label: String) {
        if (cachedTypeAdapter != null) {
            error("can't add legacy subtype, this factory has already created and cached its type adapter")
        }
        if (legacyLabelToSubtype.contains(label)) {
            error("legacy labels must be unique")
        }
        legacyLabelToSubtype[label] = type
    }

    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (baseType.java.isAssignableFrom(type.rawType) == false) return null
        if (cachedTypeAdapter == null) {
            val labelToDelegate = mutableMapOf<String, TypeAdapter<out T>>()
            val legacyLabelToDelegate = mutableMapOf<String, TypeAdapter<out T>>()
            val subtypeToDelegate = mutableMapOf<KClass<out T>, TypeAdapter<out T>>()
            labelToSubtype.forEach { label, subtype ->
                val delegate = gson.getDelegateAdapter(this, TypeToken.get(subtype.java))
                labelToDelegate[label] = delegate
                subtypeToDelegate[subtype] = delegate
            }
            legacyLabelToSubtype.forEach { label, subtype ->
                val delegate = gson.getDelegateAdapter(this, TypeToken.get(subtype.java))
                legacyLabelToDelegate[label] = delegate
            }
            cachedTypeAdapter = object : TypeAdapter<R>() {
                override fun write(output: JsonWriter, value: R) {
                    val srcType = (value as Any)::class
                    val label = subtypeToLabel[srcType]
                    @Suppress("UNCHECKED_CAST") // registration requires that subtype extends T
                    val delegate = subtypeToDelegate[srcType] as TypeAdapter<R>?
                            ?: throw JsonParseException("cannot serialize ${srcType.simpleName}, did you forget to " +
                                    "register a subtype?")
                    val jsonObject = delegate.toJsonTree(value).asJsonObject
                    val clone = JsonObject()
                    if (jsonObject.has(typeFieldName)) {
                        throw JsonParseException("cannot serialize ${srcType.simpleName} because it already defined " +
                                "a field names $typeFieldName")
                    }
                    clone.add(typeFieldName, JsonPrimitive(label))
                    jsonObject.entrySet().forEach { (key, value) ->
                        clone.add(key, value)
                    }
                    Streams.write(clone, output)
                }

                override fun read(input: JsonReader): R {
                    val jsonElement = Streams.parse(input)
                    val labelJsonElement = jsonElement.asJsonObject.remove(typeFieldName)
                            ?: throw JsonParseException("cannot deserialize $baseType because it does not define " +
                                    "a field named $typeFieldName")
                    val label = labelJsonElement.asString
                    @Suppress("UNCHECKED_CAST") // registration requires that subtype extends T
                    var delegate = labelToDelegate[label] as TypeAdapter<R>?
                    if (delegate == null) {
                        @Suppress("UNCHECKED_CAST") // registration requires that subtype extends T
                        delegate = legacyLabelToDelegate[label] as TypeAdapter<R>?
                        if (delegate == null) {
                            throw JsonParseException("cannot deserialize $baseType subtype named $label, " +
                                    "did you forget to register a subtype?")
                        }
                    }
                    return delegate.fromJsonTree(jsonElement)
                }
            }
        }
        @Suppress("UNCHECKED_CAST") // cached adapter is only created in this function
        return cachedTypeAdapter as TypeAdapter<R>
    }
}
