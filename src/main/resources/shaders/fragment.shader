#version 400 core

in vec2 fragTextureCoords;
flat in int fragTextureIndex;
//GL_TEXTURE_ARRAY_2D
uniform sampler2DArray textureArray;
out vec4 outColor;

void main() {
    vec3 texCoords  = vec3(fragTextureCoords, fragTextureIndex);
    outColor = texture(textureArray, texCoords);
}