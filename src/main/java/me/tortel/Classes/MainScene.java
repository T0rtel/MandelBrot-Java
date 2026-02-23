package me.tortel.Classes;

import me.tortel.Classes.Renderer.Shader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class MainScene extends Scene{

    Shader shader;

    public MainScene() {

    }

    @Override
    public void init() {
        shader = new Shader("src/main/java/me/tortel/Shaders");
        shader.compile();
    }

    @Override
    public void update(float dt) {
        shader.use();

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); //bind nothing

        glUseProgram(0); //use no shader program

        shader.detach();
    }
}
