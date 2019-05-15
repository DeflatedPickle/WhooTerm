package com.deflatedpickle.whootm.terminal

import sun.misc.Queue
import java.util.concurrent.atomic.AtomicInteger

class ShellThread : Runnable {
    companion object {
        val shellName = "cmd"
        var run = true

        var lineCount = AtomicInteger(0)
        var unreadLines = Queue<String>()

        val shell = Runtime.getRuntime().exec(shellName)

        val shellIn = shell.outputStream.bufferedWriter()
        val shellOut = shell.inputStream.bufferedReader()
        val shellError = shell.errorStream.bufferedReader()

        val commands = Queue<String>()
    }

    override fun run() {
        while (run) {
            shellIn.write("echo --EOF--\n")
            shellIn.flush()

            for (i in commands.elements()) {
                shellIn.write("${commands.dequeue()}\n")
            }

            var line = shellOut.readLine()
            while (line != null && line.trim() != "--EOF--") {
                if (!line.contains("--EOF--") && line !in listOf("", " ", "\n")) {
                    unreadLines.enqueue(line)
                    lineCount.set(lineCount.get() + 1)
                }
                line = shellOut.readLine()
            }
            if (line == null) {
                break
            }
        }
    }
}