package org.meteordev.juno.api.text;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.meteordev.juno.api.JunoProvider;
import org.meteordev.juno.api.texture.Filter;
import org.meteordev.juno.api.texture.Format;
import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.api.texture.Wrap;

import java.nio.ByteBuffer;

public class Font {
    public final Texture texture;
    public final int height;

    private final Glyph[] glyphs = new Glyph[128];

    public Font(FontInfo fontInfo, int height) {
        this.height = height;

        // Allocate STBTTPackedchar buffer
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(glyphs.length);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(2048 * 2048);

        // Create font texture
        STBTTPackContext packContext = STBTTPackContext.create();
        STBTruetype.stbtt_PackBegin(packContext, bitmap, 2048, 2048, 0, 1);
        STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2);
        STBTruetype.stbtt_PackFontRange(packContext, fontInfo.buffer, 0, height, 32, cdata);
        STBTruetype.stbtt_PackEnd(packContext);
        fontInfo.buffer.rewind();

        // Create texture object and get font scale
        texture = JunoProvider.get().createTexture(2048, 2048, Format.R, Filter.LINEAR, Filter.LINEAR, Wrap.CLAMP_TO_EDGE);
        texture.write(bitmap);

        //scale = STBTruetype.stbtt_ScaleForPixelHeight(info.fontInfo, height);

        // Populate charData array
        for (int i = 0; i < glyphs.length; i++) {
            STBTTPackedchar packedChar = cdata.get(i);

            float ipw = 1f / 2048;
            float iph = 1f / 2048;

            glyphs[i] = new Glyph(
                    packedChar.xoff(),
                    packedChar.yoff(),
                    packedChar.xoff2(),
                    packedChar.yoff2(),
                    packedChar.x0() * ipw,
                    packedChar.y0() * iph,
                    packedChar.x1() * ipw,
                    packedChar.y1() * iph,
                    packedChar.xadvance()
            );
        }
    }

    public Glyph getGlyph(char c) {
        if (c < 32 || c > 128) c = 32;
        return glyphs[c];
    }
}
