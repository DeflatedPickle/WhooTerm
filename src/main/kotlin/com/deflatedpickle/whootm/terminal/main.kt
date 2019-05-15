package com.deflatedpickle.whootm.terminal

fun main() {
    val shellThread = ShellThread()
    Thread(shellThread).start()

    val window = Window()
    window.setBlockOnOpen(true)
    window.open()
}