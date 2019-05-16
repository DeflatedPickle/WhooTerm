package com.deflatedpickle.whootm.gui

import com.deflatedpickle.whootm.ShellThread
import com.deflatedpickle.whootm.Theme
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display

class TerminalText(parent: Composite, theme: Theme,
                   override val textBuffer: StringBuffer = StringBuffer(1024),
                   override val commandBuffer: StringBuffer = StringBuffer(1024 / 2),
                   override val editableIndex: IntArray = IntArray(1),
                   override var currentStatementIndex: Int = 0,
                   override var previousCommandIndex: Int = -1,
                   override var skimCommandIndex: Int = -1
) :
    TerminalReceiver {
    val text = StyledText(parent, SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL)

    var setStatementIndex = false

    init {
        text.font = Font(Display.getDefault(), "Consolas", 12, SWT.NORMAL)

        val background = java.awt.Color.decode(theme.background)
        text.background = Color(Display.getDefault(), background.red, background.green, background.blue)

        val foreground = java.awt.Color.decode(theme.foreground)
        text.foreground = Color(Display.getDefault(), foreground.red, foreground.green, foreground.blue)

        text.addCaretListener {
            if (setStatementIndex) {
                currentStatementIndex = text.selection.y
                setStatementIndex = false
            }
        }

        text.addListener(SWT.KeyDown) {
            // TODO: Stop the user from editing text sent by the shell
            // it.doit = text.caretOffset >= editableIndex[0]

            when (it.keyCode) {
                SWT.CR.toInt() -> {
                    ShellThread.commandQueue.add(commandBuffer.toString())
                    commandBuffer.setLength(0)
                    previousCommandIndex = ShellThread.previousCommandStack.size
                    skimCommandIndex = previousCommandIndex
                }
                SWT.ARROW_UP -> {
                    text.setSelection(currentStatementIndex)

                    if (text.text.length > currentStatementIndex) {
                        if (skimCommandIndex - 1 > -1) {
                            skimCommandIndex--
                        }

                        text.replaceTextRange(currentStatementIndex, text.text.length - currentStatementIndex, "")
                    }

                    if (skimCommandIndex >= 0) {
                        if (ShellThread.previousCommandStack.isNotEmpty()) {
                            text.insert(ShellThread.previousCommandStack[skimCommandIndex])
                            text.setSelection(text.text.length)
                            commandBuffer.setLength(0)
                            commandBuffer.append(ShellThread.previousCommandStack[skimCommandIndex])
                        }
                    }
                }
                SWT.ARROW_DOWN -> {
                    text.setSelection(currentStatementIndex)

                    if (text.text.length > currentStatementIndex) {
                        if (skimCommandIndex + 1 < ShellThread.previousCommandStack.size ) {
                            skimCommandIndex++
                        }

                        text.replaceTextRange(currentStatementIndex, text.text.length - currentStatementIndex, "")
                    }

                    if (skimCommandIndex < ShellThread.previousCommandStack.size) {
                        if (ShellThread.previousCommandStack.isNotEmpty()) {
                            text.insert(ShellThread.previousCommandStack[skimCommandIndex])
                            text.setSelection(text.text.length)
                            commandBuffer.setLength(0)
                            commandBuffer.append(ShellThread.previousCommandStack[skimCommandIndex])
                        }
                    }
                }
                SWT.BS.toInt() -> {
                    // TODO: Get the character at the carets location, convert it's location to the command space and delete it from the buffer
                }
                else -> {
                    // TODO: Convert to the total text and caret to command space, then append the `it.character` at the caret's location to the buffer
                    commandBuffer.append(it.character)
                }
            }
        }

        Display.getDefault().asyncExec(object : Runnable {
            override fun run() {
                for (line in ShellThread.unreadLineQueue.iterator()) {
                    val unreadLine = ShellThread.unreadLineQueue.take()
                    text.insert(unreadLine + if (line.contains(ShellThread.textPrompt)) "" else "\n")
                    text.setSelection(text.text.length)
                    currentStatementIndex = text.text.length

                    if (line.contains(ShellThread.textPrompt)) {
                        setStatementIndex = true
                    }
                }

                Display.getDefault().asyncExec(this)
            }
        })
    }
}