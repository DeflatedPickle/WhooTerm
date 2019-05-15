package com.deflatedpickle.whootm.terminal.gui

import com.deflatedpickle.whootm.terminal.ShellThread
import com.deflatedpickle.whootm.terminal.TerminalUtil
import com.deflatedpickle.whootm.terminal.Theme
import com.deflatedpickle.whootm.terminal.font.Font
import com.google.gson.Gson
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.events.ShellAdapter
import org.eclipse.swt.events.ShellEvent
import org.eclipse.swt.events.ShellListener
import org.eclipse.swt.opengl.GLData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control

class Window : ApplicationWindow(null) {
    val theme: Theme

    val glData = GLData()

    lateinit var terminalFont: Font

    init {
        glData.doubleBuffer = true

        theme = Gson().fromJson(
            ClassLoader.getSystemResource("colours/red.json").readText(),
            Theme::class.java
        )
    }

    override fun create() {
        super.create()

        shell.text = "WhooTerm"
        shell.setSize(700, 400)

        shell.addShellListener(object : ShellAdapter() {
            override fun shellClosed(e: ShellEvent) {
                ShellThread.run = false
            }
        })
    }

    override fun createContents(parent: Composite): Control {
        // val canvas = TerminalCanvas(parent, theme)
        val text = TerminalText(parent, theme)
        TerminalUtil.terminalReciever = text

        // terminalFont = Font(
        //     ClassLoader.getSystemResource("fonts/iosevka-regular.ttf").path.substring(1),
        //     14f
        // )

        return super.createContents(parent)
    }
}