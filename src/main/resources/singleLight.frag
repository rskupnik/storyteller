#version 100

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

// Attributes from vertex shader
varying LOWP vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;    // diffuse map

// Values used for shading algorithm...
uniform vec2 Resolution;        // resolution of screen
uniform vec3 LightPos;          // light position, normalized
uniform LOWP vec4 LightColor;   // light RGBA -- alpha is intensity
uniform LOWP vec4 AmbientColor; // ambient RGBA -- alpha is intensity
uniform vec3 Falloff;           // attentuation coefficients

void main() {
    // RGBA of our diffuse color
    vec4 lDiffuseColor = texture2D(u_texture, vTexCoord);

    // The delta position of light
    vec3 lLightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);

    // Correct for aspect ratio
    lLightDir.x *= Resolution.x / Resolution.y;

    // Determine distance (used for attentuation) BEFORE we normalize our LightDir
    float D = length(lLightDir);

    vec3 lDiffuse = (LightColor.rgb * LightColor.a);

    // Pre-multiply ambient color with intensity
    vec3 lAmbient = AmbientColor.rgb * AmbientColor.a;

    // Calculate attentuation
    float lAttentuation = 1.0 / (Falloff.x + (Falloff.y*D) + (Falloff.z*D*D));

    // The calculation which brings it all together
    vec3 lIntensity = lAmbient + lDiffuse * lAttentuation;
    vec3 lFinalColor = lDiffuseColor.rgb * lIntensity;
    gl_FragColor = vColor * vec4(lFinalColor, lDiffuseColor.a);
}