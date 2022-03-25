/*
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

class PlatformUtilsTest {
  @Test
  fun `should pad array`() {
    assertThat(padArray(ByteArray(0), 4)).hasSize(0)
    assertThat(padArray(ByteArray(1), 4)).hasSize(4)
    assertThat(padArray(ByteArray(2), 4)).hasSize(4)
    assertThat(padArray(ByteArray(3), 4)).hasSize(4)
    assertThat(padArray(ByteArray(4), 4)).hasSize(4)
    assertThat(padArray(ByteArray(5), 4)).hasSize(8)
    assertThat(padArray(ByteArray(0), 16)).hasSize(0)
    assertThat(padArray(ByteArray(10), 16)).hasSize(16)
    assertThat(padArray(ByteArray(16), 16)).hasSize(16)
  }

  @Test
  fun `should copy array`() {
    val arr1 = ByteArray(5) { 0xCD.toByte() }
    val arr2 = ByteArray(5)
    arrayCopy(src = arr1, dest = arr2)
    assertThat(arr1).containsExactly(*arr2)
  }

  @Test
  fun `should copy part of array`() {
    val arr1 = ByteArray(5) { 0xCD.toByte() }
    val arr2 = ByteArray(5)
    arrayCopy(src = arr1, dest = arr2, destPos = 2, length = 1)
    assertThat(arr2).containsExactly(0, 0, 0xCD, 0, 0)
  }

  @Test
  fun `should swap 2 elements using indexes`() {
    val arr = mutableListOf(true, false)
    arr.swap(0, 1)
    assertThat(arr).containsExactly(false, true)
  }

  @Test
  fun `should swap 2 elements using objects`() {
    val arr = mutableListOf(true, false)
    arr.swap(arr[0], arr[1])
    assertThat(arr).containsExactly(false, true)
  }

  @Test
  fun `should append line`() {
    val sb = StringBuilder()
    sb.appendLine("foo")
    assertThat(sb.toString()).isEqualTo("foo\n")
  }

  @Test
  fun `should append line with custom new line`() {
    val sb = StringBuilder()
    sb.appendLine("foo", newLine = "\r\n")
    assertThat(sb.toString()).isEqualTo("foo\r\n")
  }
}

