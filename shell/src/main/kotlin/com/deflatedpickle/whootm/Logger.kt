package com.deflatedpickle.whootm

import org.fusesource.jansi.Ansi

object Logger {
    fun prompt(string: String) {
        print(Ansi.ansi().fgGreen().a(string).reset())
    }

    fun info(string: String) {
        println(Ansi.ansi().fgCyan().a(string).reset())
    }

    fun warn(string: String) {
        println(Ansi.ansi().fgYellow().a(string).reset())
    }

    fun error(string: String) {
        println(Ansi.ansi().fgRed().a(string).reset())
    }
}