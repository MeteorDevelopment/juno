package org.meteordev.juno.utils.uniforms;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

class WriterFactory {
    private static int COUNT = 0;

    public static UniformWriter create(Class<?> klass) {
        // Class
        String name = WriterFactory.class.getPackageName() + "." + klass.getSimpleName() + "_Writer_" + COUNT++;
        ClassWriter writerClass = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        writerClass.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, name.replace('.', '/'), null, "java/lang/Object", new String[] { Asm.tName(UniformWriter.class) });

        // <init>()
        MethodVisitor init = writerClass.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitMaxs(0, 0);
        init.visitInsn(Opcodes.RETURN);
        init.visitEnd();

        // getSize()
        MethodVisitor getSize = writerClass.visitMethod(Opcodes.ACC_PUBLIC, "getSize", "()I", null, null);
        getSize.visitAnnotation(Asm.tDescriptor(Override.class), false).visitEnd();
        getSize.visitCode();
        getSize.visitLdcInsn(getSizing(klass).size);
        getSize.visitInsn(Opcodes.IRETURN);
        getSize.visitMaxs(0, 0);
        getSize.visitEnd();

        // write()
        MethodVisitor write = writerClass.visitMethod(Opcodes.ACC_PUBLIC, "write", "(Ljava/lang/Object;Ljava/nio/ByteBuffer;)V", null, null);
        write.visitAnnotation(Asm.tDescriptor(Override.class), false).visitEnd();
        write.visitParameter("value", Opcodes.ACC_FINAL);
        write.visitParameter("buffer", Opcodes.ACC_FINAL);
        write.visitCode();
        write.visitVarInsn(Opcodes.ALOAD, 1);
        write.visitTypeInsn(Opcodes.CHECKCAST, Asm.tName(klass));
        write.visitVarInsn(Opcodes.ASTORE, 3);
        createStructWriter(klass, write, 0, 3);
        write.visitInsn(Opcodes.RETURN);
        write.visitMaxs(0, 0);
        write.visitEnd();

        // End
        writerClass.visitEnd();

        // Load class
        try {
            byte[] bytes = writerClass.toByteArray();
            Class<?> writerKlass = MethodHandles.lookup().defineClass(bytes);

            return (UniformWriter) writerKlass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static int createStructWriter(Class<?> klass, MethodVisitor visitor, int offset, int variableIndex) {
        // Start, end labels
        Label start = new Label();
        Label end = new Label();

        visitor.visitLabel(start);

        // Apply alignment to the offset
        Sizing sizing = getSizing(klass);
        offset += byteBufferIncrementPosition(visitor, padding(offset, sizing));

        for (Field field : klass.getDeclaredFields()) {
            if (skipField(field))
                continue;

            Class<?> fieldType = field.getType();

            // Built-in writable classes

            if (fieldType == Matrix4f.class) {
                Sizing fieldSizing = getSizing(fieldType);
                offset += byteBufferIncrementPosition(visitor, padding(offset, fieldSizing));

                getField(visitor, variableIndex, field);
                visitor.visitVarInsn(Opcodes.ALOAD, 2);
                Asm.invoke(visitor, Matrix4f.class, "get", ByteBuffer.class);
                visitor.visitInsn(Opcodes.POP);

                byteBufferIncrementPosition(visitor, fieldSizing.size);
                offset += fieldSizing.size;

                continue;
            }

            // Primitives

            if (fieldType.isPrimitive()) {
                Sizing fieldSizing = getSizing(fieldType);
                offset += byteBufferIncrementPosition(visitor, padding(offset, fieldSizing));

                String primitiveName = fieldType.getSimpleName();
                primitiveName = Character.toUpperCase(primitiveName.charAt(0)) + primitiveName.substring(1);

                visitor.visitVarInsn(Opcodes.ALOAD, 2);
                getField(visitor, variableIndex, field);
                Asm.invoke(visitor, ByteBuffer.class, "put" + primitiveName, fieldType);
                visitor.visitInsn(Opcodes.POP);

                offset += fieldSizing.size;

                continue;
            }

            // User-defined classes

            getField(visitor, variableIndex, field);
            visitor.visitVarInsn(Opcodes.ASTORE, variableIndex + 1);
            offset = createStructWriter(fieldType, visitor, offset, variableIndex + 1);
        }

        // End label
        visitor.visitLabel(end);

        // Create variable holding the reference to this class
        visitor.visitLocalVariable("struct" + (variableIndex - 2), Asm.tDescriptor(klass), null, start, end, variableIndex);

        return offset;
    }

    private static void getField(MethodVisitor visitor, int variableIndex, Field field) {
        visitor.visitVarInsn(Opcodes.ALOAD, variableIndex);
        Asm.getField(visitor, field.getDeclaringClass(), field.getName());
    }

    private static int byteBufferIncrementPosition(MethodVisitor visitor, int padding) {
        if (padding == 0)
            return 0;

        visitor.visitVarInsn(Opcodes.ALOAD, 2);
        visitor.visitInsn(Opcodes.DUP);
        Asm.invoke(visitor, ByteBuffer.class, "position");
        visitor.visitLdcInsn(padding);
        visitor.visitInsn(Opcodes.IADD);
        Asm.invoke(visitor, ByteBuffer.class, "position", int.class);
        visitor.visitInsn(Opcodes.POP);

        return padding;
    }

    private static Sizing getSizing(Class<?> klass) {
        // Built-in writable classes

        if (klass == Vector2f.class)
            return new Sizing(2 * 4, 2 * 4);

        if (klass == Vector3f.class)
            return new Sizing(3 * 4, 4 * 4);

        if (klass == Vector4f.class)
            return new Sizing(4 * 4, 4 * 4);

        if (klass == Matrix4f.class)
            return new Sizing(4 * 4 * 4, 4 * 4);

        // Primitives

        if (klass.isPrimitive()) {
            if (klass == float.class || klass == int.class) {
                return new Sizing(4, 4);
            }

            throw new IllegalArgumentException(klass + " primitive cannot be written as an uniform");
        }

        // User-defined classes

        if (klass.isInterface() || klass.isEnum() || klass.isArray())
            throw new IllegalArgumentException(klass + " cannot be written as an uniform because it is not a class or a record.");

        if (!Modifier.isPublic(klass.getModifiers()))
            throw new IllegalArgumentException(klass + " needs to be public to be written as an uniform");

        UniformStruct struct = klass.getAnnotation(UniformStruct.class);

        if (struct == null)
            throw new IllegalArgumentException(klass + " cannot be written as an uniform because it doesn't have the @UniformStruct annotation");

        int size = 0;
        int alignment = 0;

        for (Field field : klass.getDeclaredFields()) {
            if (skipField(field))
                continue;

            Sizing sizing = getSizing(field.getType());

            size += padding(size, sizing);
            size += sizing.size;

            alignment = Math.max(alignment, sizing.alignment);
        }

        if (size == 0)
            throw new IllegalArgumentException(klass + " cannot be written as an uniform because it is empty (contains no non-static public fields)");

        if (struct.alignment() > 0)
            alignment = struct.alignment();

        return new Sizing(size, alignment);
    }

    private static boolean skipField(Field field) {
        int mods = field.getModifiers();

        if (!field.getDeclaringClass().isRecord() && !Modifier.isPublic(mods))
            return true;

        return Modifier.isStatic(mods);
    }

    private static int padding(int offset, Sizing sizing) {
        return (sizing.alignment - (offset % sizing.alignment)) % sizing.alignment;
    }

    private record Sizing(int size, int alignment) {}
}
