#version 400 core

in vec3 position;
out vec4 color;

void main(void) {
    gl_Position = vec4(position, 1.0);
    //float x = max(min(sin(position.x), 0.9), 0.5);
    float x = position.x + 0.5;
    float y = position.y + 0.5;
    float z = position.z + 0.5;
    color = vec4(x, y, z, 1.0);
    //color = vec4(1, 0, 0, 1);
}