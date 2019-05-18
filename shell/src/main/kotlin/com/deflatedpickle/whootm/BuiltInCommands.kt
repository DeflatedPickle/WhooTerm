package com.deflatedpickle.whootm

import org.apache.commons.lang3.StringUtils
import org.fusesource.jansi.Ansi
import java.io.File

// TODO: Move these to a scripting language
enum class BuiltInCommands {
    CD {
        override fun execute(command: String, args: List<String>): Boolean {
            if (File(args[0]).absoluteFile.isDirectory) {
                System.setProperties(System.getProperties().apply { set("user.dir", args[0]) })
                return true
            }
            println(Ansi.ansi().fgRed().a("\"${args[0]}\" isn't a valid directory"))
            return false
        }
    },
    LS {
        override fun execute(command: String, args: List<String>): Boolean {
            val files = mutableListOf<File>()
            var longest = 0
            for (i in File(System.getProperty("user.dir")).listFiles()) {
                if (i.name.length > longest) {
                    longest = i.name.length
                }
                files.add(i)
            }

            for (i in files) {
                println("${i.name} ${StringUtils.repeat(".", longest + 1 - i.name.length)} [${if (i.isFile) "File" else "Directory"}]")
            }
            return true
        }
    },
    HELP {
        override fun execute(command: String, args: List<String>): Boolean {
            return true
        }
    },
    EXIT {
        override fun execute(command: String, args: List<String>): Boolean {
            GlobalValues.run = false
            return true
        }
    };

    abstract fun execute(command: String, args: List<String>): Boolean
}