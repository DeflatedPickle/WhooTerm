package com.deflatedpickle.whootm.gui

import com.deflatedpickle.whootm.ShellThread
import com.deflatedpickle.whootm.Theme
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display

class TerminalText(parent: Composite, theme: Theme) :
    TerminalReceiver {
    val text = StyledText(parent, SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL)

    init {
        text.font = Font(Display.getDefault(), "Courier", 12, SWT.NORMAL)

        val background = java.awt.Color.decode(theme.background)
        text.background = Color(Display.getDefault(), background.red, background.green, background.blue)

        val foreground = java.awt.Color.decode(theme.foreground)
        text.foreground = Color(Display.getDefault(), foreground.red, foreground.green, foreground.blue)

        text.addListener(SWT.KeyDown) {
            it.doit = text.caretOffset >= editableIndex[0]

            if (it.doit) {
                if (it.keyCode != 13) {
                    // textBuffer.append(it.character)
                    commandBuffer.append(it.character)
                }
                else {
                    ShellThread.commands.enqueue(commandBuffer.toString())
                    commandBuffer.setLength(0)
                }
            }
        }

        Display.getDefault().asyncExec(object : Runnable {
            override fun run() {
                for (line in ShellThread.unreadLines.elements()) {
                    text.text += ShellThread.unreadLines.dequeue() + "\n"
                    text.setSelection(text.text.length - 1)
                }

                Display.getDefault().asyncExec(this)
            }
        })
    }
}