package me.tortel.Listeners;

import org.lwjgl.glfw.GLFWKeyCallbackI;
import static org.lwjgl.glfw.GLFW.*;

public class KeyListener implements GLFWKeyCallbackI {

    private static boolean[] keys = new boolean[GLFW_KEY_LAST];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key < keys.length) {
            keys[key] = (action != GLFW_RELEASE);
        }
    }

    public static boolean isKeyPressed(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }
}