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
import java.io.InputStream
import java.io.PrintStream
import java.net.URLDecoder
import java.nio.charset.Charset

private val windows932Charset = Charset.forName("windows-932")
private val shiftJisCharset = Charset.forName("Shift_JIS")

val Charsets.WINDOWS_932: Charset
  get() = windows932Charset

@Deprecated(
  level = DeprecationLevel.WARNING,
  message = "Prefer using Charsets.WINDOWS_932 to properly support IBM code page 932",
  replaceWith = ReplaceWith("Charsets.WINDOWS_932", "kio.util.WINDOWS_932")
)
val Charsets.SHIFT_JIS: Charset
  get() = shiftJisCharset


fun StringBuilder.appendLine(text: String = "", newLine: String = "\n") {
  append(text)
  append(newLine)
}

var stdin: InputStream
  get() = System.`in`
  set(s) {
    System.setIn(s)
  }

var stdout: PrintStream
  get() = System.out
  set(s) {
    System.setOut(s)
  }

var stderr: PrintStream
  get() = System.err
  set(s) {
    System.setErr(s)
  }

fun getJarPath(caller: Class<*>): String {
  val url = caller.protectionDomain.codeSource.location
  var path = URLDecoder.decode(url.file, "UTF-8")
  // remove jar name from path and cut first '/' when on Windows
  path = if (System.getProperty("os.name").lowercase().contains("win")) {
    path.substring(1, path.lastIndexOf('/'))
  } else {
    path.substring(0, path.lastIndexOf('/'))
  }
  path = path.replace("/", File.separator)
  return path + File.separator
}
