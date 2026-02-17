package com.minechinima.utils;

import org.lwjgl.system.MemoryUtil;
import java.io.*;
import java.nio.file.*;
import java.util.jar.*;

public class NativeLoader {
    public static void loadNatives() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir") + "/minechinima-natives";
        Files.createDirectories(Paths.get(tmpDir));

        String[] nativeJars = {
                "lwjgl-natives-windows.jar",
                "lwjgl-glfw-natives-windows.jar",
                "lwjgl-opengl-natives-windows.jar",
                "imgui-java-natives-windows.jar"
        };

        ClassLoader cl = NativeLoader.class.getClassLoader();
        for (String jar : nativeJars) {
            try (InputStream is = cl.getResourceAsStream(jar)) {
                if (is != null) {
                    Path out = Paths.get(tmpDir, jar);
                    Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        System.setProperty("org.lwjgl.librarypath", tmpDir);
    }
}
