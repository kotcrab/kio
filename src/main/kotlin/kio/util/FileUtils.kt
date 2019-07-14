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

import kio.LERandomAccessFile
import java.io.ByteArrayOutputStream
import java.io.DataInput
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.nio.file.Paths
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.log10
import kotlin.math.pow


/** @author Kotcrab */

fun File.child(name: String): File {
    return File(this, name)
}

fun File.toRelativeNixPath(base: File): String {
    return relativizePath(base).replace("\\", "/")
}

fun File.relativizePath(base: File): String {
    val pathAbsolute = Paths.get(this.absolutePath.toString())
    val pathBase = Paths.get(base.absolutePath.toString())
    val pathRelative = pathBase.relativize(pathAbsolute)
    var path = pathRelative.toString().replace("\\", "/")
    if (this.isDirectory) path += "/"
    return path
}

fun walkDir(dir: File, processFile: (File) -> Unit, errorHandler: (File, Exception) -> Unit = { _, e -> throw(e) }) {
    dir.listFiles()!!.forEach {
        if (it.isFile) {
            try {
                processFile(it)
            } catch (e: Exception) {
                errorHandler(it, e)
            }
        } else {
            walkDir(it, processFile, errorHandler)
        }
    }
}

fun RandomAccessFile.align(pad: Long) {
    if (length() % pad == 0L) return
    val targetCount = (length() / pad + 1) * pad
    write(ByteArray((targetCount - length()).toInt()))
}

fun LERandomAccessFile.align(pad: Long) {
    if (length() % pad == 0L) return
    val targetCount = (length() / pad + 1) * pad
    write(ByteArray((targetCount - length()).toInt()))
}

private fun RandomAccessFile.readString(size: Int, charset: Charset = Charsets.UTF_8): String {
    return readBytes(size).toString(charset)
}

private fun LERandomAccessFile.readString(size: Int, charset: Charset = Charsets.UTF_8): String {
    return readBytes(size).toString(charset)
}

private fun RandomAccessFile.writeString(string: String, charset: Charset = Charsets.UTF_8) {
    write(string.toByteArray(charset))
}

private fun LERandomAccessFile.writeString(string: String, charset: Charset = Charsets.UTF_8) {
    write(string.toByteArray(charset))
}

fun RandomAccessFile.readNullTerminatedString(charset: Charset = Charsets.US_ASCII): String {
    val out = ByteArrayOutputStream()
    while (true) {
        val byte = readByte().toInt()
        if (byte == 0) break
        out.write(byte)
    }
    return String(out.toByteArray(), charset)
}

fun LERandomAccessFile.readNullTerminatedString(charset: Charset = Charsets.US_ASCII): String {
    val out = ByteArrayOutputStream()
    while (true) {
        val byte = readByte().toInt()
        if (byte == 0) break
        out.write(byte)
    }
    return String(out.toByteArray(), charset)
}

private fun RandomAccessFile.writeNullTerminatedString(string: String, charset: Charset = Charsets.UTF_8) {
    writeString(string, charset)
    writeByte(0)
}

private fun LERandomAccessFile.writeNullTerminatedString(string: String, charset: Charset = Charsets.UTF_8) {
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

fun RandomAccessFile.skipByte(byteToSkip: Int) {
    while (true) {
        if (filePointer == length()) return
        val byte = readByte().toUnsignedInt()
        if (byte != byteToSkip) {
            seek(filePointer - 1L)
            return
        }
    }
}

fun LERandomAccessFile.skipByte(byteToSkip: Int) {
    while (true) {
        if (filePointer == length()) return
        val byte = readByte().toUnsignedInt()
        if (byte != byteToSkip) {
            seek(filePointer - 1L)
            return
        }
    }
}

fun DataInput.readNullTerminatedString(charset: Charset = Charsets.US_ASCII): String {
    val out = ByteArrayOutputStream()
    while (true) {
        val byte = readByte().toInt()
        if (byte == 0) break
        out.write(byte)
    }
    return String(out.toByteArray(), charset)
}

fun RandomAccessFile.temporaryJump(addr: Int, reader: (RandomAccessFile) -> Unit) {
    val lastPos = filePointer
    seek(addr)
    reader(this)
    seek(lastPos)
}

fun LERandomAccessFile.temporaryJump(addr: Int, reader: (LERandomAccessFile) -> Unit) {
    val lastPos = filePointer
    seek(addr)
    reader(this)
    seek(lastPos)
}

fun RandomAccessFile.seek(pos: Int) {
    seek(pos.toLong())
}

fun LERandomAccessFile.seek(pos: Int) {
    seek(pos.toLong())
}

fun getSubArrayPos(data: ByteArray, needle: ByteArray, startFrom: Int = 0): Int {
    outer@ for (i in startFrom..data.size - needle.size) {
        for (j in needle.indices) {
            if (data[i + j] != needle[j]) {
                continue@outer
            }
        }
        return i
    }
    return -1
}

fun RandomAccessFile.getSubArrayPos(needle: ByteArray, startFrom: Int = 0): Long {
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

fun LERandomAccessFile.getSubArrayPos(needle: ByteArray, startFrom: Int = 0): Long {
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

fun readableFileSize(size: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    if (size == 0L) return "0 B"
    if (size < 0L) throw IllegalArgumentException("size can't be <0")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    val format = DecimalFormat("###0.#", DecimalFormatSymbols(Locale.US))
    return format.format(size / 1024.0.pow(digitGroups.toDouble()))
        .replace(",", ".") + " " + units[digitGroups]
}
