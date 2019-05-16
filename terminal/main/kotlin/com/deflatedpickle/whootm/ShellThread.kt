package com.deflatedpickle.whootm

import org.apache.commons.lang3.SystemUtils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class ShellThread : Runnable {
    companion object {
        val shellName = if (SystemUtils.IS_OS_WINDOWS) {
            "cmd"
        }
        else if (SystemUtils.IS_OS_LINUX) {
            "bash"
        }
        else {
            ""
        }
        var run = true

        var lineCount = AtomicInteger(0)
        var unreadLines = LinkedBlockingQueue<String>()
        var previousCommand = ""

        val shell = Runtime.getRuntime().exec(shellName)

        val shellIn = shell.outputStream.bufferedWriter()
        val shellOut = shell.inputStream.bufferedReader()
        val shellError = shell.errorStream.bufferedReader()

        val commands = LinkedBlockingQueue<String>()
    }

    override fun run() {
        while (run) {
            // Constantly writes to the shell, keeping it responsive
            shellIn.write("echo --EOF--\n")
            shellIn.flush()

            // Runs through the commands sent from the terminal
            for (i in commands.iterator()) {
                val command = commands.take()

                if (command != "") {
                    shellIn.write("$command\n")
                    // Causes another prompt to show
                    shellIn.write("\n")
                    shellIn.flush()
                    previousCommand = command
                }
                else {
                    shellIn.write("\n")
                }
            }

            // Constantly reads from the shell, ignoring the constant writes
            var line = shellOut.readLine()
            while (line != null && line.trim() != "--EOF--") {
                if (!line.contains("--EOF--") && line !in listOf("", " ", "\n")) {
                    // Makes sure the welcome text is shown
                    if (previousCommand == "") {
                        unreadLines.add(line)
                        lineCount.set(lineCount.get() + 1)
                    }
                    else {
                        // Stops from sending the prompt and command
                        if (!line.contains(previousCommand)) {
                            unreadLines.add(line)
                            lineCount.set(lineCount.get() + 1)
                        }
                    }
                }
                line = shellOut.readLine()
            }
            if (line == null) {
                break
            }
        }
    }
}