package com.minechinima;

import com.minechinima.input.hotkey.*;
import com.minechinima.input.hotkey.configs.HotKeyKeyboardConfig;
import com.minechinima.input.hotkey.configs.HotKeyMouseConfig;
import com.minechinima.input.hotkey.configs.values.KeyModEnum;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnum;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnum;
import com.minechinima.input.keymap.KeyMapEventManager;
import com.minechinima.input.keymap.builder.KeyEventBuilder;
import com.minechinima.input.keymap.builder.MouseMoveKeyMapBuilder;
import com.minechinima.input.raw.*;
import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import com.minechinima.i18n.ClasspathBundleProvider;
import com.minechinima.i18n.TranslationBackpack;
import com.minechinima.controller.ControllerManager;
import com.minechinima.model.BBContext;
import com.minechinima.model.BBDataTreeManager;
import com.minechinima.model.BBNameSpace;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.scene.Scene;
import com.minechinima.operator.*;
import com.minechinima.operator.config.OperatorConfig;
import com.minechinima.operator.config.OperatorConfigurations;
import com.minechinima.operator.defaults.RedoOperator;
import com.minechinima.operator.defaults.UIDragValueSlider;
import com.minechinima.operator.defaults.UndoOperator;
import com.minechinima.rendering.IWindow;

import com.minechinima.ui.ImguiLoader;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.elements.properties.*;
import com.minechinima.ui.panels.UIViewport;
import com.minechinima.ui.UIManager;
import com.minechinima.ui.uihandlers.UIHandlerRegistry;
import com.minechinima.ui.panels.UIOutlinerPanel;
import com.minechinima.ui.panels.UIPropertiesPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Locale;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Application {
    private long[] window = new long[1];
    private final IWindow windowProvider = new IWindow() {
        @Override
        public long ptr() {
            return Application.this.window[0];
        }
    };
    /* ui layer*/
    private UIManager<BBOperatorContext> uiManager;
    private final OperatorManager<BBOperatorContext> operatorManager;
    /* data layer */
    private final ControllerManager controller;
    private final BBDataTreeManager dataTree;
    private LWJGLInputEventManager inputEventManager;
    private final UIHandlerRegistry handlerRegistry;
    private final TranslationBackpack translationBackpack;
    private final Logger logger;
    private final HotKeyConfigRegistry hotKeyConfigRegistry;

    public Application() {
        this.logger = LogManager.getLogger(Application.class);
        EventTypeRegistry dataEventTypes = new EventTypeRegistry();
        EventTypeRegistry.registerDefaultEventTypes(dataEventTypes);

        this.hotKeyConfigRegistry = new HotKeyConfigRegistry(1, HotKeyKeyboardConfig.class, () -> new HotKeyKeyboardConfig("keyboard", "hi"));
        this.hotKeyConfigRegistry.register(2, HotKeyMouseConfig.class, () -> new HotKeyMouseConfig("mouse", "hi"));

        this.dataTree = new BBDataTreeManager(dataEventTypes, this.hotKeyConfigRegistry);
        BBNameSpace bb = this.dataTree.getRoot();
        this.handlerRegistry = new UIHandlerRegistry();
        this.translationBackpack = new TranslationBackpack();
        this.controller = new ControllerManager(this.dataTree, this.logger);

        OperatorFactory opFactory = new OperatorFactory(this.logger);
        OperatorConfigurations<BBOperatorContext> opConfig = bb.opConfig;

        opFactory.register(bb.ops.move, TranslateObject::new);
        OperatorConfig config = opConfig.newConfig();
        config.operatorType.set(bb.ops.move);
        config.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfig) config.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_G);


        opFactory.register(bb.ops.delete, DeleteObject::new);
        OperatorConfig deleteConfig = opConfig.newConfig();
        deleteConfig.operatorType.set(bb.ops.delete);
        deleteConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfig) deleteConfig.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_DELETE);

        opFactory.register(bb.ops.dragUISlider, UIDragValueSlider::new);
        OperatorConfig dragConfig = opConfig.newConfig();
        dragConfig.operatorType.set(bb.ops.dragUISlider);
        dragConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfig) dragConfig.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_BUTTON_1);

        opFactory.register(bb.ops.undo, UndoOperator::new);
        OperatorConfig undoConfig = opConfig.newConfig();
        undoConfig.operatorType.set(bb.ops.undo);
        undoConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfig) undoConfig.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_Z);
        ((HotKeyKeyboardConfig) undoConfig.hotKeyConfigSelector.get()).mod.set(KeyModEnum.CONTROL);

        opFactory.register(bb.ops.redo, RedoOperator::new);
        OperatorConfig redoConfig = opConfig.newConfig();
        redoConfig.operatorType.set(bb.ops.redo);
        redoConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfig) redoConfig.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_Y);
        ((HotKeyKeyboardConfig) redoConfig.hotKeyConfigSelector.get()).mod.set(KeyModEnum.CONTROL);
        /*opFactory.register(bb.ops.delete, DeleteObject::new);
        opConfig.keyConfig.put(Key.KEY_DELETE, bb.ops.delete);

        opFactory.register(bb.ops.dragUISlider, UIDragValueSlider::new);
        opConfig.uiKeyConfig.put(Key.MOUSE_BUTTON_1, bb.ops.dragUISlider);*/

        this.operatorManager = new OperatorManager<>(opFactory, opConfig, this.controller, this.logger, BBOperatorContext.class);
    }

    public void init() {
        this.translationBackpack.loadTranslations(new ClasspathBundleProvider("com/minechinima/base", getClass()), Locale.GERMANY);
        this.translationBackpack.loadTranslations(new ClasspathBundleProvider("com/minechinima/base", getClass()), Locale.US);

        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.window[0] = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (this.window[0] == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        this.inputEventManager = new LWJGLInputEventManager();

        glfwSetKeyCallback(this.window[0], this.inputEventManager::onKey);
        glfwSetMouseButtonCallback(this.window[0], this.inputEventManager::onMouseButton);
        glfwSetCursorPosCallback(this.window[0], this.inputEventManager::onCursor);
        glfwSetScrollCallback(this.window[0], this.inputEventManager::onScroll);
        glfwSetCharCallback(this.window[0], this.inputEventManager::onChar);

        this.inputEventManager.addListener(this::closeOnEscape, KeyEvent.class);
        this.inputEventManager.addListener(this::debugInputEvent, KeyEvent.class);
        this.uiManager = new UIManager<>(this.windowProvider, new ImguiLoader(this.inputEventManager), this.operatorManager, this.handlerRegistry,
                new BBOperatorContext(new BBContext("", this.dataTree.getRoot().data), null));
        this.inputEventManager.addListener(this.uiManager, InputEvent.class);
        KeyMapEventManager eventManager = this.initKeyMapEventManager(this.uiManager);
        this.inputEventManager.addListener(eventManager, InputEvent.class);
        this.uiManager.setKeyMapEventManager(eventManager);

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window[0], pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window[0],
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window[0]);
        glfwSwapInterval(1);
        glfwShowWindow(window[0]);
        GL.createCapabilities();

        this.uiManager.init();
        this.uiManager.windows.add(new UIWindow());

        BBDatabase db = this.dataTree.getRoot().data;
        db.objects.add(new BaseObject("test", db));
        db.objects.add(new BaseObject("test1", db));
        db.objects.add(new BaseObject("test2", db));
        db.objects.add(new BaseObject("test3", db));
        Scene scene = new Scene("test", db);
        db.scenes.add(scene);
        for (BaseObject child : db.objects.getChildren()) {
            //scene.rootCollection.addPointer(child);
        }
    }

    private KeyMapEventManager initKeyMapEventManager(UIManager uiManager) {
        KeyMapEventManager manager = new KeyMapEventManager(uiManager);
        manager.registerKeyMapBuilder(KeyEvent.class, new KeyEventBuilder());
        manager.registerKeyMapBuilder(MouseEvent.class, new MouseMoveKeyMapBuilder());
        return manager;
    }

    public void loop() {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        UIContext<BBOperatorContext> context = new UIContext<>(this.windowProvider, this.uiManager, this.uiManager.loader, translationBackpack, new UIHandlerRegistry());
        context.register(new PropertyContextRegistry());
        context.setLocale(Locale.GERMAN);

        PropertyContextRegistry propertyPanelRegistry = context.getRegisteredObj(PropertyContextRegistry.class);
        propertyPanelRegistry.registerUIPropertyContext(new UICameraProperties());
        propertyPanelRegistry.registerUIPropertyContext(new UIEntityProperties());
        propertyPanelRegistry.registerUIPropertyContext(new UIObjectProperties());
        propertyPanelRegistry.registerStandardUIPropertyContext(new UIRenderProperties());

        context.getPanelFactory().registerUIPanel("properties", () -> new UIPropertiesPanel("properties"));
        context.getPanelFactory().registerUIPanel("outliner", () -> new UIOutlinerPanel("outliner"));
        context.getPanelFactory().registerUIPanel("viewport", () -> new UIViewport("viewport"));

        while ( !glfwWindowShouldClose(window[0]) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            this.uiManager.render(context);

            glfwSwapBuffers(window[0]);
            glfwPollEvents();
        }

        this.uiManager.close();
        glfwFreeCallbacks(window[0]);
        glfwDestroyWindow(window[0]);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void closeOnEscape(KeyEvent event) {
        if (event.getCurrentKey() != Key.KEY_ESCAPE) {
            return;
        }

        if (event.getInputType() != InputType.RELEASE) {
            return;
        }

        glfwSetWindowShouldClose(window[0], true);
    }

    private void debugInputEvent(KeyEvent event) {
        //System.out.println(event.toString());
    }
}