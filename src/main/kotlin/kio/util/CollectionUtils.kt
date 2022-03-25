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

import java.util.*

fun padArray(src: ByteArray, pad: Int = 16): ByteArray {
  if (src.size % pad == 0) return src
  val targetSize = (src.size / pad + 1) * pad
  val dest = ByteArray(targetSize)
  arrayCopy(src = src, dest = dest)
  return dest
}

fun arrayCopy(src: ByteArray, srcPos: Int = 0, dest: ByteArray, destPos: Int = 0, length: Int = src.size) {
  System.arraycopy(src, srcPos, dest, destPos, length)
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

fun <T> MutableList<T>.swap(element1: T, element2: T) {
  swap(indexOf(element1), indexOf(element2))
}

fun <T> MutableList<T>.swap(idx1: Int, idx2: Int) {
  Collections.swap(this, idx1, idx2)
}
