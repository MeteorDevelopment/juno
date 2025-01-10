#version 330 core

layout (location = 0) out vec4 color;

layout (binding = 0) uniform sampler2D u_Texture;

in vec2 a_Uv;

void main() {
    color = texture(u_Texture, a_Uv);
}