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

package kio

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SequentialArrayWriterTest {
  @Test
  fun `should write bytes`() {
    val bytes = ByteArray(4)
    val seq = SequentialArrayWriter(bytes)
    assertThat(seq.pos).isEqualTo(0)
    assertThat(seq.size).isEqualTo(bytes.size)
    seq.write(0)
    seq.write(1)
    seq.write(2)
    seq.write(3)
    assertThat(seq[0]).isEqualTo(0)
    assertThat(seq.pos).isEqualTo(bytes.size)
    assertThat(bytes).containsExactly(0, 1, 2, 3)
  }

  @Test
  fun `should write multiple bytes`() {
    val bytes = ByteArray(4)
    val seq = SequentialArrayWriter(bytes)
    assertThat(seq.pos).isEqualTo(0)
    assertThat(seq.size).isEqualTo(bytes.size)
    seq.write(byteArrayOf(0, 1, 2, 3))
    assertThat(seq[0]).isEqualTo(0)
    assertThat(seq.pos).isEqualTo(bytes.size)
    assertThat(bytes).containsExactly(0, 1, 2, 3)
  }
}
