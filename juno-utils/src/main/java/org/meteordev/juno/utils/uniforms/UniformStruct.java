package org.meteordev.juno.utils.uniforms;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.meteordev.juno.api.pipeline.Shader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.ByteBuffer;

/**
 * Marks a class or a record as being able to be written out to a {@link ByteBuffer} using {@link Uniforms}.
 *
 *
 *
 * <h3>Allowed data types</h3>
 * Only some specific types can be used for fields of uniform structs alongside other classes annotated with this annotation.
 * Built-in types include:
 * <ul>
 *     <li>{@code float}</li>
 *     <li>{@code int}</li>
 *     <li>{@link Vector2f}</li>
 *     <li>{@link Vector3f}</li>
 *     <li>{@link Vector4f}</li>
 *     <li>{@link Matrix4f}</li>
 * </ul>
 *
 *
 *
 * <h3>GLSL Usage</h3>
 * <ul>
 *     <li>Directly accessing the data in a uniform block. See {@link Shader}.</li>
 *     <li>Defining a custom struct in the shader which corresponds to a single class in Java.</li>
 *     <li>Using a custom Java class instead of JOML's vector types.
 *         For this you need to change the {@link #alignment()} of the annotation since the alignment of structs doesn't apply to vectors.
 *         Here are some basic alignments: {@code vec2} - 8 bytes, {@code vec3} - 16 bytes, {@code vec4} - 16 bytes.</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniformStruct {
    /**
     * @return the alignment of this struct in bytes.
     *         Value of 0 means that the alignment will be calculated automatically based on the fields.
     */
    int alignment() default 0;
}
