package org.meteordev.juno.opengl.pipeline;

import io.github.douira.glsl_transformer.ast.node.declaration.InterfaceBlockDeclaration;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.expression.LiteralExpression;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.NamedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinFixedTypeSpecifier;
import io.github.douira.glsl_transformer.ast.print.PrintType;
import io.github.douira.glsl_transformer.ast.transform.SingleASTTransformer;
import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.pipeline.CreateShaderException;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.opengl.BaseGLResource;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLObjectType;
import org.meteordev.juno.opengl.GLResource;

import java.util.Optional;

public class GLShader extends BaseGLResource implements GLResource, Shader {
    private final ShaderType type;
    private final String name;

    private final int handle;

    public final String[] uniformBindings;
    public final String[] imageBindings;

    public GLShader(ShaderType type, String source, String name) {
        this.type = type;
        this.name = name;

        this.uniformBindings = new String[4];
        this.imageBindings = new String[4];

        source = transformSource(source);

        handle = GL33C.glCreateShader(GL.convert(type));
        GL.setName(GLObjectType.SHADER, handle, name);
        GL33C.glShaderSource(handle, source);
        GL33C.glCompileShader(handle);

        if (GL33C.glGetShaderi(handle, GL33C.GL_COMPILE_STATUS) != GL33C.GL_TRUE) {
            String message = GL33C.glGetShaderInfoLog(handle);
            throw new CreateShaderException(type, name, message);
        }
    }

    private String transformSource(String source) {
        var transformer = new SingleASTTransformer<>();
        transformer.setPrintType(PrintType.INDENTED_ANNOTATED);

        transformer.setTransformation((unit, root) -> {
            // Uniform blocks

            root.process(
                    root.nodeIndex.getStream(InterfaceBlockDeclaration.class),
                    declaration -> {
                        var blockName = declaration.getBlockName().getName();
                        var blockBinding = getBinding(declaration.getTypeQualifier());

                        if (blockBinding.isPresent() && blockBinding.get().getExpression() instanceof LiteralExpression literal && literal.isInteger()) {
                            if (literal.getInteger() < 0 || literal.getInteger() >= 4)
                                throw new CreateShaderException(type, name, "can only use uniform slots 0 - 3, got: " + literal.getInteger());

                            uniformBindings[(int) literal.getInteger()] = blockName;
                            detachAndDelete(blockBinding.get());

                            return;
                        }

                        throw new CreateShaderException(type, name, "Uniform block '" + blockName + "' doesn't have a binding.");
                    }
            );

            // Textures

            root.process(
                    root.nodeIndex.getStream(TypeAndInitDeclaration.class),
                    declaration -> {
                        if (declaration.getType().getTypeSpecifier() instanceof BuiltinFixedTypeSpecifier specifier && specifier.type == BuiltinFixedTypeSpecifier.BuiltinType.SAMPLER2D) {
                            var textureName = declaration.getMembers().get(0).getName().getName();
                            var textureBinding = getBinding(declaration.getType().getTypeQualifier());

                            if (textureBinding.isPresent() && textureBinding.get().getExpression() instanceof LiteralExpression literal && literal.isInteger()) {
                                if (literal.getInteger() < 0 || literal.getInteger() >= 4)
                                    throw new CreateShaderException(type, name, "can only use image slots 0 - 3, got: " + literal.getInteger());

                                imageBindings[(int) literal.getInteger()] = textureName;
                                detachAndDelete(textureBinding.get());

                                return;
                            }

                            throw new CreateShaderException(type, name, "Texture '"+ textureName + "' doesn't have a binding.");
                        }
                    }
            );
        });

        return transformer.transform(source);
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public ShaderType getType() {
        return type;
    }

    @Override
    protected void destroy() {
        GL33C.glDeleteShader(handle);
    }

    @Override
    public String toString() {
        return "Shader '" + name + "'";
    }

    private static void detachAndDelete(NamedLayoutQualifierPart part) {
        var layout = (LayoutQualifier) part.getParent();

        part.detachAndDelete();

        if (layout.getParts().isEmpty())
            layout.detachAndDelete();
    }

    private static Optional<NamedLayoutQualifierPart> getBinding(TypeQualifier qualifier) {
        return qualifier.getParts().stream()
                .filter(part -> part instanceof LayoutQualifier)
                .map(part -> (LayoutQualifier) part)
                .flatMap(layout -> layout.getParts().stream())
                .filter(part -> part instanceof NamedLayoutQualifierPart)
                .map(part -> (NamedLayoutQualifierPart) part)
                .filter(part -> part.getName().getName().equals("binding"))
                .findFirst();
    }
}
