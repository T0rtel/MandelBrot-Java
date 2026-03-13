#version 330 core
#define number_of_pixels_to_average 3

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

// https://www.stevenfrady.com/tools/palette?p=[[0.5,0.51,0.54],[0.82,0.23,0.27],[0.79,0.97,0.41],[0.99,0.71,0.79]]
vec3 palette2(float t){
    vec3 a=vec3(0.5,0.51,0.54);
    vec3 b=vec3(0.82,0.23,0.27);
    vec3 c=vec3(0.79,0.97,0.41);
    vec3 d=vec3(0.99,0.71,0.79);
    return a+b*cos(6.28318*(c*t+d));
}

float random (in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.989,78.233))) * 43758.543);
}

float rseed = 0.;
vec2 random2() {
    vec2 seed = vec2(rseed++, rseed++);
    return vec2(random(seed + 0.342), random(seed + 0.756));
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

    vec3 col;

    if (fragPos.y >= 0.5 || fragPos.y <= -0.5){ //if the pixel is in the upper half of the screen, which is the Mandelbrot set, we color it black

        for (int i = 0; i < number_of_pixels_to_average; i++){ //3 points
            vec2 rand_xy_add = random2();
            col += palette(iterate()); //addition of the colors of the 3 points, which will be averaged at the end
        }

        col = col / float(number_of_pixels_to_average) * 2.0; //average the colors

        //float t = iterate();

        //vec3 col = (t == 1.)? vec3(0.) :  col;

        color = vec4(col, 1.0);
        return;
    }else{
        float t = iterate();

        vec3 col = (t == 1.)? vec3(0.) :  palette2(t);

        color = vec4(col, 1.0);
        return;
    }
}