package com.deflatedpickle.whootm

import org.apache.commons.lang3.SystemUtils
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class ShellThread : Runnable {
    companion object {
        val shellName = if (SystemUtils.IS_OS_WINDOWS) {
            "cmd" // "powershell"
        }
        else if (SystemUtils.IS_OS_LINUX) {
            "bash"
        }
        else {
            ""
        }
        var run = true

        var needPromptSet = true
        var textPrompt = ""

        var lineCount = AtomicInteger(0)
        var unreadLineQueue = LinkedBlockingQueue<String>()
        var previousCommandStack = Stack<String>()

        val shell = Runtime.getRuntime().exec(shellName)

        val shellIn = shell.outputStream.bufferedWriter()
        val shellOut = shell.inputStream.bufferedReader()
        val shellError = shell.errorStream.bufferedReader()

        val commandQueue = LinkedBlockingQueue<String>()
    }

    override fun run() {
        while (run) {
            // Constantly writes to the shell, keeping it responsive
            shellIn.write("echo --EOF--\n")
            shellIn.flush()

            // Runs through the commands sent from the terminal
            for (i in commandQueue.iterator()) {
                val command = commandQueue.take()

                if (command != "") {
                    shellIn.write("$command\n")
                    // Causes another prompt to show
                    shellIn.write("\n")
                    shellIn.flush()
                    previousCommandStack.push(command)
                }
                else {
                    shellIn.write("\n")
                }
            }

            // Constantly reads from the shell, ignoring the constant writes
            var line = shellOut.readLine()
            while (line != null && line.trim() != "--EOF--") {
                // Retrieve the text prompt
                if (needPromptSet) {
                    if (line.contains("echo --EOF--")) {
                        textPrompt = line.substringBeforeLast("echo --EOF--")
                        needPromptSet = false
                    }
                }

                if (!line.contains("--EOF--") && line !in listOf("", " ", "\n")) {
                    // Makes sure the welcome text is shown
                    if (previousCommandStack.isEmpty()) {
                        unreadLineQueue.add(line)
                        lineCount.set(lineCount.get() + 1)
                    }
                    else {
                        // Stops from sending the prompt and command
                        if (!line.contains(previousCommandStack.lastElement())) {
                            unreadLineQueue.add(line)
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