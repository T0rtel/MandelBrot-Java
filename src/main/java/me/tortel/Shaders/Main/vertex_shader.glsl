#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;


out vec4 fColor;
out vec2 fragPos;

void main(){
    fragPos = aPos.xy;
    gl_Position = vec4(aPos, 1.0);
}