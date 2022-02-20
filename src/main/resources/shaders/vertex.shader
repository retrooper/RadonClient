#version 400 core

//Attribute 0
in vec3 position;
//Attribute 1
in vec2 uv;
//Atribute 2
in int textureIndex;
//Attribute 3
in mat4 transformation;

//Uniforms
uniform mat4 projection;
uniform mat4 view;

out vec2 fragTextureCoords;
flat out int fragTextureIndex;

void main() {
    gl_Position =  projection * view * transformation * vec4(position, 1.0);
    fragTextureCoords = uv;
    fragTextureIndex = textureIndex;
}