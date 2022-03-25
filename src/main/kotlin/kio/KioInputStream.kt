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

import com.google.common.io.CountingInputStream
import com.google.common.io.LittleEndianDataInputStream
import kio.util.toUnsignedInt
import kio.util.toWHex
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FilterInputStream
import java.io.InputStream
import java.nio.charset.Charset

class KioInputStream(baseStream: InputStream, val size: Long, littleEndian: Boolean = true) {
  constructor(file: File, littleEndian: Boolean = true)
    : this(MarkableFileInputStream(FileInputStream(file)), file.length(), littleEndian)

  constructor(bytes: ByteArray, littleEndian: Boolean = true)
    : this(ByteArrayInputStream(bytes), bytes.size.toLong(), littleEndian)

  private val counter = CountingInputStream(baseStream)
  private val stream: FilterInputStream = if (littleEndian) {
    LittleEndianDataInputStream(counter)
  } else {
    DataInputStream(counter)
  }
  private val input: DataInput = stream as DataInput

  init {
    stream.mark(0)
  }

  fun readByte(): Byte {
    return input.readByte()
  }

  fun readByte(at: Int): Byte {
    val prevPos = longPos()
    setPos(at)
    val value = readByte()
    setPos(prevPos)
    return value
  }

  fun readShort(): Short {
    return input.readShort()
  }

  fun readShort(at: Int): Short {
    val prevPos = longPos()
    setPos(at)
    val value = readShort()
    setPos(prevPos)
    return value
  }

  fun readInt(): Int {
    return input.readInt()
  }

  fun readInt(at: Int): Int {
    val prevPos = longPos()
    setPos(at)
    val value = readInt()
    setPos(prevPos)
    return value
  }

  fun readLong(): Long {
    return input.readLong()
  }

  fun readLong(at: Int): Long {
    val prevPos = longPos()
    setPos(at)
    val value = readLong()
    setPos(prevPos)
    return value
  }

  fun readFloat(): Float {
    return input.readFloat()
  }

  fun readFloat(at: Int): Float {
    val prevPos = longPos()
    setPos(at)
    val value = readFloat()
    setPos(prevPos)
    return value
  }

  fun readDouble(): Double {
    return input.readDouble()
  }

  fun readDouble(at: Int): Double {
    val prevPos = longPos()
    setPos(at)
    val value = readDouble()
    setPos(prevPos)
    return value
  }

  fun readNullTerminatedString(charset: Charset = Charsets.UTF_8): String {
    val out = ByteArrayOutputStream()
    while (true) {
      val byte = readByte().toInt()
      if (byte == 0) break
      out.write(byte)
    }
    return String(out.toByteArray(), charset)
  }

  fun readDoubleNullTerminatedString(charset: Charset): String {
    val out = ByteArrayOutputStream()
    while (true) {
      val byte1 = readByte().toInt()
      val byte2 = readByte().toInt()
      if (byte1 == 0 && byte2 == 0) break
      out.write(byte1)
      out.write(byte2)
    }
    return String(out.toByteArray(), charset)
  }

  fun readString(length: Int, charset: Charset = Charsets.UTF_8): String {
    return String(readBytes(length), charset)
  }

  fun readStringAndTrim(length: Int, charset: Charset = Charsets.UTF_8): String {
    return readString(length, charset).replace("\u0000", "")
  }

  fun readBytesAsHexString(byteCount: Int): String {
    return readBytes(byteCount).joinToString(separator = "", transform = { it.toWHex() })
  }

  fun skipNullBytes() {
    skipByte(0)
  }

  fun readBytes(byteArray: ByteArray): ByteArray {
    stream.read(byteArray)
    return byteArray
  }

  fun readBytes(length: Int): ByteArray {
    return readBytes(ByteArray(length))
  }

  fun align(pad: Long) {
    if (longPos() % pad == 0L) return
    val absOffset = (longPos() / pad + 1) * pad
    setPos(absOffset)
  }

  fun <T> temporaryJump(addr: Int, reader: (KioInputStream) -> T): T {
    val lastPos = pos()
    setPos(addr)
    val result = reader(this)
    setPos(lastPos)
    return result
  }

  fun skip(n: Int) {
    input.skipBytes(n)
  }

  fun skipByte(byteToSkip: Int) {
    while (true) {
      if (eof()) return
      val byte = readByte().toUnsignedInt()
      if (byte != byteToSkip) {
        setPos(longPos() - 1L)
        return
      }
    }
  }

  fun longPos(): Long {
    return counter.count
  }

  fun pos(): Int {
    if (longPos() > Integer.MAX_VALUE) error("can't safely convert pos to int, use longPos")
    return longPos().toInt()
  }

  fun eof(): Boolean {
    return longPos() == size
  }

  fun close() {
    stream.close()
  }

  fun setPos(pos: Int) {
    setPos(pos.toLong())
  }

  fun setPos(pos: Long) {
    counter.reset()
    stream.reset()
    stream.skip(pos)
  }
}
