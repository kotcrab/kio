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

import java.io.File
import java.nio.file.Paths
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun File.child(name: String): File {
  return File(this, name)
}

fun File.toRelativeNixPath(base: File): String {
  return relativizePath(base).replace("\\", "/")
}

fun File.relativizePath(base: File): String {
  val pathAbsolute = Paths.get(absolutePath.toString())
  val pathBase = Paths.get(base.absolutePath.toString())
  val pathRelative = pathBase.relativize(pathAbsolute)
  var path = pathRelative.toString().replace("\\", "/")
  if (isDirectory) {
    path += "/"
  }
  return path
}

fun walkDir(
  dir: File,
  errorHandler: (File, Exception) -> Unit = { _, e -> throw(e) },
  processFile: (File) -> Unit,
) {
  dir.listFiles()?.forEach {
    if (it.isFile) {
      try {
        processFile(it)
      } catch (e: Exception) {
        errorHandler(it, e)
      }
    } else {
      walkDir(it, errorHandler, processFile)
    }
  }
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
