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

import kio.LERandomAccessFile
import java.io.ByteArrayOutputStream
import java.io.DataInput
import java.io.RandomAccessFile
import java.nio.charset.Charset

fun RandomAccessFile.align(pad: Long) = align(pad, length(), ::write)

fun LERandomAccessFile.align(pad: Long) = align(pad, length(), ::write)

private inline fun align(pad: Long, length: Long, write: (ByteArray) -> Unit) {
  if (length % pad == 0L) {
    return
  }
  val targetCount = (length / pad + 1) * pad
  val writeBytes = targetCount - length
  if (writeBytes > Int.MAX_VALUE) {
    error("Too big write size")
  }
  write(ByteArray(writeBytes.toInt()))
}

fun RandomAccessFile.readString(size: Int, charset: Charset = Charsets.UTF_8): String {
  return readBytes(size).toString(charset)
}

fun LERandomAccessFile.readString(size: Int, charset: Charset = Charsets.UTF_8): String {
  return readBytes(size).toString(charset)
}

fun RandomAccessFile.writeString(string: String, charset: Charset = Charsets.UTF_8) {
  write(string.toByteArray(charset))
}

fun LERandomAccessFile.writeString(string: String, charset: Charset = Charsets.UTF_8) {
  write(string.toByteArray(charset))
}

fun RandomAccessFile.readNullTerminatedString(charset: Charset = Charsets.US_ASCII) = readNullTerminatedString(charset, ::readByte)

fun LERandomAccessFile.readNullTerminatedString(charset: Charset = Charsets.US_ASCII) = readNullTerminatedString(charset, ::readByte)

fun DataInput.readNullTerminatedString(charset: Charset = Charsets.US_ASCII) = readNullTerminatedString(charset, ::readByte)

private inline fun readNullTerminatedString(charset: Charset, readByte: () -> Byte): String {
  val out = ByteArrayOutputStream()
  while (true) {
    val byte = readByte().toInt()
    if (byte == 0) break
    out.write(byte)
  }
  return String(out.toByteArray(), charset)
}

fun RandomAccessFile.writeNullTerminatedString(string: String, charset: Charset = Charsets.UTF_8) {
  writeString(string, charset)
  writeByte(0)
}

fun LERandomAccessFile.writeNullTerminatedString(string: String, charset: Charset = Charsets.UTF_8) {
  writeString(string, charset)
  writeByte(0)
}

fun RandomAccessFile.readBytes(n: Int): ByteArray {
  val bytes = ByteArray(n)
  read(bytes)
  return bytes
}

fun LERandomAccessFile.readBytes(n: Int): ByteArray {
  val bytes = ByteArray(n)
  read(bytes)
  return bytes
}

fun RandomAccessFile.skipNullBytes() {
  skipByte(0)
}

fun LERandomAccessFile.skipNullBytes() {
  skipByte(0)
}

fun RandomAccessFile.skipByte(byteToSkip: Int) = skipByte(byteToSkip, { filePointer }, ::length, ::readByte, ::seek)

fun LERandomAccessFile.skipByte(byteToSkip: Int) = skipByte(byteToSkip, { filePointer }, ::length, ::readByte, ::seek)

private inline fun skipByte(
  byteToSkip: Int,
  filePointer: () -> Long,
  length: () -> Long,
  readByte: () -> Byte,
  seek: (Long) -> Unit,
) {
  while (true) {
    if (filePointer() == length()) return
    val byte = readByte().toUnsignedInt()
    if (byte != byteToSkip) {
      seek(filePointer() - 1L)
      return
    }
  }
}

fun <T> RandomAccessFile.temporaryJump(addr: Int, reader: (RandomAccessFile) -> T): T {
  return temporaryJump(addr.toLong(), reader)
}

fun <T> LERandomAccessFile.temporaryJump(addr: Int, reader: (LERandomAccessFile) -> T): T {
  return temporaryJump(addr.toLong(), reader)
}

fun <T> RandomAccessFile.temporaryJump(addr: Long, reader: (RandomAccessFile) -> T): T =
  temporaryJump(addr, reader, this, { filePointer }, ::seek)

fun <T> LERandomAccessFile.temporaryJump(addr: Long, reader: (LERandomAccessFile) -> T): T =
  temporaryJump(addr, reader, this, { filePointer }, ::seek)

private inline fun <T, R> temporaryJump(
  addr: Long,
  reader: (T) -> R,
  raf: T,
  filePointer: () -> Long,
  seek: (Long) -> Unit,
): R {
  val lastPos = filePointer()
  seek(addr)
  val result = reader(raf)
  seek(lastPos)
  return result
}

fun RandomAccessFile.seek(pos: Int) {
  seek(pos.toLong())
}

fun LERandomAccessFile.seek(pos: Int) {
  seek(pos.toLong())
}

fun RandomAccessFile.getSubArrayPos(needle: ByteArray, startFrom: Int = 0) = getSubArrayPos(needle, startFrom, ::read, ::seek, ::length)

fun LERandomAccessFile.getSubArrayPos(needle: ByteArray, startFrom: Int = 0) = getSubArrayPos(needle, startFrom, ::read, ::seek, ::length)

private inline fun getSubArrayPos(
  needle: ByteArray,
  startFrom: Int = 0,
  read: () -> Int,
  seek: (Long) -> Unit,
  length: () -> Long,
): Long {
  seek(0)
  val needleInts = needle.map { it.toUnsignedInt() }
  outer@ for (i in startFrom..length() - needle.size) {
    for (j in needle.indices) {
      seek(i + j)
      if (read() != needleInts[j]) {
        continue@outer
      }
    }
    return i
  }
  return -1
}
