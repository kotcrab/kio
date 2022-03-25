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

import com.google.common.io.ByteStreams
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.File

fun execute(
  executable: File,
  args: List<*> = emptyList<Any>(),
  environment: Map<String, String>? = null,
  workingDirectory: File? = null,
  exitValue: Int = 0,
  streamHandler: PumpStreamHandler? = null,
) {
  execute(CommandLine(executable.absolutePath), args, environment, workingDirectory, exitValue, streamHandler)
}

fun execute(
  executable: String,
  args: List<*> = emptyList<Any>(),
  environment: Map<String, String>? = null,
  workingDirectory: File? = null,
  exitValue: Int = 0,
  streamHandler: PumpStreamHandler? = null,
) {
  execute(CommandLine(executable), args, environment, workingDirectory, exitValue, streamHandler)
}

private fun execute(
  cmdLine: CommandLine,
  args: List<*> = emptyList<Any>(),
  environment: Map<String, String>? = null,
  workingDirectory: File? = null,
  exitValue: Int = 0,
  streamHandler: PumpStreamHandler? = null,
) {
  args.forEachIndexed { index, _ ->
    cmdLine.addArgument("\${arg$index}")
  }
  val map = mutableMapOf<String, Any?>()
  args.forEachIndexed { index, arg ->
    map["arg$index"] = arg
  }
  cmdLine.substitutionMap = map
  val executor = DefaultExecutor()
  if (workingDirectory != null) {
    executor.workingDirectory = workingDirectory
  }
  if (streamHandler != null) {
    executor.streamHandler = streamHandler
  }
  executor.setExitValue(exitValue)
  executor.execute(cmdLine, environment)
}

@Suppress("UnstableApiUsage")
fun nullStreamHandler() = PumpStreamHandler(ByteStreams.nullOutputStream(), ByteStreams.nullOutputStream())

fun stdoutStreamHandler() = PumpStreamHandler(stdout, stdout)
