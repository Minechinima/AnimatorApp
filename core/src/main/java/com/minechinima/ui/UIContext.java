package com.minechinima.ui;

import com.minechinima.rendering.IWindow;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.uihandlers.UIHandlerRegistry;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UIContext {
    private final ImguiLoader imguiContext;
    private final IWindow window;
    private final WindowManager manager;
    private UIWindow currentWindow;
    private final UIPanelFactory panelFactory = new UIPanelFactory();
    private final HashMap<Class<?>, Object> registry = new HashMap<>();
    private final UIHandlerRegistry uiHandlerRegistry;
    private float mouseX;
    private float mouseY;
    private float mouseMoveX;
    private float mouseMoveY;
    private boolean ignoreNextMouseMove;
    private float mouseScroll;
    private final Set<Integer> clickedMouseKeys = new HashSet<>();
    private final Set<Integer> mouseKeysDown = new HashSet<>();
    private float partialTicks;

    public UIContext(IWindow window, WindowManager manager, ImguiLoader imguiContext, UIHandlerRegistry uiHandlerRegistry) {
        this.window = window;
        this.manager = manager;
        this.imguiContext = imguiContext;
        this.uiHandlerRegistry = uiHandlerRegistry;
    }

    void setWindow(UIWindow window) {
        this.currentWindow = window;
    }

    public UIWindow getCurrentWindow() {
        return this.currentWindow;
    }

    public void updateImguiMouseStates() {
        boolean[] clickedMouseButtons = ImGui.getIO().getMouseClicked();
        boolean[] downMouseButtons = ImGui.getIO().getMouseDown();
        this.clickedMouseKeys.clear();
        this.mouseKeysDown.clear();
        for (int i = 0; i < ImGuiMouseButton.COUNT; i++) {
            if (clickedMouseButtons[i]) this.clickedMouseKeys.add(i);
        }
        for (int i = 0; i < ImGuiMouseButton.COUNT; i++) {
            if (downMouseButtons[i]) this.mouseKeysDown.add(i);
        }

        this.mouseScroll = ImGui.getIO().getMouseWheel();

        if (this.ignoreNextMouseMove) {
            this.mouseMoveX = 0;
            this.mouseMoveY = 0;
            this.ignoreNextMouseMove = false;
        } else {
            this.mouseMoveX = ImGui.getMousePosX() - this.mouseX;
            this.mouseMoveY = ImGui.getMousePosY() - this.mouseY;
        }

        this.mouseX = ImGui.getMousePosX();
        this.mouseY = ImGui.getMousePosY();
    }

    /**
     * This will make the next mouse move not trigger any event methods.
     */
    public void ignoreNextMouseMove() {
        this.ignoreNextMouseMove = true;
    }

    public IWindow getWindow() {
        return this.window;
    }

    public WindowManager getWindowManager() {
        return this.manager;
    }

    public ImguiLoader getImguiContext() {
        return this.imguiContext;
    }

    public UIPanelFactory getPanelFactory() {
        return this.panelFactory;
    }

    public UIHandlerRegistry getUiHandlerRegistry() {
        return this.uiHandlerRegistry;
    }

    /**
     * Useful to register registries/factories without having to store them in static fields somewhere.<br>
     * This allows more flexibility and runtime configuration of the context the UI runs in.
     * @param obj
     * @param <T>
     */
    public <T> boolean register(T obj) {
        if (this.registry.containsKey(obj.getClass())) return false;
        this.registry.put(obj.getClass(), obj);
        return true;
    }

    public <T> T getRegisteredObj(Class<T> type) {
        /* it should be impossible to get a class cast exception here... */
        return this.registry.containsKey(type) ? (T) this.registry.get(type) : null;
    }

    public float getMouseX() {
        return this.mouseX;
    }

    public float getMouseY() {
        return this.mouseY;
    }

    public float getMouseMoveX() {
        return this.mouseMoveX;
    }

    public float getMouseMoveY() {
        return this.mouseMoveY;
    }

    public float getMouseScroll() {
        return this.mouseScroll;
    }

    public boolean isMouseKeyClicked(int mouseKey) {
        return this.clickedMouseKeys.contains(mouseKey);
    }

    public boolean isLeftMouseKeyClicked() {
        return this.clickedMouseKeys.contains(ImGuiMouseButton.Left);
    }

    public boolean isRightMouseKeyClicked() {
        return this.clickedMouseKeys.contains(ImGuiMouseButton.Right);
    }

    public boolean isMiddleMouseKeyClicked() {
        return this.clickedMouseKeys.contains(ImGuiMouseButton.Middle);
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    /**
     * @return true when the given mouseKey is being pressed.
     */
    public boolean isMouseKeyDown(int mouseKey) {
        return this.mouseKeysDown.contains(mouseKey) || this.clickedMouseKeys.contains(mouseKey);
    }

    public boolean isLeftMouseKeyDown() {
        return this.mouseKeysDown.contains(ImGuiMouseButton.Left);
    }

    public boolean isRightMouseKeyDown() {
        return this.mouseKeysDown.contains(ImGuiMouseButton.Right);
    }

    public boolean isMiddleMouseKeyDown() {
        return this.mouseKeysDown.contains(ImGuiMouseButton.Middle);
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public boolean isAnyShiftDown() {
        return ImGui.isKeyDown(ImGuiKey.LeftShift) || ImGui.isKeyDown(ImGuiKey.RightShift);
    }

    public boolean isLeftShiftDown() {
        return ImGui.isKeyDown(ImGuiKey.LeftShift);
    }

    public boolean isLeftCtrlDown() {
        return ImGui.isKeyDown(ImGuiKey.LeftCtrl);
    }

    public boolean isAnyCtrlDown() {
        return ImGui.isKeyDown(ImGuiKey.LeftCtrl) | ImGui.isKeyDown(ImGuiKey.RightCtrl);
    }
}