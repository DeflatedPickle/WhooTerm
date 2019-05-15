package com.deflatedpickle.whootm.shell

import me.xdrop.fuzzywuzzy.FuzzySearch
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import java.io.*

fun main() {
    while (true) {
        AnsiConsole.systemInstall()
        // TODO: Add an option for the welcome message
        // TODO: Add an option for the prompt
        print(Ansi.ansi().fgGreen().a("\$").fgDefault().a(" "))

        // TODO: Add options for colours

        // TODO: Add pipes
        // TODO: Add auto-completion (https://stackoverflow.com/questions/41212646/get-key-press-in-windows-console)
        val command = readLine()!!.trim().split(" ").toTypedArray()

        try {
            val process = Runtime.getRuntime().exec(command)

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
            val suggestions = mutableListOf<String>()

            for (dir in System.getenv("PATH").split(";")) {
                val dirFile = File(dir)
                if (dirFile.isDirectory) {
                    for (file in dirFile.listFiles(FilenameFilter { dir, name -> name.endsWith("exe") })) {
                        if (FuzzySearch.partialRatio(command.first(), file.nameWithoutExtension) > 80) {
                            suggestions.add(file.name)
                        }
                    }
                }
            }

            // TODO: Add an option for the error message
            println(Ansi.ansi().fgYellow().a("\"${command.joinToString()}\" is not a registered command${if (suggestions.isNotEmpty()) ", but these are; ${suggestions.joinToString(", ")}" else ""}"))
        }
    }
}