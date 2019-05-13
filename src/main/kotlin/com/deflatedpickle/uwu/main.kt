package com.deflatedpickle.uwu

import me.xdrop.fuzzywuzzy.FuzzySearch
import java.io.BufferedReader
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.io.InputStreamReader

fun main() {
    while (true) {
        // TODO: Add an option for the welcome message
        // TODO: Add an option for the prompt
        print("\$ ")

        // TODO: Add pipes
        val command = readLine()!!.trim().split(" ").toTypedArray()

        try {
            val process = Runtime.getRuntime().exec(command)
            println(BufferedReader(InputStreamReader(process.inputStream)).readLines().joinToString("\n"))
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
            println("\"${command.joinToString()}\" is not a registered command, but these similar ones are; ${suggestions.joinToString(", ")}")
        }
    }
}