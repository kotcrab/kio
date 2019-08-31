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
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.io.File

/** @author Kotcrab */

class FileUtilsTest {
    @Test
    fun `should create child file`() {
        assertThat(File("a", "b")).isEqualTo(File("a").child("b"))
    }

    @Test
    fun `should create relative nix path`() {
        assertThat(
            File("/a/b/c/d.bin").toRelativeNixPath(File("/a/b"))
        ).isEqualTo("c/d.bin")
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun `should create relative win path`() {
        assertThat(File("C:\\a\\b\\c.bin").relativizePath(File("C:\\a\\b"))).isEqualTo("c.bin")
    }

    @Test
    fun `should return sub array pos`() {
        val arr = byteArrayOf(0, 0, 0, 1, 2, 3, 0, 0, 3, 2)
        assertThat(getSubArrayPos(arr, byteArrayOf(1, 2, 3))).isEqualTo(3)
    }

    @Test
    fun `should return readable file size`() {
        assertThatIllegalArgumentException().isThrownBy { readableFileSize(-1) }
        assertThat(readableFileSize(0)).isEqualTo("0 B")
        assertThat(readableFileSize(1000)).isEqualTo("1000 B")
        assertThat(readableFileSize(1010)).isEqualTo("1010 B")
        assertThat(readableFileSize(1024)).isEqualTo("1 KB")
        assertThat(readableFileSize(1024 * 1024)).isEqualTo("1 MB")
        assertThat(readableFileSize(1024 * 1024 * 1024)).isEqualTo("1 GB")
        assertThat(readableFileSize(1024 + 512)).isEqualTo("1.5 KB")
    }
}
