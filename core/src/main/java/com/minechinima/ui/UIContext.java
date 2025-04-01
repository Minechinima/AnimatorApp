package com.minechinima.ui;

import com.minechinima.i18n.TranslationBackpack;
import com.minechinima.operator.IContext;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.rendering.IWindow;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.ui.elements.UIScreen;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.uihandlers.UIHandlerRegistry;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public class UIContext<C extends IContext> {
    private final ImguiLoader imguiContext;
    private final IWindow window;
    private final UIOperatorContextBuilder<C> builder = new UIOperatorContextBuilder<>();
    private final UIManager<C> manager;
    private UIWindow<C> currentWindow;
    private C operatorContext;
    private final Logger logger;
    private final UIPanelFactory<C> panelFactory = new UIPanelFactory<>();
    private final HashMap<Class<?>, Object> registry = new HashMap<>();
    private final TranslationBackpack translationBackpack;
    private Locale locale = Locale.getDefault();
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

    public UIContext(IWindow window, UIManager<C> manager, ImguiLoader imguiContext, TranslationBackpack translationBackpack, UIHandlerRegistry uiHandlerRegistry) {
        this.window = window;
        this.manager = manager;
        this.imguiContext = imguiContext;
        this.uiHandlerRegistry = uiHandlerRegistry;
        this.translationBackpack = translationBackpack;
        this.logger = Logger.getLogger("UI");
    }

    public Logger getLogger() {
        return this.logger;
    }

    void setOperatorContext(C context) {
        this.operatorContext = context;
    }

    public UIOperatorContextBuilder<C> getContextBuilder() {
        return this.builder;
    }

    public C getOperatorContext() {
        return this.operatorContext;
    }

    void setWindow(UIWindow<C> window) {
        this.currentWindow = window;
    }

    public UIWindow<C> getCurrentWindow() {
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

    public UIManager<C> getUIManager() {
        return this.manager;
    }

    public ImguiLoader getImguiContext() {
        return this.imguiContext;
    }

    public TranslationBackpack getTranslationBackpack() {
        return this.translationBackpack;
    }

    public UIPanelFactory<C> getPanelFactory() {
        return this.panelFactory;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
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

    /**
     * Intermediate {@link UIOperatorContext} so the UI can set the correct values
     * and later build an immutable version of this.
     * @param <C>
     */
    public class UIOperatorContextBuilder<C extends IContext> {
        private UIWindow<C> clickedWindow;
        private UIScreen<C> clickedUIScreen;
        private UISpace<C> clickedUISpace;
        private UIElement<C> clickedWidget;
        private UIWindow<C> currentHoveredWindow;
        private UIScreen<C> currentHoveredUIScreen;
        private UISpace<C> currentHoveredUISpace;
        private UIElement<C> currentHoveredWidget;

        public void reset() {
            this.clickedWindow = null;
            this.clickedUIScreen = null;
            this.clickedUISpace = null;
            this.clickedWidget = null;
            this.currentHoveredWindow = null;
            this.currentHoveredUIScreen = null;
            this.currentHoveredUISpace = null;
            this.currentHoveredWidget = null;
        }

        public UIWindow<C> getClickedWindow() {
            return clickedWindow;
        }

        public void setClickedWindow(UIWindow<C> clickedWindow) {
            if (this.clickedWindow != null) return;
            this.clickedWindow = clickedWindow;
        }

        public UIScreen<C> getClickedUIScreen() {
            return clickedUIScreen;
        }

        public void setClickedUIScreen(UIScreen<C> clickedUIScreen) {
            if (this.clickedUIScreen != null) return;
            this.clickedUIScreen = clickedUIScreen;
        }

        public UISpace<C> getClickedUISpace() {
            return clickedUISpace;
        }

        public void setClickedUISpace(UISpace<C> clickedUISpace) {
            if (this.clickedUISpace != null) return;
            this.clickedUISpace = clickedUISpace;
        }

        public UIElement<C> getClickedWidget() {
            return clickedWidget;
        }

        public void setClickedWidget(UIElement<C> clickedWidget) {
            if (this.clickedWidget != null) return;
            this.clickedWidget = clickedWidget;
        }

        public UIWindow<C> getCurrentHoveredWindow() {
            return currentHoveredWindow;
        }

        public void setCurrentHoveredWindow(UIWindow<C> currentHoveredWindow) {
            if (this.currentHoveredWindow != null) return;
            this.currentHoveredWindow = currentHoveredWindow;
        }

        public UIScreen<C> getCurrentHoveredUIScreen() {
            return currentHoveredUIScreen;
        }

        public void setCurrentHoveredUIScreen(UIScreen<C> currentHoveredUIScreen) {
            if (this.currentHoveredUIScreen != null) return;
            this.currentHoveredUIScreen = currentHoveredUIScreen;
        }

        public UISpace<C> getCurrentHoveredUISpace() {
            return currentHoveredUISpace;
        }

        public void setCurrentHoveredUISpace(UISpace<C> currentHoveredUISpace) {
            if (this.currentHoveredUISpace != null) return;
            this.currentHoveredUISpace = currentHoveredUISpace;
        }

        public UIElement<C> getCurrentHoveredWidget() {
            return currentHoveredWidget;
        }

        public void setCurrentHoveredWidget(UIElement<C> currentHoveredWidget) {
            if (this.currentHoveredWidget != null) return;
            this.currentHoveredWidget = currentHoveredWidget;
        }

        public UIOperatorContext build() {
            return new UIOperatorContext(
                    this.clickedWindow,
                    this.clickedUIScreen,
                    this.clickedUISpace,
                    this.clickedWidget,
                    this.currentHoveredWindow,
                    this.currentHoveredUIScreen,
                    this.currentHoveredUISpace,
                    this.currentHoveredWidget,
                    UIContext.this.manager);
        }
    }
}