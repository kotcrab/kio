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

import com.google.common.io.ByteStreams
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.InputStream
import java.io.PrintStream
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*

/** @author Kotcrab */

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

fun execute(
    executable: File, args: Array<Any> = arrayOf(), workingDirectory: File? = null, exitValue: Int = 0,
    streamHandler: PumpStreamHandler? = null
) {
    execute(CommandLine(executable.absolutePath), args, workingDirectory, exitValue, streamHandler)
}

fun execute(
    executable: String, args: Array<Any> = arrayOf(), workingDirectory: File? = null, exitValue: Int = 0,
    streamHandler: PumpStreamHandler? = null
) {
    execute(CommandLine(executable), args, workingDirectory, exitValue, streamHandler)
}

private fun execute(
    cmdLine: CommandLine, args: Array<Any> = arrayOf(), workingDirectory: File? = null, exitValue: Int = 0,
    streamHandler: PumpStreamHandler? = null
) {
    args.forEachIndexed { index, _ ->
        cmdLine.addArgument("\${arg$index}")
    }
    val map = mutableMapOf<String, Any>()
    args.forEachIndexed { index, arg ->
        map["arg$index"] = arg
    }
    cmdLine.substitutionMap = map
    val executor = DefaultExecutor()
    if (workingDirectory != null) executor.workingDirectory = workingDirectory
    if (streamHandler != null) executor.streamHandler = streamHandler
    executor.setExitValue(exitValue)
    executor.execute(cmdLine)
}

@Suppress("UnstableApiUsage")
fun nullStreamHandler() = PumpStreamHandler(ByteStreams.nullOutputStream(), ByteStreams.nullOutputStream())

fun stdoutStreamHandler() = PumpStreamHandler(stdout, stdout)

fun getJarPath(caller: Class<*>): String {
    val url = caller.protectionDomain.codeSource.location
    var path = URLDecoder.decode(url.file, "UTF-8")
    // remove jar name from path and cut first '/' when on Windows
    path = if (System.getProperty("os.name").toLowerCase().contains("win")) {
        path.substring(1, path.lastIndexOf('/'))
    } else {
        path.substring(0, path.lastIndexOf('/'))
    }
    path = path.replace("/", File.separator)
    return path + File.separator
}

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

fun <T> MutableList<T>.swap(element1: T, element2: T) {
    swap(indexOf(element1), indexOf(element2))
}

fun <T> MutableList<T>.swap(idx1: Int, idx2: Int) {
    Collections.swap(this, idx1, idx2)
}

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
