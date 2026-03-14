package me.tortel.Classes;

import me.tortel.Listeners.KeyListener;
import me.tortel.Listeners.MouseListener;
import me.tortel.Util.GpuManager;
import me.tortel.Util.Time;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Window {

    private final int width;
    private final int height;
    private final String title;
    private long glfwWindow;

    public float r, g, b, a;

    private static Window window = null;

    private static Scene currentScene;

    public Window() {
        this.width = 2560;
        this.height = 1600;
        this.title = "help";//MandelBrot set

        r = 1;
        b = 1;
        g = 1;
        a = 1;
    }


    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static void startMainScene() {
        currentScene = new MainScene(get().width, get().height);
        currentScene.init();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        GpuManager.getAvailableGpus();

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        //handle inputs
        glfwSetScrollCallback(glfwWindow, new MouseListener());
        glfwSetKeyCallback(glfwWindow, new KeyListener());

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.startMainScene();
    }

    public void loop() {

        new Time();

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (Time.getDtInSeconds() >= 0) {
                currentScene.update(Time.getDtInSeconds());
            }

            glfwSwapBuffers(glfwWindow);

            Time.update();

            System.out.println("FPS: " + Time.getFPS());
        }
    }
}
