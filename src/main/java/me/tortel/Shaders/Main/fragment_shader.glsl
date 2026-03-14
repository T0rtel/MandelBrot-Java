#version 330 core
#define SS 16           // supersampling samples per pixel
#define maxIter 1024   // max mandelbrot iterations
#define B 4.0         // escape radius

in vec2 fragPos;
out vec4 color;

uniform vec2  u_center;     // pan offset in complex plane (WASD keys)
uniform float u_zoom;       // zoom level (MOUSE WHEEL)
uniform vec2  u_resolution; // window size in pixels

// https://www.stevenfrady.com/tools/palette?p=[[0.11,0.3,0.86],[0.87,0.34,0.2],[0.29,0.1,0.99],[0.81,0.95,0.99]]
//vec3 palette(float t){
//    vec3 a=vec3(0,0.5,0.5);
//    vec3 b=vec3(0,0.5,0.5);
//    vec3 c=vec3(0,0.5,0.33);
//    vec3 d=vec3(0,0.5,0.66);
//    return a+b*cos(6.28318*(c*t+d));
//}

vec3 palette(float t){
    vec3 a=vec3(.5);
    vec3 b=vec3(.5);
    vec3 c=vec3(1.0);
    vec3 d=vec3(.0, .10, .2);
    return a+b*cos(6.28318 * (c*t +d));
}

float random (in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.989,78.233))) * 43758.543);
}

float rseed = 0.;
vec2 random2() {
    vec2 seed = vec2(rseed++, rseed++);
    return vec2(random(seed + 0.342), random(seed + 0.756));
}

float iterate(in vec2 c){
    vec2 z = vec2(0.0);
    int iter = 0;

    //we start at z = 0 + 0i and repeatedly apply z = z^2 + c until |z| > 4 or we hit the iteration limit
    for(iter = 0; iter < maxIter; iter++){
        if(dot(z, z) > 4.0) break;
        z = vec2(z.x * z.x - z.y * z.y, 2.0 * z.x * z.y) + c; //x is real part, y is imaginary part
//        z = mat2(z, -z.y, z.x) * z + c;
    }

    // Inside the set → signal black
    if (iter == maxIter) return 1.0;

    // Smooth (continuous) iteration count — removes harsh color bands
    // log2(log2(|z|)) normalises the overshoot past the escape radius
    float smooth_i = float(iter) - log2(log2(dot(z, z))) + 4.0;// + log2(log2(B));
    return fract(smooth_i / float(maxIter) * 8.0);
}

void main(){

    vec3 col;
    vec2 fragCoord = gl_FragCoord.xy;
    vec2 R         = u_resolution;

    for (int i = 0; i < SS; i++){ //3 points
        // Jitter the pixel position by a sub-pixel random offset — this is
        // the supersampling step: instead of always sampling (500,500) we
        // might sample (500.28, 500.64) etc.
        vec2 jitter = random2();  // [0,1] in pixel space

        // Map jittered pixel → complex-plane coordinate:
        //   (2*p - R) / R.y  →  aspect-correct [-aspect,+aspect] x [-1,+1]
        //   / u_zoom          →  zoom
        //   + u_center        →  pan
        vec2 c = (2.0 * (fragCoord + jitter) - R) / R.y / u_zoom + u_center;

        float t = iterate(c); //iterate the point with the random offset

        if (t == 1.) {//if the point is in the set, add black
            col = vec3(0.);
            break;
        }
        else{
            col += palette(t); //otherwise add the color corresponding to the number of iterations
        }
    }

    col = col / float(SS); //average the colors

    color = vec4(col, 1.0);

}