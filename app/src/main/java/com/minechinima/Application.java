package com.minechinima;

import com.minechinima.input.hotkey.*;
import com.minechinima.input.hotkey.configs.HotKeyKeyboardConfigGroupStruct;
import com.minechinima.input.hotkey.configs.HotKeyMouseConfigGroupStruct;
import com.minechinima.input.hotkey.configs.values.*;
import com.minechinima.input.keymap.KeyMapEventManager;
import com.minechinima.input.keymap.builder.KeyEventBuilder;
import com.minechinima.input.keymap.builder.MouseDragKeyMapBuilder;
import com.minechinima.input.keymap.builder.MouseMoveKeyMapBuilder;
import com.minechinima.input.keymap.builder.MouseScrollBuilder;
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
import com.minechinima.operator.config.modal.*;
import com.minechinima.operator.config.operator.OperatorKeyConfigItemGroupStruct;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.operator.defaults.DragValueSliderProp;
import com.minechinima.operator.defaults.RedoOperator;
import com.minechinima.operator.defaults.UIDragValueSlider;
import com.minechinima.operator.defaults.UndoOperator;
import com.minechinima.operator.defaults.modal.CancelModal;
import com.minechinima.operator.defaults.modal.ConfirmModal;
import com.minechinima.operator.defaults.modal.uidrag.DragIncrementModal;
import com.minechinima.operator.defaults.modal.uidrag.RoundModal;
import com.minechinima.operator.history.CompressionStrategyRegistry;
import com.minechinima.operator.history.HistoryManager;
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

        this.hotKeyConfigRegistry = new HotKeyConfigRegistry(1, HotKeyKeyboardConfigGroupStruct.class, () -> new HotKeyKeyboardConfigGroupStruct("keyboard", "hi"));
        this.hotKeyConfigRegistry.register(2, HotKeyMouseConfigGroupStruct.class, () -> new HotKeyMouseConfigGroupStruct("mouse", "hi"));

        this.dataTree = new BBDataTreeManager(dataEventTypes, this.hotKeyConfigRegistry);
        BBNameSpace bb = this.dataTree.getRoot();
        this.handlerRegistry = new UIHandlerRegistry();
        this.translationBackpack = new TranslationBackpack();
        this.controller = new ControllerManager(this.dataTree, this.logger);

        OperatorFactory opFactory = new OperatorFactory(this.logger);
        UserFunctionalityConfigGroupStruct<BBOperatorContext> opConfig = bb.opConfig;

        opFactory.register(bb.ops.move, TranslateObject::new);
        OperatorKeyConfigItemGroupStruct config = opConfig.newConfig();
        config.operatorType.set(bb.ops.move);
        config.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) config.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_G);


        opFactory.register(bb.ops.delete, DeleteObject::new);
        OperatorKeyConfigItemGroupStruct deleteConfig = opConfig.newConfig();
        deleteConfig.operatorType.set(bb.ops.delete);
        deleteConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) deleteConfig.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_DELETE);

        opFactory.register(bb.ops.dragUISlider, UIDragValueSlider::new);
        OperatorKeyConfigItemGroupStruct dragConfig = opConfig.newConfig();
        dragConfig.operatorType.set(bb.ops.dragUISlider);
        dragConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfigGroupStruct) dragConfig.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_BUTTON_1);
        ((HotKeyMouseConfigGroupStruct) dragConfig.hotKeyConfigSelector.get()).type.set(KeyType.CLICKDRAG);

        opFactory.register(bb.ops.undo, UndoOperator::new);
        OperatorKeyConfigItemGroupStruct undoConfig = opConfig.newConfig();
        undoConfig.operatorType.set(bb.ops.undo);
        undoConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) undoConfig.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_Z);
        ((HotKeyKeyboardConfigGroupStruct) undoConfig.hotKeyConfigSelector.get()).mod.set(KeyModEnum.CONTROL);

        opFactory.register(bb.ops.redo, RedoOperator::new);
        OperatorKeyConfigItemGroupStruct redoConfig = opConfig.newConfig();
        redoConfig.operatorType.set(bb.ops.redo);
        redoConfig.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) redoConfig.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_Y);
        ((HotKeyKeyboardConfigGroupStruct) redoConfig.hotKeyConfigSelector.get()).mod.set(KeyModEnum.CONTROL);

        opFactory.register(bb.ops.orbitViewport, OrbitViewportOperator::new);
        OperatorKeyConfigItemGroupStruct orbitCfg = opConfig.newConfig();
        orbitCfg.operatorType.set(bb.ops.orbitViewport);
        orbitCfg.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) orbitCfg.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_E);
        ((HotKeyKeyboardConfigGroupStruct) orbitCfg.hotKeyConfigSelector.get()).type.set(KeyType.CLICKDRAG);
        ((HotKeyKeyboardConfigGroupStruct) orbitCfg.hotKeyConfigSelector.get()).mod.set(KeyModEnum.SHIFT);
        ((HotKeyKeyboardConfigGroupStruct) orbitCfg.hotKeyConfigSelector.get()).confirmOnStop.set(true);

        opFactory.register(bb.ops.panViewport, PanViewportOP::new);
        OperatorKeyConfigItemGroupStruct panCfg = opConfig.newConfig();
        panCfg.operatorType.set(bb.ops.panViewport);
        panCfg.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfigGroupStruct) panCfg.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_BUTTON_3);
        ((HotKeyMouseConfigGroupStruct) panCfg.hotKeyConfigSelector.get()).type.set(KeyType.CLICKDRAG);
        ((HotKeyMouseConfigGroupStruct) panCfg.hotKeyConfigSelector.get()).mod.set(KeyModEnum.SHIFT);
        ((HotKeyMouseConfigGroupStruct) panCfg.hotKeyConfigSelector.get()).confirmOnStop.set(true);

        opFactory.register(bb.ops.zoomViewport, ZoomViewportOP::new);
        OperatorKeyConfigItemGroupStruct zoomInCfg = opConfig.newConfig();
        zoomInCfg.operatorType.set(bb.ops.zoomViewport);
        zoomInCfg.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfigGroupStruct) zoomInCfg.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_WHEEL_UP);
        ((ZoomViewportProps)zoomInCfg.props.getProps()).delta.set(1F);

        OperatorKeyConfigItemGroupStruct zoomOutCfg = opConfig.newConfig();
        zoomOutCfg.operatorType.set(bb.ops.zoomViewport);
        zoomOutCfg.hotKeyConfigSelector.set(this.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfigGroupStruct) zoomOutCfg.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_WHEEL_DOWN);
        ((ZoomViewportProps)zoomOutCfg.props.getProps()).delta.set(-1F);

        this.setupModal();

        CompressionStrategyRegistry logCompReg = new CompressionStrategyRegistry();

        this.operatorManager = new OperatorManager<>(opFactory, opConfig, new HistoryManager(logCompReg), this.controller, this.logger, BBOperatorContext.class);
    }

    public void setupModal() {
        UserFunctionalityConfigGroupStruct<BBOperatorContext> config = this.dataTree.getRoot().opConfig;
        PropertyModifierConfigsMapStruct propertyModifiers = config.modalKeyConfigs.propModifiers;
        PropertyModifiersListStruct<TranslateProps> translate_modal = new PropertyModifiersListStruct<>("transform_modal", TranslateProps.class, config.hotKeyConfigRegistry);
        PropertyModifiersListStruct<DragValueSliderProp> slider_modal = new PropertyModifiersListStruct<>("slider_modal", DragValueSliderProp.class, config.hotKeyConfigRegistry);
        propertyModifiers.add(translate_modal);
        propertyModifiers.add(slider_modal);

        translate_modal.register(0, TranslateModalAxisX.class, TranslateModalAxisX::new);
        translate_modal.register(1, TranslateModalAxisY.class, TranslateModalAxisY::new);

        slider_modal.register(0, DragIncrementModal.class, DragIncrementModal::new);
        slider_modal.register(1, RoundModal.class, RoundModal::new);

        PropertyModifierConfigGroupStruct<TranslateProps> xCfg = translate_modal.newConfig();
        xCfg.modifier.set(new TranslateModalAxisX());
        xCfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) xCfg.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_X);
        ((HotKeyKeyboardConfigGroupStruct) xCfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);

        PropertyModifierConfigGroupStruct<TranslateProps> yCfg = translate_modal.newConfig();
        yCfg.modifier.set(new TranslateModalAxisY());
        yCfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) yCfg.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_Y);
        ((HotKeyKeyboardConfigGroupStruct) yCfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);

        PropertyModifierConfigGroupStruct<DragValueSliderProp> slider1Cfg = slider_modal.newConfig();
        slider1Cfg.modifier.set(new DragIncrementModal(0.1F));
        slider1Cfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) slider1Cfg.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_LEFT_CONTROL);
        ((HotKeyKeyboardConfigGroupStruct) slider1Cfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);

        PropertyModifierConfigGroupStruct<DragValueSliderProp> sliderCfg = slider_modal.newConfig();
        sliderCfg.modifier.set(new DragIncrementModal(0.001F));
        sliderCfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) sliderCfg.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_LEFT_SHIFT);
        ((HotKeyKeyboardConfigGroupStruct) sliderCfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);
        ((HotKeyKeyboardConfigGroupStruct) sliderCfg.hotKeyConfigSelector.get()).confirmOnStop.set(true);

        PropertyModifierConfigGroupStruct<DragValueSliderProp> sliderRoundCfg = slider_modal.newConfig();
        sliderRoundCfg.modifier.set(new RoundModal());
        sliderRoundCfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) sliderRoundCfg.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_LEFT_ALT);
        ((HotKeyKeyboardConfigGroupStruct) sliderCfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);


        ModalFunctionConfigsMapStruct<BBOperatorContext> modalFunctions = config.modalFunctions;

        modalFunctions.register(0, ConfirmModal.class, ConfirmModal::new);
        modalFunctions.register(1, CancelModal.class, CancelModal::new);
        ModalFunctionConfigGroupStruct<BBOperatorContext> confirmCfg = new ModalFunctionConfigGroupStruct<>(modalFunctions);
        ModalFunctionConfigGroupStruct<BBOperatorContext> confirmCfg2 = new ModalFunctionConfigGroupStruct<>(modalFunctions);
        ModalFunctionConfigGroupStruct<BBOperatorContext> cancelCfg = new ModalFunctionConfigGroupStruct<>(modalFunctions);

        modalFunctions.add(confirmCfg);
        modalFunctions.add(confirmCfg2);
        modalFunctions.add(cancelCfg);

        confirmCfg.modifier.set(new ConfirmModal<>());
        confirmCfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfigGroupStruct) confirmCfg.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_BUTTON_1);
        ((HotKeyMouseConfigGroupStruct) confirmCfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);

        confirmCfg2.modifier.set(new ConfirmModal<>());
        confirmCfg2.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(1));
        ((HotKeyKeyboardConfigGroupStruct) confirmCfg2.hotKeyConfigSelector.get()).key.set(KeyboardKeyEnum.KEY_ENTER);
        ((HotKeyKeyboardConfigGroupStruct) confirmCfg2.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);

        cancelCfg.modifier.set(new CancelModal<>());
        cancelCfg.hotKeyConfigSelector.set(config.hotKeyConfigRegistry.getNew(2));
        ((HotKeyMouseConfigGroupStruct) cancelCfg.hotKeyConfigSelector.get()).key.set(MouseKeyEnum.MOUSE_BUTTON_2);
        ((HotKeyMouseConfigGroupStruct) cancelCfg.hotKeyConfigSelector.get()).type.set(KeyType.PRESS);
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
        manager.registerKeyMapBuilder(ScrollEvent.class, new MouseScrollBuilder());
        manager.registerKeyMapBuilder(MouseEvent.class, new MouseDragKeyMapBuilder());
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

    private void debugInputEvent(KeyEvent event) {
        //System.out.println(event.toString());
    }
}