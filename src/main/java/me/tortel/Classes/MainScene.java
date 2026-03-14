package me.tortel.Classes;

import me.tortel.Classes.Renderer.Shader;
import me.tortel.Listeners.MouseListener;
import me.tortel.Listeners.KeyListener;
import me.tortel.Util.GpuManager;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;

public class MainScene extends Scene {

    private Shader shader;

    // View state in complex-plane coordinates
    private double centerX = -0.5;
    private double centerY =  0.0;
    private double zoom    =  1.0;

    private static final double PAN_SPEED  = 0.005;
    private static final double ZOOM_SPEED = 0.5;
    private static final int    MAX_ITER   = 600;

    private final int windowWidth;
    private final int windowHeight;

    public MainScene(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    @Override
    public void init() {
        shader = new Shader("src/main/java/me/tortel/Shaders/Main");
        shader.compile();
    }

    @Override
    public void update(float dt) {
        handleInput(dt);
        uploadUniforms();

        shader.use();
        shader.detach();
    }

    private void handleInput(float dt) {
        // Zoom with scroll wheel
        double scroll = MouseListener.getScrollY();
        if (scroll != 0) {
            zoom *= (scroll < 0) ? ZOOM_SPEED : 1.0 / ZOOM_SPEED;
            MouseListener.resetScroll();
        }

        // Pan with arrow keys
        double panStep = PAN_SPEED / zoom;
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A))  centerX -= panStep;
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) centerX += panStep;
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W))    centerY += panStep;
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S))  centerY -= panStep;

        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_R))  {
            centerY = 0.0;
            centerX = -0.5;
            zoom = 1.0;
        }

        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_C)) {
            GpuManager.relaunchWithGpu("NVIDIA GeForce RTX 5070 Ti Laptop GPU");
        }

        // Keyboard zoom fallback
        if (KeyListener.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL)) zoom *= 1.0 + ZOOM_SPEED * dt;
        if (KeyListener.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS)) zoom /= 1.0 + ZOOM_SPEED * dt;

        // Raise iteration cap at high zoom for detail
        // (optional — can be expensive)
    }

    private void uploadUniforms() {
        shader.uploadVec2("u_center",     (float) centerX, (float) centerY);
        shader.uploadFloat("u_zoom",      (float) zoom);
        shader.uploadVec2("u_resolution", windowWidth, windowHeight);
        //shader.uploadInt("u_maxIter",     MAX_ITER);
    }
}