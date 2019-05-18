package com.deflatedpickle.whootm

import com.deflatedpickle.whootm.gui.Window

fun main() {
    val shellThread = ShellThread()
    Thread(shellThread).start()

    val window = Window()
    window.setBlockOnOpen(true)
    window.open()
}