#version 400 core

in vec2 textureCoords;
//GL_TEXTURE0
uniform sampler2D textureSampler;

out vec4 outColor;
void main() {
    outColor = texture(textureSampler, textureCoords);
}