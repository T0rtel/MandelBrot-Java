package me.tortel.Classes.Renderer;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Shader {

    private int shaderProgram;
    String filepath;
    String vertexSrc, fragmentSrc;
    private int vaoID, vboID, eboID;

    private float[] vertexArray = {
            // position          // color
            1f, -1f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,  // Bottom right 0
            -1f,  1f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,  // Top left     1
            1f,  1f, 0.0f,     1.0f, 0.0f, 1.0f, 1.0f,  // Top right    2
            -1f, -1f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,  // Bottom left  3
    };

    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            vertexSrc   = new String(Files.readAllBytes(Paths.get(filepath + "/vertex_shader.glsl")));
            fragmentSrc = new String(Files.readAllBytes(Paths.get(filepath + "/fragment_shader.glsl")));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open shader file: '" + filepath + "'";
        }
    }

    public void compile() {
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);
        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println("ERROR: Vertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, glGetShaderi(vertexID, GL_INFO_LOG_LENGTH)));
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);
        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println("ERROR: Fragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH)));
        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            System.out.println("ERROR: Shader linking failed: " +
                    glGetProgramInfoLog(shaderProgram, glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH)));
        }

        // Upload geometry to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int posSize    = 3;
        int colorSize  = 4;
        int stride     = (posSize + colorSize) * Float.BYTES;
        glVertexAttribPointer(0, posSize,   GL_FLOAT, false, stride, 0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, stride, posSize * Float.BYTES);
    }

    public void use() {
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
    }

    public void detach() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    // ── Uniform helpers ──────────────────────────────────────────────

    public void uploadFloat(String name, float value) {
        int loc = glGetUniformLocation(shaderProgram, name);
        glUseProgram(shaderProgram);
        glUniform1f(loc, value);
    }

    public void uploadInt(String name, int value) {
        int loc = glGetUniformLocation(shaderProgram, name);
        glUseProgram(shaderProgram);
        glUniform1i(loc, value);
    }

    public void uploadVec2(String name, float x, float y) {
        int loc = glGetUniformLocation(shaderProgram, name);
        glUseProgram(shaderProgram);
        glUniform2f(loc, x, y);
    }
}