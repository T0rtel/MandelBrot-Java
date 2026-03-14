package me.tortel.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GpuManager {

    /** Queries available GPUs via PowerShell (Windows). */
    public static List<String> getAvailableGpus() {
        List<String> gpus = new ArrayList<>();
        try {
            Process process = new ProcessBuilder(
                    "powershell", "-Command",
                    "Get-WmiObject Win32_VideoController | Select-Object -ExpandProperty Name"
            ).redirectErrorStream(true).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) gpus.add(line);
                }
            }
        } catch (Exception e) {
            System.err.println("GPU query failed: " + e.getMessage());
        }

        if (gpus.isEmpty()) gpus.add("Default GPU");
        gpus.forEach(gpu -> System.out.println("Detected GPU: " + gpu));
        return gpus;
    }

    /**
     * Restarts the JVM.
     * Note: actual GPU selection on NVIDIA/AMD systems requires setting
     * the preferred GPU in the NVIDIA Control Panel / AMD Radeon Software
     * for javaw.exe. This restart applies any such setting cleanly.
     */
    public static void relaunchWithGpu(String gpuName) {
        System.out.println("Switching to: " + gpuName);
        try {
            String java = ProcessHandle.current().info().command().orElse("java");
            new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"), "me.tortel.Main")
                    .inheritIO()
                    .start();
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Relaunch failed: " + e.getMessage());
        }
    }
}