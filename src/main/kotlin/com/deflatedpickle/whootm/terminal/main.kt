package com.deflatedpickle.whootm.terminal

import com.deflatedpickle.whootm.terminal.gui.Window

fun main() {
    val shellThread = ShellThread()
    Thread(shellThread).start()

    val window = Window()
    window.setBlockOnOpen(true)
    window.open()
}