package com.deflatedpickle.whootm.terminal.font

import com.mlomb.freetypejni.Face
import com.mlomb.freetypejni.FreeTypeConstants
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import kotlin.math.max

class FontAtlas(val fontFace: Face) {
    val textureID = GL11.glGenTextures()

    var width = 0
    var height = 0

    val imageAtlas = mutableMapOf<Char, Glyph>()

    var exportFont = true

    init {
        // Credit: https://en.wikibooks.org/wiki/OpenGL_Programming/Modern_OpenGL_Tutorial_Text_Rendering_02
        val glyph = fontFace.glyphSlot

        for (i in 32..255) {
            if (fontFace.loadChar(i.toChar(), FreeTypeConstants.FT_LOAD_RENDER)) {
                width += glyph.bitmap.width
                height += max(height, glyph.bitmap.rows)
            }
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_ALPHA,
            width,
            height,
            0,
            GL11.GL_ALPHA,
            GL11.GL_UNSIGNED_BYTE,
            0
        )

        var x = 0
        for (i in 32..255) {
            if (fontFace.loadChar(i.toChar(), FreeTypeConstants.FT_LOAD_RENDER)) {
                GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    x,
                    0,
                    glyph.bitmap.width,
                    glyph.bitmap.rows,
                    GL11.GL_ALPHA,
                    GL11.GL_UNSIGNED_BYTE,
                    glyph.bitmap.buffer
                )

                imageAtlas[i.toChar()] = Glyph(i.toChar(), fontFace.getCharIndex(i),
                    (glyph.advance.x.toBigInteger() shr 6).toFloat(), (glyph.advance.y shr 6).toFloat(),
                    glyph.bitmap.width.toFloat(), glyph.bitmap.rows.toFloat(),
                    glyph.bitmapLeft.toFloat(), glyph.bitmapTop.toFloat(),
                    x.toFloat() / width
                )

                x += glyph.bitmap.width
            }
        }
    }
}