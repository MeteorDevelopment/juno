package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.Resource;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.sampler.Sampler;

import java.nio.ByteBuffer;

/**
 * Represents a compiled shader for use in a {@link GraphicsPipeline}.
 *
 *
 *
 * <h3>GLSL Version</h3>
 * Each GLSL shader should begin with a version directive.
 * While you can use any version you want, if you don't know which one to use then use the 330 version.
 * It corresponds to the OpenGL 3.3 version which Juno requires.
 * <pre>{@code
 * #version 330 core
 * }</pre>
 *
 *
 *
 * <h3>Uniform data</h3>
 * To access uniform data passed to a render pass using {@link RenderPass#setUniforms(ByteBuffer, int)}
 * you need to define the layout of the data in the shader. Here is an example:
 * <pre>{@code
 * // Uniform blocks can be unnamed,
 * layout (binding = 0) uniform Uniforms1 {
 *     mat4 u_Projection;
 *     float4 u_Color;
 * };
 *
 * // or named, which affects how you access the individual fields.
 * layout (binding = 1) uniform Uniforms2 {
 *     mat4 projection;
 *     float4 color;
 * } u;
 *
 * void main() {
 *     mat4 projection1 = u_Projection;
 *     float4 projection2 = u.projection;
 * }
 * }</pre>
 * The {@code binding} attribute in the layout portion of the code refers to the {@code slot} parameter in {@link RenderPass#setUniforms(ByteBuffer, int)}.
 *
 *
 *
 * <h3>Sampling images</h3>
 * To sample images bound to a render pass using {@link RenderPass#bindImage(Image, Sampler, int)}
 * you need to specify them as uniforms in the shader. Here is an example:
 * <pre>{@code
 * layout (binding = 0) uniform sampler2D u_Texture;
 *
 * void main() {
 *     vec4 color = texture(u_Texture, vec2(0.0, 0.5));
 * }
 * }</pre>
 * The {@code binding} attribute in the layout portion of the code refers to the {@code slot} parameter in {@link RenderPass#bindImage(Image, Sampler, int)}.
 * And then, you can use the {@code texture(sample, coordinates)} function to sample from the image.
 *
 *
 *
 * <h3>Rendering to images</h3>
 * To output to an image specified as a color attachment of a render pass in {@link CommandList#beginRenderPass(Attachment, Attachment...)}
 * you again need to define it in the shader. Here is an example:
 * <pre>{@code
 * layout (location = 0) out vec4 color;
 *
 * void main() {
 *     color = vec4(1.0, 0.0, 0.5, 1.0);
 * }
 * }</pre>
 * The {@code location} attribute in the layout portion of the code refers to the {@code index}
 * of the color attachment inside {@link CommandList#beginRenderPass(Attachment, Attachment...)}.
 *
 *
 *
 * <h3>Note on the usage of the binding attribute</h3>
 * People already familiar with GLSL might notice how this documentation depends on the usage of the {@code binding} layout qualifier.
 * While at the same time suggesting the GLSL 330 version.
 * Normally the {@code binding} qualifier is not supported on this version without any extensions.<p>
 * Juno gets around this by scanning the GLSL source code for the {@code binding} qualifier,
 * saving the value and removing it from the source code before passing it to the driver.
 */
public interface Shader extends Resource {
    /**
     * @return the shader type.
     */
    ShaderType getType();
}
