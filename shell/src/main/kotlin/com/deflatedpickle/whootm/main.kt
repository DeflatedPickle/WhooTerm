package com.deflatedpickle.whootm

import me.xdrop.fuzzywuzzy.FuzzySearch
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import java.io.*

fun main() {
    while (GlobalValues.run) {
        AnsiConsole.systemInstall()

        // | Default Colours        |
        // | ---------------------- |
        // | Blue ... = Information |
        // | Yellow . = Warning     |
        // | Red .... = Error       |

        // TODO: Add an option for the welcome message
        // TODO: Add an option for the prompt
        print(Ansi.ansi().fgGreen().a("[${System.getProperty("user.dir")}] ~>").fgDefault().a(""))

        // TODO: Add options for colours

        // TODO: Add pipes
        // TODO: Add auto-completion (https://stackoverflow.com/questions/41212646/get-key-press-in-windows-console)
        val input = readLine()!!.trim().split(" ").toTypedArray()
        val command = input[0]
        val args = input.slice(1 until input.size)

        try {
            val process = Runtime.getRuntime().exec(input)

            val inputStream = BufferedReader(InputStreamReader(process.inputStream)).readLines().joinToString("\n")
            val errorStream = BufferedReader(InputStreamReader(process.errorStream)).readLines().joinToString("\n")

            if (inputStream != "") {
                println(Ansi.ansi().fgCyan().a(inputStream))
            }
            else {
                println(Ansi.ansi().fgRed().a(errorStream))
            }

            process.waitFor()
        }
        catch (e : IOException) {
            try {
                BuiltInCommands.valueOf(command.toUpperCase()).execute(command, args)
            }
            catch (e : IllegalArgumentException) {
                val suggestions = mutableListOf<String>()

                for (dir in System.getenv("PATH").split(";")) {
                    val dirFile = File(dir)
                    if (dirFile.isDirectory) {
                        for (file in dirFile.listFiles(FilenameFilter { _, name -> name.endsWith("exe") })) {
                            if (FuzzySearch.partialRatio(command, file.nameWithoutExtension) > 80) {
                                suggestions.add(file.name)
                            }
                        }
                    }
                }

                // TODO: Add an option for the error message
                println(Ansi.ansi().fgYellow().a("\"$command ${args.joinToString(" ")}\" is not a registered command${if (suggestions.isNotEmpty()) ", but these are; ${suggestions.joinToString(", ")}" else ""}"))
            }
        }
    }
}