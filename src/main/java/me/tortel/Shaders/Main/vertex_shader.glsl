#version 330 core

// No vertex attributes needed at all.
// We use gl_VertexID to generate a triangle that covers the entire screen.
// Call with: glDrawArrays(GL_TRIANGLES, 0, 3)
//
// The triangle covers NDC space like this (way outside [-1,1] on purpose):
//
//   (-1, 3)
//      |  \
//      |    \
//   (-1,-1)---(3,-1)
//
// OpenGL clips it to the [-1,1] viewport, giving a perfect fullscreen quad.

out vec2 fragPos;

void main() {
    vec2 positions[3] = vec2[](
    vec2(-1.0, -1.0),  // bottom-left
    vec2( 3.0, -1.0),  // bottom-right (way out, gets clipped)
    vec2(-1.0,  3.0)   // top-left    (way out, gets clipped)
    );

    fragPos = positions[gl_VertexID];
    gl_Position = vec4(positions[gl_VertexID], 0.0, 1.0);
}