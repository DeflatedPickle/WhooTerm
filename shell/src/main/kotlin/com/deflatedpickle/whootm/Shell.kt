package com.deflatedpickle.whootm

import me.xdrop.fuzzywuzzy.FuzzySearch
import org.fusesource.jansi.AnsiConsole
import java.io.*

class Shell {
    // | Default Colours        |
    // | ---------------------- |
    // | Cyan ... = Information |
    // | Yellow . = Warning     |
    // | Red .... = Error       |

    companion object {
        var run = true

        var process: Process? = null

        fun prompt(): String {
            return "[${System.getProperty("user.dir")}] ~> "
        }
    }

    fun run() {
        AnsiConsole.systemInstall()

        while (run) {
            // TODO: Add an option for the welcome message
            // TODO: Add an option for the prompt
            Logger.prompt(prompt())

            // TODO: Add pipes
            // TODO: Add auto-completion (https://stackoverflow.com/questions/41212646/get-key-press-in-windows-console)
            val input = readLine()!!.trim().split(" ").toTypedArray()
            val command = input[0]
            val args = input.slice(1 until input.size)

            // Check if it's a built-in command
            try {
                BuiltInCommands.valueOf(command.toUpperCase()).execute(args)
            }
            // It wasn't
            catch (e: IllegalArgumentException) {
                // Try to run the command
                try {
                    process = Runtime.getRuntime().exec(input)

                    val inputStream = BufferedReader(InputStreamReader(process!!.inputStream)).readLines().joinToString("\n")
                    val errorStream = BufferedReader(InputStreamReader(process!!.errorStream)).readLines().joinToString("\n")

                    if (inputStream != "") {
                        Logger.info(inputStream)
                    } else {
                        Logger.error(errorStream)
                    }

                    process!!.waitFor()
                }
                // It failed/doesn't exist
                catch (e: IOException) {
                    val suggestions = mutableListOf<String>()

                    // Loop all programs listed on the PATH
                    for (dir in System.getenv("PATH").split(";")) {
                        val dirFile = File(dir)
                        if (dirFile.isDirectory) {
                            for (file in dirFile.listFiles { _, name -> name.split(".").last() in arrayOf("exe", "bat") }) {
                                if (FuzzySearch.partialRatio(command, file.nameWithoutExtension) > 80) {
                                    suggestions.add(file.name)
                                }
                            }
                        }
                    }

                    // TODO: Add an option for the error message
                    Logger.warn("\"$command ${args.joinToString(" ")}\" is not a registered command${if (suggestions.isNotEmpty()) ", but these are; ${suggestions.joinToString(", ")}" else ""}")
                }
            }
        }
    }
}