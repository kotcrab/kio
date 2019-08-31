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
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test

/** @author Kotcrab */

class NumberUtilsTest {
    @Test
    fun `should convert boolean to int`() {
        assertThat(false.toInt()).isEqualTo(0)
        assertThat(true.toInt()).isEqualTo(1)
    }

    @Test
    fun `should convert byte to unsigned int`() {
        assertThat(0x1.toByte().toUnsignedInt()).isEqualTo(0x1)
        assertThat(0xFF.toByte().toUnsignedInt()).isEqualTo(0xFF)
    }

    @Test
    fun `should convert short to unsigned int`() {
        assertThat(0x1.toShort().toUnsignedInt()).isEqualTo(0x1)
        assertThat(0xFFFF.toShort().toUnsignedInt()).isEqualTo(0xFFFF)
    }

    @Test
    fun `should convert int to unsigned long`() {
        assertThat(0x1.toUnsignedLong()).isEqualTo(0x1)
        assertThat(0xFFFFFFFF.toInt().toUnsignedLong()).isEqualTo(0xFFFFFFFF)
    }

    @Test
    fun `should convert byte to wide hex`() {
        assertThat(0x0.toByte().toWHex()).isEqualTo("00")
        assertThat(0x42.toByte().toWHex()).isEqualTo("42")
    }

    @Test
    fun `should convert short to wide hex`() {
        assertThat(0x0.toShort().toWHex()).isEqualTo("0000")
        assertThat(0x4242.toShort().toWHex()).isEqualTo("4242")
    }

    @Test
    fun `should convert int to wide hex`() {
        assertThat(0x0.toWHex()).isEqualTo("00000000")
        assertThat(0x42424242.toWHex()).isEqualTo("42424242")
    }

    @Test
    fun `should convert long to wide hex`() {
        assertThat(0x0.toLong().toWHex()).isEqualTo("0000000000000000")
        assertThat(0x4242424242424242.toWHex()).isEqualTo("4242424242424242")
    }

    @Test
    fun `should convert int to hex`() {
        assertThat(0x0.toHex()).isEqualTo("0x0")
        assertThat((-1).toHex()).isEqualTo("0xFFFFFFFF")
        assertThat(0x42424242.toHex()).isEqualTo("0x42424242")
    }

    @Test
    fun `should convert int to signed hex`() {
        assertThat(0x0.toSignedHex()).isEqualTo("0x0")
        assertThat((-1).toSignedHex()).isEqualTo("-0x1")
        assertThat(0x42424242.toSignedHex()).isEqualTo("0x42424242")
    }

    @Test
    fun `should check if bit in byte set`() {
        assertThat(0b10000000.toByte().isBitSet(7)).isTrue()
        assertThat(0b10000000.toByte().isBitSet(0)).isFalse()
        assertThat(0b00000001.toByte().isBitSet(0)).isTrue()
        assertThat(0b00000001.toByte().isBitSet(7)).isFalse()
        assertThat(0b00001000.toByte().isBitSet(3)).isTrue()
    }

    @Test
    fun `should throw exception when checked set bit in byte out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().isBitSet(8) }
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().isBitSet(-1) }
    }

    @Test
    fun `should set bit in byte`() {
        assertThat(0b00000000.toByte().setBit(0)).isEqualTo(0b00000001.toByte())
        assertThat(0b00000001.toByte().setBit(0)).isEqualTo(0b00000001.toByte())
        assertThat(0b00000000.toByte().setBit(7)).isEqualTo(0b10000000.toByte())
        assertThat(0b10000000.toByte().setBit(7)).isEqualTo(0b10000000.toByte())
        assertThat(0b00000000.toByte().setBit(3)).isEqualTo(0b00001000.toByte())
    }

    @Test
    fun `should throw exception when set bit in byte out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().setBit(8) }
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().setBit(-1) }
    }

    @Test
    fun `should toggle bit in byte`() {
        assertThat(0b00000000.toByte().toggleBit(0)).isEqualTo(0b00000001.toByte())
        assertThat(0b00000001.toByte().toggleBit(0)).isEqualTo(0b00000000.toByte())
        assertThat(0b00000000.toByte().toggleBit(7)).isEqualTo(0b10000000.toByte())
        assertThat(0b10000000.toByte().toggleBit(7)).isEqualTo(0b00000000.toByte())
        assertThat(0b00001000.toByte().toggleBit(3)).isEqualTo(0b00000000.toByte())
    }

    @Test
    fun `should throw exception when toggle bit in byte out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().toggleBit(8) }
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().toggleBit(-1) }
    }

    @Test
    fun `should reset bit in byte`() {
        assertThat(0b00000000.toByte().resetBit(0)).isEqualTo(0b00000000.toByte())
        assertThat(0b00000001.toByte().resetBit(0)).isEqualTo(0b00000000.toByte())
        assertThat(0b00000000.toByte().resetBit(7)).isEqualTo(0b00000000.toByte())
        assertThat(0b10000000.toByte().resetBit(7)).isEqualTo(0b00000000.toByte())
        assertThat(0b00001000.toByte().resetBit(3)).isEqualTo(0b00000000.toByte())
    }

    @Test
    fun `should throw exception when reset bit in byte out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().resetBit(8) }
        assertThatIllegalArgumentException().isThrownBy { 0.toByte().resetBit(-1) }
    }

    @Test
    fun `should check if bit in int set`() {
        assertThat((1 shl 31).isBitSet(31)).isTrue()
        assertThat(0.isBitSet(0)).isFalse()
        assertThat(1.isBitSet(0)).isTrue()
        assertThat(1.isBitSet(31)).isFalse()
        assertThat(4.isBitSet(2)).isTrue()
    }

    @Test
    fun `should throw exception when checked set bit in int out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.isBitSet(32) }
        assertThatIllegalArgumentException().isThrownBy { 0.isBitSet(-1) }
    }

    @Test
    fun `should set bit in int`() {
        assertThat(0.setBit(31)).isEqualTo((1 shl 31))
        assertThat((1 shl 31).setBit(31)).isEqualTo((1 shl 31))
        assertThat(0.setBit(0)).isEqualTo(1)
        assertThat(1.setBit(0)).isEqualTo(1)
        assertThat(0.setBit(3)).isEqualTo(8)
    }

    @Test
    fun `should throw exception when set bit in int out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.setBit(32) }
        assertThatIllegalArgumentException().isThrownBy { 0.setBit(-1) }
    }

    @Test
    fun `should toggle bit in int`() {
        assertThat(0.toggleBit(31)).isEqualTo((1 shl 31))
        assertThat((1 shl 31).toggleBit(31)).isEqualTo(0)
        assertThat(0.toggleBit(0)).isEqualTo(1)
        assertThat(1.toggleBit(0)).isEqualTo(0)
        assertThat(0.toggleBit(3)).isEqualTo(8)
    }


    @Test
    fun `should throw exception when toggle bit in int out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.toggleBit(32) }
        assertThatIllegalArgumentException().isThrownBy { 0.toggleBit(-1) }
    }

    @Test
    fun `should reset bit in int`() {
        assertThat(0.resetBit(31)).isEqualTo(0)
        assertThat((1 shl 31).resetBit(31)).isEqualTo(0)
        assertThat(0.resetBit(0)).isEqualTo(0)
        assertThat(1.resetBit(0)).isEqualTo(0)
        assertThat(0b11.resetBit(1)).isEqualTo(1)
    }

    @Test
    fun `should throw exception when reset bit in int out of range`() {
        assertThatIllegalArgumentException().isThrownBy { 0.resetBit(32) }
        assertThatIllegalArgumentException().isThrownBy { 0.resetBit(-1) }
    }

    @Test
    fun `should convert byte to bits array`() {
        assertThat(0b10001110.toByte().getBits()).containsExactly(false, true, true, true, false, false, false, true)
        assertThat(0b00000000.toByte().getBits()).containsExactly(
            false, false, false, false, false, false, false, false
        )
        assertThat(0b11111111.toByte().getBits()).containsExactly(true, true, true, true, true, true, true, true)
    }

    @Test
    fun `should get low int bits`() {
        assertThat(0x11223344.lowBits()).isEqualTo(0x3344)
        assertThat(0.lowBits()).isEqualTo(0)
        assertThat(0xFFFFFFFF.toInt().lowBits()).isEqualTo(0xFFFF)
    }

    @Test
    fun `should get high int bits`() {
        assertThat(0x11223344.highBits()).isEqualTo(0x1122)
        assertThat(0.highBits()).isEqualTo(0)
        assertThat(0xFFFFFFFF.toInt().highBits()).isEqualTo(0xFFFF)
    }

    @Test
    fun `should swap int bytes`() {
        assertThat(0x11223344.swapBytes()).isEqualTo(0x44332211)
        assertThat(0.swapBytes()).isEqualTo(0)
        assertThat(0xFFFFFFFF.toInt().swapBytes()).isEqualTo(0xFFFFFFFF.toInt())
    }

    @Test
    fun `should map values`() {
        assertThat(mapValue(5f, 0f, 10f, 50f, 100f)).isEqualTo(75f)
        assertThat(mapValue(-5f, -10f, 0f, -100f, -50f)).isEqualTo(-75f)
    }
}
