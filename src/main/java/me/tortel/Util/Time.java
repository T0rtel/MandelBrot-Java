package me.tortel.Util;

import lombok.Getter;
import me.tortel.Classes.Window;

public class Time {

    private static long lastTime;
    @Getter
    private static long deltaTime;

    public Time() {
        lastTime = System.nanoTime();
    }

    public static void update() {
        long currentTime = System.nanoTime();
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;
    }

    public static float getDtInSeconds() {
        return deltaTime / 1_000_000_000.0f;
    }

    public static double getFPS() {
        if (getDtInSeconds() == 0) {
            return 0;
        }

        return 1.0 / (getDtInSeconds());
    }
}
