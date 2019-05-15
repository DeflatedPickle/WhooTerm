package com.deflatedpickle.whootm.gui

interface TerminalReceiver {
    val textBuffer: StringBuffer

    val commandBuffer: StringBuffer

    val editableIndex: IntArray
}