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

fun Boolean.toInt(): Int = if (this) 1 else 0

fun Byte.toUnsignedInt() = (this.toInt() and 0xFF)
fun Short.toUnsignedInt() = (this.toInt() and 0xFFFF)
fun Int.toUnsignedLong() = (this.toLong() and 0xFFFFFFFF)

fun Byte.toWHex() = String.format("%02X", this)
fun Short.toWHex() = String.format("%04X", this)
fun Int.toWHex() = String.format("%08X", this)
fun Long.toWHex() = String.format("%016X", this)

fun Int.toHex() = String.format("0x%X", this)
fun Int.toSignedHex(): String {
  return if (this >= 0) {
    String.format("0x%X", this)
  } else {
    String.format("-0x%X", -this)
  }
}

fun Byte.isBitSet(bit: Int): Boolean {
  checkInRange(bit, 0, 8)
  return (this.toUnsignedInt()) and (1 shl bit) != 0
}

fun Byte.setBit(bit: Int): Byte {
  checkInRange(bit, 0, 8)
  return (this.toUnsignedInt() or (1 shl bit)).toByte()
}

fun Byte.toggleBit(bit: Int): Byte {
  checkInRange(bit, 0, 8)
  return (this.toUnsignedInt() xor (1 shl bit)).toByte()
}

fun Byte.resetBit(bit: Int): Byte {
  checkInRange(bit, 0, 8)
  return (this.toUnsignedInt() and (1 shl bit).inv()).toByte()
}

fun Int.isBitSet(bit: Int): Boolean {
  checkInRange(bit, 0, 32)
  return this and (1 shl bit) != 0
}

fun Int.setBit(bit: Int): Int {
  checkInRange(bit, 0, 32)
  return this or (1 shl bit)
}

fun Int.toggleBit(bit: Int): Int {
  checkInRange(bit, 0, 32)
  return this xor (1 shl bit)
}

fun Int.resetBit(bit: Int): Int {
  checkInRange(bit, 0, 32)
  return this and (1 shl bit).inv()
}

private fun checkInRange(bit: Int, min: Int, max: Int) {
  if (bit < min || bit >= max) {
    throw IllegalArgumentException("Out of range, bit must be >=${min} and <${max}")
  }
}

fun Byte.getBits(): BooleanArray {
  val bits = BooleanArray(8)
  for (bit in 0..7) {
    bits[bit] = this.isBitSet(bit)
  }
  return bits
}

fun Int.lowBits(): Int = this and 0xFFFF
fun Int.highBits(): Int = this ushr 16
fun Int.swapBytes(): Int {
  return (this and 0xFF shl 24) or
    (this and 0xFF00 shl 8) or
    (this and 0xFF0000 ushr 8) or
    (this and 0xFF000000.toInt() ushr 24)
}

fun mapValue(value: Float, fromStart: Float, fromEnd: Float, toStart: Float, toEnd: Float): Float {
  return (value - fromStart) / (fromEnd - fromStart) * (toEnd - toStart) + toStart
}
