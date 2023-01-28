package org.meteordev.juno.api.shader.uniforms;

public interface FloatUniform extends Uniform {
    interface Single extends FloatUniform {
        void set(float v);

        default void set(double v) {
            set((float) v);
        }
    }

    interface Double extends FloatUniform {
        void set(float v1, float v2);

        default void set(double v1, double v2) {
            set((float) v1, (float) v2);
        }
    }

    interface Triple extends FloatUniform {
        void set(float v1, float v2, float v3);

        default void set(double v1, double v2, double v3) {
            set((float) v1, (float) v2, (float) v3);
        }
    }

    interface Quadruple extends FloatUniform {
        void set(float v1, float v2, float v3, float v4);

        default void set(double v1, double v2, double v3, double v4) {
            set((float) v1, (float) v2, (float) v3, (float) v4);
        }
    }
}
