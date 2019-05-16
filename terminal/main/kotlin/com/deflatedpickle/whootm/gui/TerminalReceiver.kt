package com.deflatedpickle.whootm.gui

import org.eclipse.swt.graphics.Point

interface TerminalReceiver {
    val textBuffer: StringBuffer

    val commandBuffer: StringBuffer

    val editableIndex: IntArray

    var currentStatementIndex: Int
    var previousCommandIndex: Int
    var skimCommandIndex: Int
}