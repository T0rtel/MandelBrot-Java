package me.tortel.Listeners;

import lombok.Getter;
import org.lwjgl.glfw.GLFWScrollCallbackI;

public class MouseListener implements GLFWScrollCallbackI {

    @Getter
    private static double scrollY = 0.0;

    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        scrollY = yoffset;
    }

    public static void resetScroll() {
        scrollY = 0.0;
    }
}