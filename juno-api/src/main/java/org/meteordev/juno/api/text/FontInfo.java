package org.meteordev.juno.api.text;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class FontInfo {
    public final String name;
    public final FontType type;

    public final STBTTFontinfo stbttFontInfo;
    public final ByteBuffer buffer;

    public FontInfo(String name, FontType type, STBTTFontinfo stbttFontInfo, ByteBuffer buffer) {
        this.name = name;
        this.type = type;
        this.stbttFontInfo = stbttFontInfo;
        this.buffer = buffer;
    }

    public static FontInfo read(ByteBuffer buffer) {
        if (buffer.limit() < 5) return null;

        if (
            buffer.get(0) != 0 ||
            buffer.get(1) != 1 ||
            buffer.get(2) != 0 ||
            buffer.get(3) != 0 ||
            buffer.get(4) != 0
        ) return null;

        STBTTFontinfo stbttFontInfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(stbttFontInfo, buffer)) return null;

        ByteBuffer nameBuffer = STBTruetype.stbtt_GetFontNameString(stbttFontInfo, STBTruetype.STBTT_PLATFORM_ID_MICROSOFT, STBTruetype.STBTT_MS_EID_UNICODE_BMP, STBTruetype.STBTT_MS_LANG_ENGLISH, 1);
        ByteBuffer typeBuffer = STBTruetype.stbtt_GetFontNameString(stbttFontInfo, STBTruetype.STBTT_PLATFORM_ID_MICROSOFT, STBTruetype.STBTT_MS_EID_UNICODE_BMP, STBTruetype.STBTT_MS_LANG_ENGLISH, 2);
        if (typeBuffer == null || nameBuffer == null) return null;

        return new FontInfo(
                StandardCharsets.UTF_16.decode(nameBuffer).toString(),
                FontType.fromString(StandardCharsets.UTF_16.decode(typeBuffer).toString()),
                stbttFontInfo,
                buffer
        );
    }

    public static FontInfo read(InputStream in) {
        try {
            ByteArrayOutputStream btao = new ByteArrayOutputStream();
            byte[] bytes = new byte[4096];
            int read;

            while ((read = in.read(bytes)) != -1) {
                btao.write(bytes, 0, read);
            }

            ByteBuffer buffer = BufferUtils.createByteBuffer(btao.size());
            buffer.put(btao.toByteArray()).rewind();

            return read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
