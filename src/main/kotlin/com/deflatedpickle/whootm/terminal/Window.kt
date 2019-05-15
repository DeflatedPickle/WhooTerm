package com.deflatedpickle.whootm.terminal

import com.deflatedpickle.whootm.terminal.font.Font
import com.google.gson.Gson
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.opengl.GLCanvas
import org.eclipse.swt.opengl.GLData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import java.awt.Color

class Window : ApplicationWindow(null) {
    val theme: Theme

    lateinit var canvas: GLCanvas
    val glData = GLData()

    lateinit var terminalFont: Font

    val textBuffer = StringBuffer(1024)

    init {
        glData.doubleBuffer = true

        theme = Gson().fromJson(
            ClassLoader.getSystemResource("colours/blue.json").readText(),
            Theme::class.java
        )
    }

    override fun create() {
        super.create()

        shell.text = "owo"
    }

    override fun createContents(parent: Composite): Control {
        canvas = object : GLCanvas(parent, SWT.H_SCROLL or SWT.V_SCROLL, glData) {
            init {
                this.setCurrent()
                GL.createCapabilities()

                val background = Color.decode(theme.background)
                GL11.glClearColor(background.red / 255f, background.green / 255f, background.blue / 255f, 1.0f)

                this.addListener(SWT.Resize) {
                    this.setCurrent()

                    val area = this.clientArea

                    GL11.glViewport(0, 0, area.width, area.height)
                    GL11.glMatrixMode(GL11.GL_PROJECTION)
                    GL11.glLoadIdentity()
                    // GL11.glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0)
                }

                this.display.asyncExec(object : Runnable {
                    override fun run() {
                        if (!canvas.isDisposed) {
                            canvas.setCurrent()

                            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
                            GL11.glEnable(GL11.GL_TEXTURE_2D)

                            // TODO: Draw text
                            // GL11.glBindTexture(GL11.GL_TEXTURE_2D, terminalFont.fontAtlas.textureID)

                            // GL11.glPushMatrix()
                            // GL11.glBegin(GL11.GL_POLYGON);
                            // GL11.glVertex2i(-1, -1);
                            // GL11.glVertex2i(1, -1);
                            // GL11.glVertex2i(1, 1);
                            // GL11.glVertex2i(-1, 1);
                            // GL11.glEnd();
                            // GL11.glPopMatrix()

                            canvas.swapBuffers()
                            display.asyncExec(this)
                        }
                    }
                })
            }
        }

        terminalFont = Font(
            ClassLoader.getSystemResource("fonts/iosevka-regular.ttf").path.substring(1),
            14f
        )

        return super.createContents(parent)
    }
}