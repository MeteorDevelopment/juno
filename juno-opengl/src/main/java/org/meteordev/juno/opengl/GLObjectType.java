package org.meteordev.juno.opengl;

import org.lwjgl.opengl.GL43C;

public enum GLObjectType {
    BUFFER(GL43C.GL_BUFFER),
    IMAGE(GL43C.GL_TEXTURE),
    PROGRAM(GL43C.GL_PROGRAM),
    SHADER(GL43C.GL_SHADER);

    public final int gl;

    GLObjectType(int gl) {
        this.gl = gl;
    }
}
