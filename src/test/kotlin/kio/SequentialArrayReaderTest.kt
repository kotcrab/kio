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

class SequentialArrayReaderTest {
  private val bytes = byteArrayOf(0, 1, 2, 3)

  @Test
  fun `should read bytes`() {
    val seq = SequentialArrayReader(bytes)
    assertThat(seq.pos).isEqualTo(0)
    assertThat(seq.size).isEqualTo(bytes.size)
    assertThat(seq.read()).isEqualTo(0)
    assertThat(seq.read()).isEqualTo(1)
    assertThat(seq.read()).isEqualTo(2)
    assertThat(seq.read()).isEqualTo(3)
    assertThat(seq.pos).isEqualTo(bytes.size)
  }

  @Test
  fun `should read multiple bytes`() {
    val seq = SequentialArrayReader(bytes)
    assertThat(seq.pos).isEqualTo(0)
    assertThat(seq.size).isEqualTo(bytes.size)
    assertThat(seq.read(bytes.size)).containsExactly(*bytes)
    assertThat(seq.pos).isEqualTo(bytes.size)
  }
}
