package org.meteordev.juno.utils.uniforms;

import org.joml.Matrix4f;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

class WriterFactory {
    private static int COUNT = 0;

    static UniformWriter create(Class<?> klass) {
        // Class
        String name = WriterFactory.class.getPackageName() + "." + klass.getSimpleName() + "_Writer_" + COUNT++;
        ClassWriter writerClass = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        writerClass.visit(Opcodes.V16, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, name.replace('.', '/'), null, "java/lang/Object", new String[] { Type.getInternalName(UniformWriter.class) });

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
        getSize.visitAnnotation(Type.getDescriptor(Override.class), false).visitEnd();
        getSize.visitCode();
        getSize.visitLdcInsn(getSizing(klass).size);
        getSize.visitInsn(Opcodes.IRETURN);
        getSize.visitMaxs(0, 0);
        getSize.visitEnd();

        // write()
        MethodVisitor write = writerClass.visitMethod(Opcodes.ACC_PUBLIC, "write", "(Ljava/lang/Object;Ljava/nio/ByteBuffer;)V", null, null);
        write.visitAnnotation(Type.getDescriptor(Override.class), false).visitEnd();
        write.visitCode();
        write.visitVarInsn(Opcodes.ALOAD, 1);
        write.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(klass));
        write.visitVarInsn(Opcodes.ASTORE, 3);
        createClassWriter(klass, write, 0, 3);
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

    static int createClassWriter(Class<?> klass, MethodVisitor method, int offset, int variableIndex) {
        // Start, end labels
        Label start = new Label();
        Label end = new Label();

        method.visitLabel(start);

        // Apply alignment to the offset
        Sizing sizing = getSizing(klass);
        byteBufferIncrementPosition(method, offset % sizing.alignment);
        offset += offset % sizing.alignment;

        for (Field field : klass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            Class<?> fieldType = field.getType();

            // Built-in writable classes

            if (fieldType == Matrix4f.class) {
                Sizing fieldSizing = getSizing(fieldType);
                byteBufferIncrementPosition(method, offset % fieldSizing.alignment);
                offset += offset % fieldSizing.alignment;

                getField(method, variableIndex, field);
                method.visitVarInsn(Opcodes.ALOAD, 2);
                method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(fieldType), "get", "(Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;", false);
                method.visitInsn(Opcodes.POP);

                byteBufferIncrementPosition(method, fieldSizing.size);
                offset += fieldSizing.size;

                continue;
            }

            // Primitives

            if (fieldType.isPrimitive()) {
                Sizing fieldSizing = getSizing(fieldType);
                byteBufferIncrementPosition(method, offset % fieldSizing.alignment);
                offset += offset % fieldSizing.alignment;

                String primitiveName = fieldType.getSimpleName();
                primitiveName = Character.toUpperCase(primitiveName.charAt(0)) + primitiveName.substring(1);

                method.visitVarInsn(Opcodes.ALOAD, 2);
                getField(method, variableIndex, field);
                method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/ByteBuffer", "put" + primitiveName, "(" + Type.getDescriptor(fieldType) + ")Ljava/lang/ByteBuffer;", false);
                method.visitInsn(Opcodes.POP);

                continue;
            }

            // User-defined classes

            offset = createClassWriter(fieldType, method, offset, variableIndex + 1);
        }

        // End label
        method.visitLabel(end);

        // Create variable holding the reference to this class
        method.visitLocalVariable("var" + variableIndex, Type.getDescriptor(klass), null, start, end, variableIndex);

        return offset;
    }

    static void getField(MethodVisitor method, int variableIndex, Field field) {
        method.visitVarInsn(Opcodes.ALOAD, variableIndex);

        if (field.getDeclaringClass().isRecord()) {
            method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(field.getDeclaringClass()), field.getName(), "()" + Type.getDescriptor(field.getType()), false);
        } else {
            method.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
        }
    }

    static void byteBufferIncrementPosition(MethodVisitor method, int padding) {
        if (padding == 0)
            return;

        method.visitVarInsn(Opcodes.ALOAD, 2);
        method.visitInsn(Opcodes.DUP);
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ByteBuffer.class), "position", "()I", false);
        method.visitLdcInsn(padding);
        method.visitInsn(Opcodes.IADD);
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ByteBuffer.class), "position", "(I)Ljava/nio/ByteBuffer;", false);
        method.visitInsn(Opcodes.POP);
    }

    static Sizing getSizing(Class<?> klass) {
        // Built-in writable classes

        if (klass == Matrix4f.class) {
            return new Sizing(4 * 4 * 4, 4 * 4 * 4);
        }

        // Primitives

        if (klass.isPrimitive()) {
            if (klass == float.class || klass == int.class) {
                return new Sizing(4, 4);
            }

            throw new IllegalArgumentException(klass.getName() + " primitive cannot be written as an uniform");
        }

        // User-defined classes

        if (!klass.isAnnotationPresent(UniformStruct.class)) {
            throw new IllegalArgumentException(klass.getPackageName() + " class cannot be written as an uniform because it doesn't have the @UniformStruct annotation");
        }

        int size = 0;
        int alignment = 0;

        for (Field field : klass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            Sizing sizing = getSizing(field.getType());

            size += size % sizing.alignment;
            size += sizing.size;

            alignment = Math.max(alignment, sizing.alignment);
        }

        return new Sizing(size, alignment);
    }

    record Sizing(int size, int alignment) {}
}
