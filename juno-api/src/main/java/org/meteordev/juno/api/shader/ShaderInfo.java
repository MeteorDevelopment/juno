package org.meteordev.juno.api.shader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class ShaderInfo {
    public final ShaderType type;

    public ShaderInfo(ShaderType type) {
        this.type = type;
    }

    public abstract String getSource();

    public static ShaderInfo source(ShaderType type, String source) {
        return new Source(type, source);
    }

    public static ShaderInfo resource(ShaderType type, String resource) {
        return new Resource(type, resource);
    }

    public static ShaderInfo path(ShaderType type, java.nio.file.Path path) {
        return new Path(type, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShaderInfo)) return false;

        ShaderInfo that = (ShaderInfo) o;

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    public static class Source extends ShaderInfo {
        public final String source;

        public Source(ShaderType type, String source) {
            super(type);
            this.source = source;
        }

        @Override
        public String getSource() {
            return source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Source source1 = (Source) o;

            return type == source1.type && source.equals(source1.source);
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + type.hashCode();
            result = 31 * result + source.hashCode();
            return result;
        }
    }

    public static class Resource extends ShaderInfo {
        public final String resource;

        public Resource(ShaderType type, String resource) {
            super(type);

            if (!resource.startsWith("/")) {
                resource = "/" + resource;
            }

            this.resource = resource;
        }

        @Override
        public String getSource() {
            InputStream in = ShaderInfo.class.getResourceAsStream(resource);
            if (in == null) return null;

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];

                while (true) {
                    int read = in.read(bytes);
                    if (read == -1) break;

                    out.write(bytes, 0, read);
                }

                return out.toString();
            }
            catch (IOException e) {
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Resource resource1 = (Resource) o;

            return type == resource1.type && resource.equals(resource1.resource);
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + type.hashCode();
            result = 31 * result + resource.hashCode();
            return result;
        }
    }

    public static class Path extends ShaderInfo {
        public final java.nio.file.Path path;

        public Path(ShaderType type, java.nio.file.Path path) {
            super(type);
            this.path = path;
        }

        @Override
        public String getSource() {
            try {
                return new String(Files.readAllBytes(path));
            }
            catch (IOException e) {
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Path path1 = (Path) o;

            return type == path1.type && path.equals(path1.path);
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + type.hashCode();
            result = 31 * result + path.hashCode();
            return result;
        }
    }
}
