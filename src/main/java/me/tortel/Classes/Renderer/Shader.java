package me.tortel.Classes.Renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Shader {

    private int shaderProgram;
    private int vaoID;  // Core profile requires a bound VAO even with no VBOs
    String filepath;
    String vertexSrc, fragmentSrc;

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

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);

        // Empty VAO — required by OpenGL Core profile even when drawing
        // with no vertex attributes. The vertex shader uses gl_VertexID instead.
        vaoID = glGenVertexArrays();
    }

    public void use() {
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);
        glDrawArrays(GL_TRIANGLES, 0, 3);  // 3 vertices → fullscreen triangle
    }

    public void detach() {
        glBindVertexArray(0);
        glUseProgram(0);
    }

    // ── Uniform helpers ────────────────────────────────────────────────────────

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