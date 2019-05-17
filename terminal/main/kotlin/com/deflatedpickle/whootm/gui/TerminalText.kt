package com.deflatedpickle.whootm.gui

import com.deflatedpickle.whootm.ShellThread
import com.deflatedpickle.whootm.Theme
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import java.io.File
import java.awt.Font as AWTFont

class TerminalText(
    parent: Composite, theme: Theme,
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
    var pastPrompt = false

    init {
        val fontFile = File(ClassLoader.getSystemResource("fonts/FantasqueSansMono-Regular.ttf").path)
        val fontName = AWTFont.createFont(AWTFont.TRUETYPE_FONT, fontFile).deriveFont(12).name
        Display.getDefault().loadFont(fontFile.absolutePath)
        text.font = Font(Display.getDefault(), fontName, 12, SWT.NORMAL)

        val background = java.awt.Color.decode(theme.background)
        text.background = Color(Display.getDefault(), background.red, background.green, background.blue)

        val foreground = java.awt.Color.decode(theme.foreground)
        text.foreground = Color(Display.getDefault(), foreground.red, foreground.green, foreground.blue)

        text.alwaysShowScrollBars = false

        text.addListener(SWT.Dispose) {
            text.font.dispose()
            text.background.dispose()
            text.foreground.dispose()
        }

        text.addCaretListener {
            if (setStatementIndex) {
                currentStatementIndex = text.selection.x
                setStatementIndex = false
            }
        }

        text.addListener(SWT.KeyDown) {
            when (it.keyCode) {
                SWT.CR.toInt() -> {
                    ShellThread.commandQueue.add(commandBuffer.toString())
                    commandBuffer.setLength(0)
                    previousCommandIndex = ShellThread.previousCommandStack.size
                    skimCommandIndex = previousCommandIndex
                }
                SWT.ARROW_RIGHT -> {
                }
                SWT.ARROW_LEFT -> {
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
                        if (skimCommandIndex + 1 < ShellThread.previousCommandStack.size) {
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
                    // Deletes the character from the buffer, where it is relative to the caret
                    if (commandBuffer.isNotEmpty()) {
                        commandBuffer.deleteCharAt(text.selection.x - currentStatementIndex + 1)
                    }
                }
                else -> {
                    // Inserts the character to the buffer, where it is relative to the caret
                    commandBuffer.insert(text.selection.x - currentStatementIndex, it.character)
                }
            }
        }

        text.addVerifyKeyListener {
            // Stops the user from editing text before the prompt
            // TODO: Check if the key is a backspace so the user can't edit the last character of the prompt
            if (pastPrompt) {
                it.doit = text.selection.x >= currentStatementIndex
            }
        }

        Display.getDefault().asyncExec(object : Runnable {
            override fun run() {
                for (line in ShellThread.unreadLineQueue.iterator()) {
                    val unreadLine = ShellThread.unreadLineQueue.take()

                    if (line.contains(ShellThread.textPrompt)) {
                        text.insert("\n" + unreadLine)
                    } else {
                        text.insert(unreadLine + "\n")
                    }

                    text.setSelection(text.text.length)
                    currentStatementIndex = text.text.length

                    if (line.contains(ShellThread.textPrompt)) {
                        setStatementIndex = true
                        pastPrompt = true
                    }
                }

                Display.getDefault().asyncExec(this)
            }
        })
    }
}