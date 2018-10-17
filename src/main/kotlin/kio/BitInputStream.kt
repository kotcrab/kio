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

import kio.util.isBitSet
import kio.util.toInt

/** @author Kotcrab */

class BitInputStream(private val bytes: ByteArray, private val msbOrder: Boolean = true) {
    private var currentByte = bytes[0]
    var posInCurrentByte = 0
        private set
    var pos = 0
        private set
    var eof = false
        private set

    fun readBit(): Boolean {
        if (eof) error("no more bytes to read")
        val value = currentByte.isBitSet(if (msbOrder) 7 - posInCurrentByte else posInCurrentByte)
        posInCurrentByte++
        if (posInCurrentByte == 8) {
            pos++
            posInCurrentByte = 0
            if (pos == bytes.size) {
                eof = true
            } else {
                currentByte = bytes[pos]
            }
        }
        return value
    }

    fun readByte(): Byte {
        var value = 0
        repeat(8) { it ->
            value = value or (readBit().toInt() shl (7 - it))
        }
        return value.toByte()
    }

    fun readInt(bits: Int = 32): Int {
        if (bits > 32) error("bits must be <=32")
        var value = 0
        repeat(bits) { it ->
            value = value or (readBit().toInt() shl (bits - 1 - it))
        }
        return value
    }
}
