package com.deflatedpickle.whootm.terminal

class ShellThread : Runnable {
    companion object {
        val shellName = "cmd"
        var run = true

        val shell = Runtime.getRuntime().exec(shellName)

        val shellIn = shell.outputStream.bufferedWriter()
        val shellOut = shell.inputStream.bufferedReader()
        val shellError = shell.errorStream.bufferedReader()
    }

    override fun run() {
        while (run){
            shellIn.write("echo --EOF--\n")
            shellIn.flush()

            var line = shellOut.readLine()
            while (line != null && line.trim() != "--EOF--") {
                if (!line.contains("--EOF--") && line !in listOf("", " ", "\n")) {
                    println(line)
                }
                line = shellOut.readLine()
            }
            if (line == null) {
                break
            }
        }
    }
}