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

class BitInputStreamTest {
    private val testBytes = byteArrayOf(0b01100001, 0b01111111, 0b00000000, 0b01101000)

    @Test
    fun `should read bits in msb order`() {
        val bis = BitInputStream(testBytes, msbOrder = true)
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.pos).isEqualTo(1)
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.posInCurrentByte).isEqualTo(1)
    }

    @Test
    fun `should read bits in lsb order`() {
        val bis = BitInputStream(testBytes, msbOrder = false)
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.readBit()).isFalse()
        assertThat(bis.pos).isEqualTo(1)
        assertThat(bis.readBit()).isTrue()
        assertThat(bis.posInCurrentByte).isEqualTo(1)
    }

    @Test
    fun `should read byte`() {
        val bis = BitInputStream(testBytes)
        assertThat(bis.readByte()).isEqualTo(97)
        assertThat(bis.pos).isEqualTo(1)
        assertThat(bis.readByte()).isEqualTo(127)
        assertThat(bis.pos).isEqualTo(2)
    }

    @Test
    fun `should read int`() {
        val bis = BitInputStream(testBytes)
        assertThat(bis.readInt()).isEqualTo(0x617F0068)
        assertThat(bis.pos).isEqualTo(4)
        assertThat(bis.eof).isTrue()
    }
}
