#version 400 core

in vec3 position;
out vec4 color;

void main(void) {
    gl_Position = vec4(position, 1.0);
    color = vec4(0, 1, 1, 1);
}