#version 400 core

in vec2 fragTextureCoords;
in float fragTextureIndex;
//GL_TEXTURE_ARRAY_2D
uniform sampler2DArray textureArray;
out vec4 outColor;

void main() {
    if (fragTextureIndex > 0.9) {
        vec3 texCoords  = vec3(fragTextureCoords, fragTextureIndex);
        outColor = texture(textureArray, texCoords);
    }
    else {
        outColor = vec4(1.0, 0.0, 0.0, 0.0);
    }
}