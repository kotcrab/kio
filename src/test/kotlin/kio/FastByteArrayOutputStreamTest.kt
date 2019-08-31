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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/** @author Kotcrab */

class FastByteArrayOutputStreamTest {
    @Test
    fun `should return byte at pos`() {
        val baos = FastByteArrayOutputStream()
        baos.write(0x41)
        baos.write(0x42)
        baos.write(0x43)
        assertThat(baos.at(0)).isEqualTo(0x41)
        assertThat(baos.at(1)).isEqualTo(0x42)
        assertThat(baos.at(2)).isEqualTo(0x43)
    }

    @Test
    fun `should return byte count`() {
        val baos = FastByteArrayOutputStream(4)
        baos.write(0x41)
        baos.write(0x42)
        baos.write(0x43)
        assertThat(baos.count()).isEqualTo(3)
    }

    @Test
    fun `should return internal buf`() {
        val baos = FastByteArrayOutputStream(2)
        baos.write(0x41)
        baos.write(0x42)
        assertThat(baos.getInternalBuf()).containsExactly(0x41, 0x42)
    }

    @Test
    fun `should return byte array`() {
        val baos = FastByteArrayOutputStream(2)
        baos.write(0x41)
        baos.write(0x42)
        assertThat(baos.toByteArray()).containsExactly(0x41, 0x42)
    }

    @Test
    fun `should return internal buf when size is same as count`() {
        val baos = FastByteArrayOutputStream(2)
        baos.write(0x41)
        baos.write(0x42)
        assertThat(baos.toByteArray()).isEqualTo(baos.getInternalBuf())
    }

    @Test
    fun `should not return internal buf when size is different as count`() {
        val baos = FastByteArrayOutputStream(3)
        baos.write(0x41)
        baos.write(0x42)
        assertThat(baos.toByteArray()).isNotEqualTo(baos.getInternalBuf())
    }
}
