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

// Our texture samplers
uniform sampler2D u_texture;    // diffuse map
uniform sampler2D u_normals;    // normal map

// Values used for shading algorithm...
uniform vec2 Resolution;        // resolution of screen
uniform vec3 LightPos;          // light position, normalized
uniform LOWP vec4 LightColor;   // light RGBA -- alpha is intensity
uniform LOWP vec4 AmbientColor; // ambient RGBA -- alpha is intensity
uniform vec3 Falloff;           // attentuation coefficients

void main() {
    // RGBA of our diffuse color
    vec4 lDiffuseColor = texture2D(u_texture, vTexCoord);

    // RGB of our normal map
    vec3 lNormalMap = texture2D(u_normals, vTexCoord).rgb;

    // The delta position of light
    vec3 lLightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);

    // Correct for aspect ratio
    lLightDir.x *= Resolution.x / Resolution.y;

    // Determine distance (used for attentuation) BEFORE we normalize our LightDir
    float D = length(lLightDir);

    // Normalize our vectors
    vec3 N = normalize(lNormalMap * 2.0 - 1.0);
    vec3 L = normalize(lLightDir);

    // Pre-multiply light color with intensity
    // Then perform N dot L to determine our diffuse term
    vec3 lDiffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);

    // Pre-multiply ambient color with intensity
    vec3 lAmbient = AmbientColor.rgb * AmbientColor.a;

    // Calculate attentuation
    float lAttentuation = 1.0 / (Falloff.x + (Falloff.y*D) + (Falloff.z*D*D));

    // The calculation which brings it all together
    vec3 lIntensity = lAmbient + lDiffuse * lAttentuation;
    vec3 lFinalColor = lDiffuseColor.rgb * lIntensity;
    gl_FragColor = vColor * vec4(lFinalColor, lDiffuseColor.a);
}