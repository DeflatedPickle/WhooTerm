package com.deflatedpickle.whootm

import org.apache.commons.lang3.StringUtils
import org.fusesource.jansi.Ansi
import java.io.File

// TODO: Move these to a scripting language
enum class BuiltInCommands {
    CD {
        override fun execute(args: List<String>): Boolean {
            if (args.isNotEmpty()) {
                if (File(args.joinToString(" ")).absoluteFile.isDirectory) {
                    System.setProperties(System.getProperties().apply { set("user.dir", args.joinToString(" ")) })
                    return true
                }
                Logger.error("\"${args[0]}\" isn't a valid directory")
                return false
            }
            Logger.error("No directory was specified")
            return false
        }
    },
    LS {
        override fun execute(args: List<String>): Boolean {
            val files = mutableListOf<File>()
            var longest = 0
            for (i in File(System.getProperty("user.dir")).listFiles()) {
                if (i.name.length > longest) {
                    longest = i.name.length
                }
                files.add(i)
            }

            for (i in files) {
                Logger.info("${i.name} ${StringUtils.repeat(".", longest + 1 - i.name.length)} [${if (i.isFile) "File" else "Directory"}]")
            }
            return true
        }
    },
    HELP {
        override fun execute(args: List<String>): Boolean {
            return true
        }
    },
    EXIT {
        override fun execute(args: List<String>): Boolean {
            GlobalValues.run = false
            return true
        }
    };

    abstract fun execute(args: List<String>): Boolean
}