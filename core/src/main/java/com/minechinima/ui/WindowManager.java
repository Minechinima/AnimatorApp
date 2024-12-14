package com.minechinima.ui;

import com.minechinima.controller.Controller;
import com.minechinima.controller.HistoryManager;
import com.minechinima.model.DataTreeManager;
import com.minechinima.rendering.IWindow;
import com.minechinima.ui.elements.UIScreen;
import com.minechinima.ui.elements.UIWindow;
import imgui.flag.ImGuiKey;
import imgui.internal.ImGui;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all open windows and rendering them.
 * WindowManager is like the root of all user interaction
 */
public class WindowManager {
    public final List<UIWindow> windows = new ArrayList<>();
    private final HistoryManager historyManager;
    private final DataTreeManager<?> dataTree;
    //TODO the ONLY reason we only have one window handle is because last time I tried I couldn't cuz of Minecraft... fix this.
    private final IWindow renderingWindow;
    public final ImguiLoader loader;
    private final Controller controller;

    public WindowManager(IWindow renderingWindow, ImguiLoader imguiLoader, DataTreeManager<?> dataTree) {
        this.dataTree = dataTree;
        this.historyManager = new HistoryManager();
        this.renderingWindow = renderingWindow;
        this.loader = imguiLoader;
        this.controller = new Controller(this.dataTree);
    }

    public Controller getController() {
        return controller;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void init() {
        this.loader.init(this.renderingWindow.ptr());
        for (UIWindow window : this.windows) {
            window.onShow();
        }
    }

    public void render(UIContext context) {
        this.loader.beginFrame();
        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
            if (ImGui.isKeyPressed(ImGuiKey.Y)) {
                this.historyManager.undo(this.controller, this.dataTree.getRoot());
            } else if (ImGui.isKeyPressed(ImGuiKey.Z)) {
                this.historyManager.redo(this.controller, this.dataTree.getRoot());
            }
        }
        for (UIWindow window : this.windows) {
            context.setWindow(window);
            window.render(context);
        }
        this.loader.endFrame();
    }

    public void close() {
        for (UIWindow window : this.windows) {
            window.onClose();
        }
    }
}
