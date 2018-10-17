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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.StringReader

/** @author Kotcrab */

internal class GsonUtilsTest {
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
        assertThat(arr).containsEntry("a", "1")
                .containsEntry("b", "2")
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
    fun `should handle byte array as base64`() {
        val gson = createStdGson(byteArrayAsBase64 = true)
        val str = gson.toJson(byteArrayOf(0, 1, 2, 3))
        assertThat(str).isNotBlank()
        assertThat(gson.fromJson(str) as ByteArray).containsExactly(0, 1, 2, 3)
    }
}
