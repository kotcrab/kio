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

import com.google.common.io.CountingOutputStream
import com.google.common.io.LittleEndianDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FilterOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

class KioOutputStream(private val outputStream: OutputStream, littleEndian: Boolean = true) {
    constructor(file: File, littleEndian: Boolean = true) : this(FileOutputStream(file), littleEndian)

    private val counter = CountingOutputStream(outputStream)
    private val stream: FilterOutputStream = if (littleEndian) {
        LittleEndianDataOutputStream(counter)
    } else {
        DataOutputStream(counter)
    }
    private val output = stream as DataOutput

    fun writeByte(value: Byte) {
        output.write(byteArrayOf(value))
    }

    fun writeShort(value: Short) {
        output.writeShort(value.toInt())
    }

    fun writeInt(value: Int) {
        output.writeInt(value)
    }

    fun writeLong(value: Long) {
        output.writeLong(value)
    }

    fun writeFloat(value: Float) {
        output.writeFloat(value)
    }

    fun writeDouble(value: Double) {
        output.writeDouble(value)
    }

    fun writeNullTerminatedString(string: String, charset: Charset = Charsets.UTF_8) {
        output.write(string.toByteArray(charset))
        output.writeByte(0)
    }

    fun writeDoubleNullTerminatedString(string: String, charset: Charset) {
        output.write(string.toByteArray(charset))
        output.writeByte(0)
        output.writeByte(0)
    }

    fun writeString(string: String, length: Int = string.length, charset: Charset = Charsets.UTF_8) {
        val bytes = string.toByteArray(charset)
        output.write(bytes)
        writeNullBytes(length - bytes.size)
    }

    fun writeNullBytes(count: Int) {
        output.write(ByteArray(count))
    }

    fun writeBytes(bytes: ByteArray) {
        output.write(bytes)
    }

    fun align(pad: Long) {
        if (longPos() % pad == 0L) return
        val targetCount = (longPos() / pad + 1) * pad
        writeNullBytes((targetCount - longPos()).toInt())
    }

    fun longPos(): Long {
        return counter.count
    }

    fun pos(): Int {
        if (longPos() > Integer.MAX_VALUE) error("can't safely convert pos to int, use longPos")
        return longPos().toInt()
    }

    fun close() {
        stream.close()
    }

    fun getAsByteArrayOutputStream(): ByteArrayOutputStream {
        return outputStream as ByteArrayOutputStream
    }
}
