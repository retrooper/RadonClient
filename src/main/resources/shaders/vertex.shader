#version 400 core

//Attribute 0
in vec3 position;
//Attribute 1
in vec2 uv;
//Atribute 2
in float textureIndex;
//Uniforms
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec2 fragTextureCoords;
out float fragTextureIndex;

void main() {
    gl_Position =  projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    fragTextureCoords = uv;
    fragTextureIndex = textureIndex;
}