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

        val shell = Runtime.getRuntime().exec(shellName)

        val shellIn = shell.outputStream.bufferedWriter()
        val shellOut = shell.inputStream.bufferedReader()
        val shellError = shell.errorStream.bufferedReader()

        val commands = LinkedBlockingQueue<String>()
    }

    override fun run() {
        while (run) {
            shellIn.write("echo --EOF--\n")
            shellIn.flush()

            for (i in commands.iterator()) {
                shellIn.write("${commands.take()}\n")
            }

            var line = shellOut.readLine()
            while (line != null && line.trim() != "--EOF--") {
                if (!line.contains("--EOF--") && line !in listOf("", " ", "\n")) {
                    unreadLines.add(line)
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