#version 400 core

//Attribute 0
in vec3 position;
//Attribute 1
in vec2 uv;
//Uniforms
uniform mat4 transformationMatrix;

out vec2 textureCoords;

void main(void) {
    gl_Position = transformationMatrix * vec4(position, 1.0);
    textureCoords = uv;
}