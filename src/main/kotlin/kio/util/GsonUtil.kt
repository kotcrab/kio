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
import com.google.common.reflect.TypeToken
import com.google.gson.*
import java.io.File
import java.io.Reader
import java.lang.reflect.Type

/** @author Kotcrab */

val stdGson = createStdGson()

fun createStdGson(prettyPrint: Boolean = true, byteArrayAsBase64: Boolean = true): Gson {
    val builder = GsonBuilder()
    if (prettyPrint) {
        builder.setPrettyPrinting()
    }
    if (byteArrayAsBase64) {
        builder.registerTypeAdapter(ByteArray::class.java, ByteArrayAsBase64TypeAdapter())
    }
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
