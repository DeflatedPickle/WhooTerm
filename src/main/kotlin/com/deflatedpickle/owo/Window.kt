package com.deflatedpickle.owo

import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.opengl.GLCanvas
import org.eclipse.swt.opengl.GLData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control

class Window : ApplicationWindow(null) {
    val glData = GLData()

    override fun create() {
        super.create()

        shell.text = "owo"
    }

    override fun createContents(parent: Composite): Control {
        val canvas = GLCanvas(parent, SWT.H_SCROLL or SWT.V_SCROLL, GLData())

        canvas.addListener(SWT.Resize) {
            canvas.bounds = shell.bounds
        }

        return super.createContents(parent)
    }
}