package com.minechinima;

import com.minechinima.input.raw.IInputEventManager;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.input.raw.InputType;
import com.minechinima.input.raw.Key;
import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import com.minechinima.model.BBContext;
import com.minechinima.model.BBDataTreeManager;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.scene.Scene;
import com.minechinima.rendering.IWindow;

import com.minechinima.ui.ImguiLoader;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.WindowManager;
import com.minechinima.ui.elements.BBUIWindow;
import com.minechinima.ui.elements.properties.*;
import com.minechinima.ui.uihandlers.UIHandlerRegistry;
import com.minechinima.ui.panels.UIOutlinerPanel;
import com.minechinima.ui.panels.UIPropertiesPanel;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

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
    private final WindowManager windowManager;
    private final BBDataTreeManager dataTree;
    private LWJGLInputEventManager inputEventManager;

    public Application() {
        EventTypeRegistry dataEventTypes = new EventTypeRegistry();
        EventTypeRegistry.registerDefaultEventTypes(dataEventTypes);
        this.dataTree = new BBDataTreeManager(dataEventTypes);
        this.windowManager = new WindowManager(this.windowProvider, new ImguiLoader(), this.dataTree);
    }

    public void init() {
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
        inputEventManager.addListener(this::closeOnEscape);
        inputEventManager.addListener(this::debugInputEvent);

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
        this.windowManager.init(inputEventManager);
        this.windowManager.windows.add(new BBUIWindow(new BBContext("context", this.dataTree.getRoot().data)));

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
        UIContext context = new UIContext(this.windowProvider, this.windowManager, this.windowManager.loader);
        context.register(new PropertyContextRegistry());
        // Register EntryPointRegistry
        context.register(new UIHandlerRegistry());

        PropertyContextRegistry propertyPanelRegistry = context.getRegisteredObj(PropertyContextRegistry.class);
        propertyPanelRegistry.registerUIPropertyContext(new UICameraProperties());
        propertyPanelRegistry.registerUIPropertyContext(new UIEntityProperties());
        propertyPanelRegistry.registerUIPropertyContext(new UIObjectProperties());
        propertyPanelRegistry.registerStandardUIPropertyContext(new UIRenderProperties());

        context.getPanelFactory().registerUIPanel("properties", () -> new UIPropertiesPanel("properties"));
        context.getPanelFactory().registerUIPanel("outliner", () -> new UIOutlinerPanel("outliner"));

        while ( !glfwWindowShouldClose(window[0]) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            this.windowManager.render(context);

            glfwSwapBuffers(window[0]);
            glfwPollEvents();
        }

        this.windowManager.close();
        glfwFreeCallbacks(window[0]);
        glfwDestroyWindow(window[0]);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void closeOnEscape(InputEvent event) {
        if (event.currentKey() != Key.KEY_ESCAPE) {
            return;
        }

        if (event.inputType() != InputType.RELEASE) {
            return;
        }

        glfwSetWindowShouldClose(window[0], true);
    }

    private void debugInputEvent(InputEvent event) {
        //System.out.println(event.toString());
    }
}