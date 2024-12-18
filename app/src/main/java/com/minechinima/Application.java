package com.minechinima;

import com.minechinima.input.raw.KeyEvent;
import com.minechinima.input.raw.InputType;
import com.minechinima.input.raw.Key;
import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import com.minechinima.i18n.ClasspathBundleProvider;
import com.minechinima.i18n.TranslationBackpack;
import com.minechinima.controller.Controller;
import com.minechinima.i18n.ClasspathBundleProvider;
import com.minechinima.i18n.TranslationBackpack;
import com.minechinima.model.BBContext;
import com.minechinima.model.BBDataTreeManager;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.scene.Scene;
import com.minechinima.operator.*;
import com.minechinima.operator.defaults.RedoOperator;
import com.minechinima.operator.defaults.UndoOperator;
import com.minechinima.rendering.IWindow;

import com.minechinima.ui.BBOperatorContext;
import com.minechinima.ui.ImguiLoader;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.BBUIWindow;
import com.minechinima.ui.elements.properties.*;
import com.minechinima.ui.panels.UIViewport;
import com.minechinima.ui.UIManager;
import com.minechinima.ui.elements.BBUIWindow;
import com.minechinima.ui.elements.properties.*;
import com.minechinima.ui.panels.UIViewport;
import com.minechinima.ui.input.events.Input;
import com.minechinima.ui.uihandlers.UIHandlerRegistry;
import com.minechinima.ui.panels.UIOutlinerPanel;
import com.minechinima.ui.panels.UIPropertiesPanel;
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
    private final UIManager<BBOperatorContext> uiManager;
    /* data layer */
    private final Controller controller;
    private final BBDataTreeManager dataTree;
    private LWJGLInputEventManager inputEventManager;
    private final UIHandlerRegistry handlerRegistry;
    private final TranslationBackpack translationBackpack;

    public Application() {
        EventTypeRegistry dataEventTypes = new EventTypeRegistry();
        EventTypeRegistry.registerDefaultEventTypes(dataEventTypes);
        this.dataTree = new BBDataTreeManager(dataEventTypes);

        handlerRegistry = new UIHandlerRegistry();
        translationBackpack = new TranslationBackpack();

        this.controller = new Controller(this.dataTree);
        OperatorFactory<BBOperatorContext> opFactory = new OperatorFactory<>();
        OperatorConfiguration<BBOperatorContext> opConfig = new OperatorConfiguration<>();

        OTDeleteObject delOT = new OTDeleteObject("delete", "a");
        opFactory.register(delOT, OperatorDeleteObject::new);
        opConfig.keyConfig.put(Input.KEY_DELETE, delOT);

        OTDeleteObject undoT = new OTDeleteObject("undo", "a");
        opFactory.register(undoT, UndoOperator::new);
        opConfig.keyConfig.put(Input.KEY_Z, undoT);

        OTDeleteObject redoT = new OTDeleteObject("redo", "a");
        opFactory.register(redoT, RedoOperator::new);
        opConfig.keyConfig.put(Input.KEY_Y, redoT);

        this.uiManager = new UIManager<>(this.windowProvider, new ImguiLoader(), opConfig, opFactory, this.controller);
    }

    public void init() {
        translationBackpack.loadTranslations(new ClasspathBundleProvider("com/minechinima/base", getClass()), Locale.GERMANY);
        translationBackpack.loadTranslations(new ClasspathBundleProvider("com/minechinima/base", getClass()), Locale.US);

        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window[0] = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window[0] == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        inputEventManager = new LWJGLInputEventManager(window[0]);
        inputEventManager.addListener(this::closeOnEscape, KeyEvent.class);
        inputEventManager.addListener(this::debugInputEvent, KeyEvent.class);

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

        this.uiManager.init(inputEventManager);
        BBOperatorContext ctx = new BBOperatorContext(new UIOperatorContext(),new BBContext("context", this.dataTree.getRoot().data));
        ctx.uiManager = this.uiManager;
        this.uiManager.windows.add(new BBUIWindow(ctx));

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

    public void loop() {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        UIContext context = new UIContext(this.windowProvider, this.uiManager, this.uiManager.loader, translationBackpack, new UIHandlerRegistry());
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