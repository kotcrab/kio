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

package kio

import kio.KioInputStream
import kio.KioOutputStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

/** @author Kotcrab */

internal class KioStreamTest {
    private val sink = PipedInputStream()
    private val output = KioOutputStream(PipedOutputStream(sink), true)
    private val input = KioInputStream(sink, Long.MAX_VALUE, true)
    private val testBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)

    @Test
    fun `should handle byte`() {
        output.writeByte(41)
        assertThat(input.readByte()).isEqualTo(41)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle max byte`() {
        output.writeByte(Byte.MAX_VALUE)
        assertThat(input.readByte()).isEqualTo(Byte.MAX_VALUE)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle byte at`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.readByte(at = 1)).isNotZero()
        assertThat(stream.pos()).isZero()
    }

    @Test
    fun `should handle short`() {
        output.writeShort(41)
        assertThat(input.readShort()).isEqualTo(41)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle max short`() {
        output.writeShort(Short.MAX_VALUE)
        assertThat(input.readShort()).isEqualTo(Short.MAX_VALUE)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle short at`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.readShort(at = 1)).isNotZero()
        assertThat(stream.pos()).isZero()
    }

    @Test
    fun `should handle int`() {
        output.writeInt(41)
        assertThat(input.readInt()).isEqualTo(41)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle max int`() {
        output.writeInt(Integer.MAX_VALUE)
        assertThat(input.readInt()).isEqualTo(Integer.MAX_VALUE)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle int at`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.readInt(at = 1)).isNotZero()
        assertThat(stream.pos()).isZero()
    }

    @Test
    fun `should handle long`() {
        output.writeLong(41)
        assertThat(input.readLong()).isEqualTo(41)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle max long`() {
        output.writeLong(Long.MAX_VALUE)
        assertThat(input.readLong()).isEqualTo(Long.MAX_VALUE)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle long at`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.readLong(at = 1)).isNotZero()
        assertThat(stream.pos()).isZero()
    }

    @Test
    fun `should handle float`() {
        val pi = Math.PI.toFloat()
        output.writeFloat(pi)
        assertThat(input.readFloat()).isEqualTo(pi)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle max float`() {
        output.writeFloat(Float.MAX_VALUE)
        assertThat(input.readFloat()).isEqualTo(Float.MAX_VALUE)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle float at`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.readFloat(at = 1)).isNotZero()
        assertThat(stream.pos()).isZero()
    }

    @Test
    fun `should handle double`() {
        output.writeDouble(Math.PI)
        assertThat(input.readDouble()).isEqualTo(Math.PI)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle max double`() {
        output.writeDouble(Double.MAX_VALUE)
        assertThat(input.readDouble()).isEqualTo(Double.MAX_VALUE)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle double at`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.readDouble(at = 1)).isNotZero()
        assertThat(stream.pos()).isZero()
    }

    @Test
    fun `should handle empty string`() {
        output.writeNullTerminatedString("")
        assertThat(input.readNullTerminatedString()).isEmpty()
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle unicode string`() {
        val str = "test 日本語"
        output.writeNullTerminatedString(str)
        assertThat(input.readNullTerminatedString()).isEqualTo(str)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle fixed len string`() {
        val str = "test"
        output.writeString(str)
        assertThat(input.readString(4)).isEqualTo(str)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle fixed len string and trim`() {
        val str = "test\u0000\u0000"
        output.writeString(str)
        assertThat(input.readStringAndTrim(6)).isEqualTo("test")
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle double null terminated string`() {
        output.writeDoubleNullTerminatedString("test", Charsets.UTF_16LE)
        assertThat(input.readDoubleNullTerminatedString(Charsets.UTF_16LE)).isEqualTo("test")
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should read bytes as hex string`() {
        output.writeBytes(byteArrayOf(0x41, 0x42, 0x43, 0x44))
        assertThat(input.readBytesAsHexString(4)).isEqualTo("41424344")
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should handle byte array`() {
        val bytes = byteArrayOf(0x41, 0x42, 0x43, 0x44)
        val bytesOut = ByteArray(bytes.size)
        output.writeBytes(bytes)
        input.readBytes(bytesOut)
        assertThat(bytesOut).containsExactly(*bytes)
        assertThat(sink.available()).isEqualTo(0)
    }

    @Test
    fun `should align input stream`() {
        val stream = KioInputStream(testBytes)
        stream.skip(1)
        stream.align(4)
        assertThat(stream.pos()).isEqualTo(4)
    }

    @Test
    fun `should align output stream`() {
        val stream = KioOutputStream(ByteArrayOutputStream())
        stream.writeByte(0)
        stream.align(4)
        assertThat(stream.pos()).isEqualTo(4)
    }

    @Test
    fun `should do temp jump`() {
        val stream = KioInputStream(testBytes)
        stream.temporaryJump(4) {}
        assertThat(stream.pos()).isEqualTo(0)
    }

    @Test
    fun `should skip bytes`() {
        val stream = KioInputStream(testBytes)
        stream.skip(4)
        assertThat(stream.pos()).isEqualTo(4)
    }


    @Test
    fun `should skip specified byte`() {
        val stream = KioInputStream(testBytes)
        stream.skipByte(0)
        assertThat(stream.pos()).isEqualTo(1)
    }

    @Test
    fun `should return eof state`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.eof()).isFalse()
        stream.skip(testBytes.size)
        assertThat(stream.eof()).isTrue()
    }

    @Test
    fun `should set pos`() {
        val stream = KioInputStream(testBytes)
        assertThat(stream.pos()).isZero()
        stream.setPos(4)
        assertThat(stream.pos()).isEqualTo(4)
    }
}
