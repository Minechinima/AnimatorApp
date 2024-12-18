package com.minechinima.ui;

import com.minechinima.controller.Controller;
import com.minechinima.controller.HistoryManager;
import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import com.minechinima.model.DataTreeManager;

import com.minechinima.operator.OperatorConfiguration;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.OperatorFactory;
import com.minechinima.operator.OperatorManager;
import com.minechinima.rendering.IWindow;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.input.IOEventManager;
import imgui.flag.ImGuiKey;
import imgui.internal.ImGui;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all open windows and rendering them.
 * WindowManager is like the root of all user interaction
 */
public class UIManager<C extends OperatorContext> {
    public final List<UIWindow<C>> windows = new ArrayList<>();
    private final HistoryManager historyManager;
    private final OperatorManager<C> operatorManager;
    private final IOEventManager ioEventManager;
    //TODO the ONLY reason we only have one window handle is because last time I tried I couldn't cuz of Minecraft... fix this.
    private final IWindow renderingWindow;
    public final ImguiLoader loader;
    private UIWindow<C> active = null;
    private UIWindow<C> hovered = null;

    public UIManager(IWindow renderingWindow, ImguiLoader imguiLoader,
                     OperatorConfiguration<C> configuration, OperatorFactory<C> factory,
                     Controller controller) {
        this.historyManager = new HistoryManager();
        this.renderingWindow = renderingWindow;
        this.loader = imguiLoader;
        this.ioEventManager = new IOEventManager();
        this.operatorManager = new OperatorManager<>(this, factory, configuration, controller);
        this.ioEventManager.registerListener(this.operatorManager);
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

    public OperatorManager<C> getOperatorManager() {
        return this.operatorManager;
    }

    public void setActiveWindow(UIWindow<C> window) {
        this.active = window;
    }

    @Nullable
    public UIWindow<C> getActiveWindow() {
        return this.windows.get(0);
    }

    @Nullable
    public UIWindow<C> getCurrentlyHoveredWindow() {
        return this.windows.get(0);
    }

    public void render(UIContext context) {
        this.loader.beginFrame();
        this.ioEventManager.dispatch();
        for (UIWindow<C> window : this.windows) {
            context.setWindow(window);
            window.render(context);
        }
        this.loader.endFrame();
    }

    public void close() {
        for (UIWindow<C> window : this.windows) {
            window.onClose();
        }
    }
}
