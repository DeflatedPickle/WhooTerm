package com.deflatedpickle.whootm.terminal.gui

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Text

interface TerminalReceiver {
    val textBuffer: StringBuffer
        get() = StringBuffer(1024)

    val commandBuffer: StringBuffer
        get() = StringBuffer(1024 / 2)

    val editableIndex: IntArray
        get() = IntArray(1)
}