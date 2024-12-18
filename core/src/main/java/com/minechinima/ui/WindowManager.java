package com.minechinima.ui;

import com.minechinima.controller.Controller;
import com.minechinima.controller.HistoryManager;
import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import com.minechinima.model.DataTreeManager;
import com.minechinima.operator.OperatorManager;
import com.minechinima.rendering.IWindow;
import com.minechinima.ui.elements.UIScreen;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.input.IOEventManager;
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
    private final OperatorManager operatorManager;
    private final IOEventManager ioEventManager;
    //TODO the ONLY reason we only have one window handle is because last time I tried I couldn't cuz of Minecraft... fix this.
    private final IWindow renderingWindow;
    public final ImguiLoader loader;

    public WindowManager(IWindow renderingWindow, ImguiLoader imguiLoader, Controller controller) {
        this.historyManager = new HistoryManager();
        this.renderingWindow = renderingWindow;
        this.loader = imguiLoader;
        this.ioEventManager = new IOEventManager();
        this.operatorManager = new OperatorManager(this, controller);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    public void init(LWJGLInputEventManager inputEventManager) {
        this.loader.init(this.renderingWindow.ptr(), inputEventManager);
        for (UIWindow window : this.windows) {
            window.onShow();
        }
    }

    public OperatorManager getOperatorManager() {
        return this.operatorManager;
    }

    public void render(UIContext context) {
        this.loader.beginFrame();
        this.ioEventManager.dispatch();
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
