package com.deflatedpickle.whootm.gui

interface TerminalReceiver {
    val textBuffer: StringBuffer
        get() = StringBuffer(1024)

    val commandBuffer: StringBuffer
        get() = StringBuffer(1024 / 2)

    val editableIndex: IntArray
        get() = IntArray(1)
}