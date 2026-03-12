#version 330 core

in vec2 fragPos;
out vec4 color;

uniform vec2  u_center;     // pan offset in complex plane (WASD keys)
uniform float u_zoom;       // zoom level (MOUSE WHEEL)
uniform vec2  u_resolution; // window size in pixels
uniform int   u_maxIter;    // iteration cap

void main(){
    float aspect = u_resolution.x / u_resolution.y;

    // Map this pixel to a complex number c = (real, imaginary)
    vec2 c = vec2(
    fragPos.x * aspect / u_zoom + u_center.x,
    fragPos.y / u_zoom + u_center.y
    );

    vec2 z = vec2(0.0);
    int iter = 0;

    //we start at z = 0 + 0i and repeatedly apply z = z^2 + c until |z| > 4 or we hit the iteration limit
    for(iter = 0; iter < u_maxIter; iter++){
        if(dot(z, z) > 4.0) break;
        z = vec2(z.x * z.x - z.y * z.y + c.x, 2.0 * z.x * z.y + c.y); //x is real part, y is imaginary part
    }

    // Points inside the set = black
    if(iter == u_maxIter){
        color = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    // Smooth coloring — removes harsh iteration bands
    float smooth_iter = float(iter) - log2(log2(dot(z, z))) + 4.0;
    float t = fract(smooth_iter / float(u_maxIter) * 8.0);

    // Ocean color palette
    vec3 a = vec3(0.016, 0.173, 0.325); // deep blue
    vec3 b = vec3(0.094, 0.573, 0.812); // mid blue
    vec3 c3 = vec3(0.976, 0.933, 0.855); // warm white
    vec3 d = vec3(0.929, 0.365, 0.478); // pink

    vec3 col;
    if      (t < 0.33) col = mix(a,  b,  t / 0.33);
    else if (t < 0.66) col = mix(b,  c3, (t - 0.33) / 0.33);
    else               col = mix(c3, d,  (t - 0.66) / 0.34);

    color = vec4(col, 1.0);
}