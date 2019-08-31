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

import com.google.gson.JsonParseException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.StringReader

/** @author Kotcrab */

class GsonUtilsTest {
    @Test
    fun `should call configure block`() {
        var called = false
        createStdGson { called = true }
        assertThat(called).isTrue()
    }

    @Test
    fun `should read json primitive`() {
        val arr: Int = stdGson.fromJson("1")
        assertThat(arr).isEqualTo(1)
    }

    @Test
    fun `should read json array`() {
        val arr: Array<String> = stdGson.fromJson("['a', 'b']")
        assertThat(arr).containsExactly("a", "b")
    }

    @Test
    fun `should read json list`() {
        val arr: List<String> = stdGson.fromJson("['a', 'b']")
        assertThat(arr).containsExactly("a", "b")
    }

    @Test
    fun `should read json map`() {
        val arr: Map<String, String> = stdGson.fromJson("{'a': '1', 'b': '2'}")
        assertThat(arr).containsEntry("a", "1").containsEntry("b", "2")
    }

    @Test
    fun `should read json array using reader`() {
        val arr: Array<String> = stdGson.fromJson(StringReader("['a', 'b']"))
        assertThat(arr).containsExactly("a", "b")
    }

    @Test
    fun `should write and read json to file`() {
        val file = createTempFile()
        val arr = arrayOf("a", "b", "c")
        file.writeJson(arr)
        assertThat(file.length()).isNotZero()
        val arr2: Array<String> = file.readJson()
        assertThat(arr).containsExactly(*arr2)
        file.delete()
    }

    @Test
    fun `should provide from default block when json file does not exist`() {
        val file = createTempFile()
        file.delete()
        assertThat(file.exists()).isFalse()
        val num = file.readJsonOrElse(default = { 42 })
        assertThat(num).isEqualTo(42)
    }

    @Test
    fun `should provide default when json file does not exist`() {
        val file = createTempFile()
        file.delete()
        assertThat(file.exists()).isFalse()
        val num = file.readJsonOrDefault(default = 42)
        assertThat(num).isEqualTo(42)
    }

    @Test
    fun `should provide null when json file does not exist`() {
        val file = createTempFile()
        file.delete()
        assertThat(file.exists()).isFalse()
        val num = file.readJsonOrNull<Int>()
        assertThat(num).isNull()
    }

    @Test
    fun `should handle byte array as base64`() {
        val gson = createStdGson(byteArrayAsBase64 = true)
        val str = gson.toJson(byteArrayOf(0, 1, 2, 3))
        assertThat(str).isNotBlank()
        assertThat(gson.fromJson(str) as ByteArray).containsExactly(0, 1, 2, 3)
    }

    @Nested
    inner class RuntimeTypeAdapterFactoryTest {
        @Test
        fun `should have unique subtypes`() {
            assertThatIllegalStateException().isThrownBy {
                RuntimeTypeAdapterFactory(Foo::class).apply {
                    registerSubtype(Bar::class, "Bar")
                    registerSubtype(Bar::class, "Bar2")
                }
            }
        }

        @Test
        fun `should have unique labels`() {
            assertThatIllegalStateException().isThrownBy {
                RuntimeTypeAdapterFactory(Foo::class).apply {
                    registerSubtype(Bar::class, "Label")
                    registerSubtype(FooBar::class, "Label")
                }
            }
        }

        @Test
        fun `should have unique legacy labels`() {
            assertThatIllegalStateException().isThrownBy {
                RuntimeTypeAdapterFactory(Foo::class).apply {
                    registerLegacySubtype(Bar::class, "Label")
                    registerLegacySubtype(FooBar::class, "Label")
                }
            }
        }

        @Test
        fun `should not serialize class with already existing type field`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(RuntimeTypeAdapterFactory(Foo::class, "type").apply {
                    registerSubtype(FailFoo::class)
                })
            }
            assertThatExceptionOfType(JsonParseException::class.java).isThrownBy {
                gson.toJson(arrayOf(FailFoo()))
            }
        }

        @Test
        fun `should not serialize unregistered type`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(RuntimeTypeAdapterFactory(Foo::class, "type").apply {
                    registerSubtype(Bar::class)
                })
            }
            assertThatExceptionOfType(JsonParseException::class.java).isThrownBy {
                gson.toJson(Baz())
            }
        }

        @Test
        fun `should not deserialize json with missing type field`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(
                    runtimeTypeAdapter(
                        Foo::class,
                        subTypes = arrayOf(Bar::class, FooBar::class, Baz::class)
                    )
                )
            }
            assertThatExceptionOfType(JsonParseException::class.java).isThrownBy {
                gson.fromJson<Array<Foo>>("[{\"_type\":\"Bar\",\"b\":0,\"a\":0},{\"b\":0,\"a\":0}]")
            }
        }

        @Test
        fun `should not deserialize not registered type`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(RuntimeTypeAdapterFactory(Foo::class, "type").apply {
                    registerSubtype(Bar::class)
                    registerSubtype(Baz::class)
                })
            }
            val secondGson = createStdGson {
                registerTypeAdapterFactory(RuntimeTypeAdapterFactory(Foo::class, "type").apply {
                    registerSubtype(Bar::class)
                })
            }
            val json = gson.toJson(arrayOf(Bar(), Baz()))
            assertThatExceptionOfType(JsonParseException::class.java).isThrownBy {
                secondGson.fromJson<Array<Foo>>(json)
            }
        }

        @Test
        fun `should serialize class hierarchy in list`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(
                    runtimeTypeAdapter(
                        Foo::class,
                        subTypes = arrayOf(Bar::class, FooBar::class, Baz::class)
                    )
                )
            }
            val objList = listOf(Bar(), FooBar(), Baz())
            val json = gson.toJson(objList)
            val jsonObjList = gson.fromJson<List<Foo>>(json)
            assertThat(jsonObjList[0]).isInstanceOf(Bar::class.java)
            assertThat(jsonObjList[1]).isInstanceOf(FooBar::class.java)
            assertThat(jsonObjList[2]).isInstanceOf(Baz::class.java)
        }

        @Test
        fun `should serialize class hierarchy in array`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(
                    runtimeTypeAdapter(
                        Foo::class,
                        subTypes = arrayOf(Bar::class, FooBar::class, Baz::class)
                    )
                )
            }
            val objList = arrayOf(Bar(), FooBar(), Baz())
            val json = gson.toJson(objList)
            val jsonObjList = gson.fromJson<List<Foo>>(json)
            assertThat(jsonObjList[0]).isInstanceOf(Bar::class.java)
            assertThat(jsonObjList[1]).isInstanceOf(FooBar::class.java)
            assertThat(jsonObjList[2]).isInstanceOf(Baz::class.java)
        }

        @Test
        fun `should deserialize legacy label`() {
            val gson = createStdGson {
                registerTypeAdapterFactory(RuntimeTypeAdapterFactory(Foo::class).apply {
                    registerSubtype(Bar::class)
                })
            }
            val newGson = createStdGson {
                registerTypeAdapterFactory(RuntimeTypeAdapterFactory(Foo::class).apply {
                    registerSubtype(NewBar::class)
                    registerLegacySubtype(NewBar::class, "Bar")
                })
            }
            val arr: Array<Foo> = arrayOf(Bar(42))
            val json = gson.toJson(arr)
            val desArr = newGson.fromJson<Array<Foo>>(json)
            assertThat((desArr[0] as NewBar).b).isEqualTo(42)
        }

        @Test
        fun `should prevent adding to cached adapter`() {
            val factory = RuntimeTypeAdapterFactory(Foo::class).apply {
                registerSubtype(Bar::class)
            }
            val gson = createStdGson {
                registerTypeAdapterFactory(factory)
            }
            gson.toJson(arrayOf(Bar()))
            assertThatIllegalStateException().isThrownBy {
                factory.registerSubtype(Baz::class)
            }
            assertThatIllegalStateException().isThrownBy {
                factory.registerLegacySubtype(Baz::class, "Baz")
            }
        }
    }

    private open class Foo(val a: Int = 0)
    private class FailFoo(val type: Int = 0) : Foo()
    private open class Bar(val b: Int = 0) : Foo()
    private class NewBar(val b: Int = 0) : Foo()
    private class FooBar(val c: Int = 0) : Bar()
    private class Baz(val b: Int = 0) : Foo()
}
