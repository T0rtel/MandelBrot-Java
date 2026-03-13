#version 330 core

in vec2 fragPos;
out vec4 color;

uniform vec2  u_center;     // pan offset in complex plane (WASD keys)
uniform float u_zoom;       // zoom level (MOUSE WHEEL)
uniform vec2  u_resolution; // window size in pixels
uniform int   u_maxIter;    // iteration cap

// https://www.stevenfrady.com/tools/palette?p=[[0.11,0.3,0.86],[0.87,0.34,0.2],[0.29,0.1,0.99],[0.81,0.95,0.99]]
vec3 palette(float t){
    vec3 a=vec3(0,0.5,0.5);
    vec3 b=vec3(0,0.5,0.5);
    vec3 c=vec3(0,0.5,0.33);
    vec3 d=vec3(0,0.5,0.66);
    return a+b*cos(6.28318*(c*t+d));
}

float iterate(){
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

    // Smooth coloring — removes harsh iteration bands
    float smooth_iter = float(iter) - log2(log2(dot(z, z))) + 4.0;
    float t = fract(smooth_iter / float(u_maxIter) * 8.0);

    return t;
}

void main(){

    float t = iterate();

    vec3 col = (t == 1.)? vec3(0.) :  palette(t);

    color = vec4(col, 1.0);
}