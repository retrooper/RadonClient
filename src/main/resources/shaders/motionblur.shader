#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D colortex0;
uniform sampler2D colortex1;

uniform mat4 gbufferProjectionInverse;
uniform mat4 gbufferPreviousProjection;

uniform mat4 gbufferModelViewInverse;
uniform mat4 gbufferPreviousModelView;

#define MOTIONBLUR_QUALITY 5 // [5 10 15 20 25 30 40 50]
#define MOTIONBLUR_LENGTH .6 // [0.2 0.4 0.6 0.8 1.0]

float rand(vec2 co){
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec4 currentPosition = vec4(TexCoords.x * 2.0 - 1.0, TexCoords.y * 2.0 - 1.0, 2.0 * texture(colortex1, TexCoords).r - 1.0, 1.0);
    vec4 fragposition = currentPosition * gbufferProjectionInverse;
    fragposition = fragposition * gbufferModelViewInverse;
    fragposition /= fragposition.w;

    vec4 previousPosition = fragposition;
    previousPosition = previousPosition * gbufferPreviousModelView;
    previousPosition = previousPosition * gbufferPreviousProjection;
    previousPosition /= previousPosition.w;

    vec2 velocity = (currentPosition - previousPosition).st / 2;
    velocity /= MOTIONBLUR_QUALITY;
    velocity *= MOTIONBLUR_LENGTH;

    float r = rand(TexCoords) / 2. + .5;
    // r = 1;
    vec2 coord = TexCoords + velocity * r;

    vec3 color = vec3(0);
    int samples = 0;
    for (int i = 0; i <= MOTIONBLUR_QUALITY; ++i, coord += velocity * r) {
        ++samples;
        if (coord.s > 1.0 || coord.t > 1.0 || coord.s < 0.0 || coord.t < 0.0) {
            color += texture(colortex0, TexCoords).rgb;
            break;
        }
        color += texture(colortex0, coord).rgb;
    }

    FragColor = vec4(color / samples, 1.);
}