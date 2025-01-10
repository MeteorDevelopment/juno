#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 uv;

layout (binding = 0) uniform Uniforms {
    mat4 u_ProjectionView;
    mat4 u_Model;
};

out vec2 a_Uv;

void main() {
    gl_Position = u_ProjectionView * u_Model * vec4(pos, 1.0);

    a_Uv = uv;
}