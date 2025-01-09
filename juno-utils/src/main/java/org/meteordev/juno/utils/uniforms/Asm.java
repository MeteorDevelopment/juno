package org.meteordev.juno.utils.uniforms;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class Asm {
    // Helpers

    public static void getField(MethodVisitor visitor, Class<?> owner, String name) {
        try {
            if (owner.isRecord()) {
                invoke(visitor, owner, name);
            } else {
                Field field = owner.getField(name);
                visitor.visitFieldInsn(Opcodes.GETFIELD, tName(owner), name, tDescriptor(field.getType()));
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void invoke(MethodVisitor visitor, Class<?> owner, String name, Class<?>... parameters) {
        try {
            Method method = owner.getMethod(name, parameters);
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, tName(owner), name, mDescriptor(method.getReturnType(), parameters), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // Types

    public static String tName(Class<?> klass) {
        return Type.getInternalName(klass);
    }

    public static String tDescriptor(Class<?> klass) {
        return Type.getDescriptor(klass);
    }

    public static String mDescriptor(Class<?> returnClass, Class<?>... parameters) {
        StringBuilder sb = new StringBuilder();

        sb.append('(');

        for (Class<?> parameter : parameters) {
            sb.append(tDescriptor(parameter));
        }

        sb.append(')');
        sb.append(tDescriptor(returnClass));

        return sb.toString();
    }
}
