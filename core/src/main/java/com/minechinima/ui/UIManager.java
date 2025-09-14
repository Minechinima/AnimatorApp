package com.minechinima.ui;

import com.minechinima.input.keymap.IKeyMapEventListener;
import com.minechinima.input.keymap.KeyMapEventManager;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.raw.*;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorManager;
import com.minechinima.rendering.IWindow;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.uihandlers.UIHandlerRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all open windows and rendering them.
 * WindowManager is like the root of all user interaction
 */
public class UIManager<C extends IContext> implements IInputEventListener<InputEvent>, IKeyMapEventListener<KeyMapEvent> {
    public final List<UIWindow> windows = new ArrayList<>();
    private final OperatorManager<C> operatorManager;
    //TODO the ONLY reason we only have one window handle is because last time I tried I couldn't cuz of Minecraft... fix this.
    private final IWindow renderingWindow;
    public final ImguiLoader loader;
    private UIWindow active = null;
    private UIWindow hovered = null;
    public UIHandlerRegistry handlerRegistry;
    private C operatorContext;
    private KeyMapEventManager keyMapEventManager;

    public UIManager(IWindow renderingWindow, ImguiLoader imguiLoader,
                     OperatorManager<C> operatorManager, UIHandlerRegistry handlerRegistry, C context) {
        this.renderingWindow = renderingWindow;
        this.loader = imguiLoader;
        this.operatorManager = operatorManager;
        this.operatorManager.setUIManager(this);
        this.handlerRegistry = handlerRegistry;
        this.operatorContext = context;
    }

    public void setKeyMapEventManager(KeyMapEventManager keyMapEventManager) {
        this.keyMapEventManager = keyMapEventManager;
        this.keyMapEventManager.addListener(KeyMapEvent.class, this);
        /* remove operatormanager to ensure the UIManager redirects events */
        this.keyMapEventManager.removeListener(this.operatorManager);
    }

    public KeyMapEventManager getKeyMapEventManager() {
        return this.keyMapEventManager;
    }

    public void init() {
        this.loader.init(this.renderingWindow.ptr());

    }

    @Nullable
    public UIWindow getWindow(long id) {
        return this.getActiveWindow();
    }

    public OperatorManager<C> getOperatorManager() {
        return this.operatorManager;
    }

    @Nullable
    public UIWindow getActiveWindow() {
        return this.windows.get(0);
    }

    @Nullable
    public UIWindow getCurrentlyHoveredWindow() {
        return this.windows.get(0);
    }

    public void render(UIContext<C> context) {
        this.operatorContext.setUIContext(null);
        this.operatorManager.resetCurrentContext();

        this.loader.beginFrame();
        for (UIWindow<C> window : this.windows) {
            context.setOperatorContext(this.operatorContext);
            context.setWindow(window);
            window.render(context);
        }
        this.loader.endFrame();

        this.operatorContext.setUIContext(context.getContextBuilder().build());
        context.getContextBuilder().reset();
        this.operatorManager.setCurrentContext(this.operatorContext);
    }

    public void close() {
        for (UIWindow<C> window : this.windows) {
            window.onClose();
        }
    }

    @Override
    public void onEvent(InputEvent event) {
        /* block imgui backend from receiving input events */
        //TODO UI cannot detect hovering when imgui does not receive any events, but operators need to know what is hovered.
        // solution: UI elements store the position of the rendered box and check without using imgui interface
        if (!this.operatorManager.isModalMode() || event instanceof MouseEvent || (event instanceof KeyEvent key && key.getInputType() == InputType.RELEASE)) {
            this.loader.getGLFWBackend().onEvent(event);
        }
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        if (event instanceof KeyMapMouseDragEvent drag) {
            //System.out.println(drag.direction());
        }
        if (!this.operatorManager.isModalMode()) {
            //TODO pass keymap to UI
        }
        //TODO only if keymapevent was not consumed by UI
        return this.operatorManager.onEvent(event);
    }
}
