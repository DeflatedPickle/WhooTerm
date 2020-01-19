package com.deflatedpickle.whootm

import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.asciitable.CWC_LongestLine
import de.vandermeer.asciithemes.u8.U8_Grids
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import org.apache.commons.io.FileUtils
import org.apache.tika.Tika
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat

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
            Logger.error("No directory has been specified")
            return false
        }
    },
    LS {
        override fun execute(args: List<String>): Boolean {
            val files = mutableListOf<File>()
            var longest = 0

            for (i in userFiles) {
                if (i.name.length > longest) {
                    longest = i.name.length
                }
                files.add(i)
            }

            AsciiTable().apply {
                addRow("Name", "Size", "Type", "Last Modified", "Created").apply {
                    setTextAlignment(TextAlignment.CENTER)
                }
                addStrongRule()

                for (i in files) {
                    val attributes = Files.readAttributes(i.toPath(), BasicFileAttributes::class.java)

                    addRow(
                        i.name,
                        FileUtils.byteCountToDisplaySize(i.length().toInt()),
                        if (i.isFile) tika.detect(i) else "directory",
                        dateFormat.format(i.lastModified()),
                        dateFormat.format(attributes.creationTime().toMillis())
                    )
                    addRule()
                }

                renderer.cwc = CWC_LongestLine()
                context.grid = U8_Grids.borderStrongDoubleLight()
                println(render())
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
            Shell.run = false
            return true
        }
    };

    abstract fun execute(args: List<String>): Boolean

    companion object {
        val tika = Tika()

        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

        val userFiles = File(System.getProperty("user.dir")).listFiles()
    }
}