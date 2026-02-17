package com.minechinima;

import com.minechinima.utils.NativeLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Loading Natives...");
            NativeLoader.loadNatives();
            Application app = new Application();
            System.out.println("init app...");
            app.init();
            System.out.println("start app loop...");
            app.loop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
