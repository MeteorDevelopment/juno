package org.meteordev.juno.api;

public class JunoProvider {
    private static Juno INSTANCE;

    public static Juno get() {
        if (INSTANCE == null) throw new IllegalStateException("No Juno instance registered");
        return INSTANCE;
    }

    public static void set(Juno instance) {
        INSTANCE = instance;
    }
}
