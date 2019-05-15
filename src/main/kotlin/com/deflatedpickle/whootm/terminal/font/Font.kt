package com.deflatedpickle.whootm.terminal.font

import com.mlomb.freetypejni.Face
import com.mlomb.freetypejni.FreeType
import com.mlomb.freetypejni.FreeTypeConstants
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import kotlin.math.max

class Font(val fontPath: String, val fontSize: Float) {
    val fontFace: Face = FreeTypeUtil.library.newFace(fontPath, 0)
    val fontAtlas: FontAtlas

    init {
        fontFace.setPixelSizes(0f, fontSize)
        fontAtlas = FontAtlas(fontFace)
    }
}