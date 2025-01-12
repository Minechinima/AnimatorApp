package com.minechinima.input.raw.lwjgl;

import com.minechinima.input.raw.*;

import static org.lwjgl.glfw.GLFW.*;

public class LWJGLInputEventManager extends AbstractInputEventManager {
    public LWJGLInputEventManager(long window) {
        super();
        registerCallbacks(window);
    }

    private void registerCallbacks(long window) {
        glfwSetKeyCallback(window, this::onKey);
        glfwSetMouseButtonCallback(window, this::onMouseButton);
        glfwSetCursorPosCallback(window, this::onCursor);
        glfwSetScrollCallback(window, this::onScroll);
    }

    public void onKey(long window, int key, int scancode, int action, int mods) {
        Key currentKey = getKeyboardKey(key);
        sendInputEvent(window, action, currentKey);
    }

    public void onMouseButton(long window, int button, int action, int mods) {
        Key currentKey = getMouseButton(button);
        sendInputEvent(window, action, currentKey);
    }

    public void onCursor(long window, double x, double y) {
        MouseEvent event = new MouseEvent(
                x, y,
                window
        );

        notifyListeners(event);
    }

    public void onScroll(long window, double xOffset, double yOffset) {
        ScrollEvent event = new ScrollEvent(
                xOffset, yOffset,
                window
        );

        notifyListeners(event);
    }

    private void sendInputEvent(long window, int action, Key currentKey) {
        InputType inputType = switch (action) {
            case GLFW_PRESS -> InputType.PRESS;
            case GLFW_RELEASE -> InputType.RELEASE;
            case GLFW_REPEAT -> InputType.REPEAT;
            default -> InputType.UNKNOWN;
        };

        KeyEvent event = new KeyEvent(
                currentKey,
                inputType,
                window
        );

        notifyListeners(event);
    }

    private Key getMouseButton(int key) {
        return switch (key) {
            case GLFW_MOUSE_BUTTON_1 -> Key.MOUSE_BUTTON_1;
            case GLFW_MOUSE_BUTTON_2 -> Key.MOUSE_BUTTON_2;
            case GLFW_MOUSE_BUTTON_3 -> Key.MOUSE_BUTTON_3;
            case GLFW_MOUSE_BUTTON_4 -> Key.MOUSE_BUTTON_4;
            case GLFW_MOUSE_BUTTON_5 -> Key.MOUSE_BUTTON_5;
            case GLFW_MOUSE_BUTTON_6 -> Key.MOUSE_BUTTON_6;
            case GLFW_MOUSE_BUTTON_7 -> Key.MOUSE_BUTTON_7;
            case GLFW_MOUSE_BUTTON_8 -> Key.MOUSE_BUTTON_8;
            default -> Key.KEY_UNKNOWN;
        };
    }

    private Key getKeyboardKey(int key) {
        return switch (key) {
            case GLFW_KEY_SPACE -> Key.KEY_SPACE;
            case GLFW_KEY_APOSTROPHE -> Key.KEY_APOSTROPHE;
            case GLFW_KEY_COMMA -> Key.KEY_COMMA;
            case GLFW_KEY_MINUS -> Key.KEY_MINUS;
            case GLFW_KEY_PERIOD -> Key.KEY_PERIOD;
            case GLFW_KEY_SLASH -> Key.KEY_SLASH;
            case GLFW_KEY_0 -> Key.KEY_0;
            case GLFW_KEY_1 -> Key.KEY_1;
            case GLFW_KEY_2 -> Key.KEY_2;
            case GLFW_KEY_3 -> Key.KEY_3;
            case GLFW_KEY_4 -> Key.KEY_4;
            case GLFW_KEY_5 -> Key.KEY_5;
            case GLFW_KEY_6 -> Key.KEY_6;
            case GLFW_KEY_7 -> Key.KEY_7;
            case GLFW_KEY_8 -> Key.KEY_8;
            case GLFW_KEY_9 -> Key.KEY_9;
            case GLFW_KEY_SEMICOLON -> Key.KEY_SEMICOLON;
            case GLFW_KEY_EQUAL -> Key.KEY_EQUAL;
            case GLFW_KEY_A -> Key.KEY_A;
            case GLFW_KEY_B -> Key.KEY_B;
            case GLFW_KEY_C -> Key.KEY_C;
            case GLFW_KEY_D -> Key.KEY_D;
            case GLFW_KEY_E -> Key.KEY_E;
            case GLFW_KEY_F -> Key.KEY_F;
            case GLFW_KEY_G -> Key.KEY_G;
            case GLFW_KEY_H -> Key.KEY_H;
            case GLFW_KEY_I -> Key.KEY_I;
            case GLFW_KEY_J -> Key.KEY_J;
            case GLFW_KEY_K -> Key.KEY_K;
            case GLFW_KEY_L -> Key.KEY_L;
            case GLFW_KEY_M -> Key.KEY_M;
            case GLFW_KEY_N -> Key.KEY_N;
            case GLFW_KEY_O -> Key.KEY_O;
            case GLFW_KEY_P -> Key.KEY_P;
            case GLFW_KEY_Q -> Key.KEY_Q;
            case GLFW_KEY_R -> Key.KEY_R;
            case GLFW_KEY_S -> Key.KEY_S;
            case GLFW_KEY_T -> Key.KEY_T;
            case GLFW_KEY_U -> Key.KEY_U;
            case GLFW_KEY_V -> Key.KEY_V;
            case GLFW_KEY_W -> Key.KEY_W;
            case GLFW_KEY_X -> Key.KEY_X;
            case GLFW_KEY_Y -> Key.KEY_Y;
            case GLFW_KEY_Z -> Key.KEY_Z;
            case GLFW_KEY_LEFT_BRACKET -> Key.KEY_LEFT_BRACKET;
            case GLFW_KEY_BACKSLASH -> Key.KEY_BACKSLASH;
            case GLFW_KEY_RIGHT_BRACKET -> Key.KEY_RIGHT_BRACKET;
            case GLFW_KEY_GRAVE_ACCENT -> Key.KEY_GRAVE_ACCENT;
            case GLFW_KEY_WORLD_1 -> Key.KEY_WORLD_1;
            case GLFW_KEY_WORLD_2 -> Key.KEY_WORLD_2;
            case GLFW_KEY_ESCAPE -> Key.KEY_ESCAPE;
            case GLFW_KEY_ENTER -> Key.KEY_ENTER;
            case GLFW_KEY_TAB -> Key.KEY_TAB;
            case GLFW_KEY_BACKSPACE -> Key.KEY_BACKSPACE;
            case GLFW_KEY_INSERT -> Key.KEY_INSERT;
            case GLFW_KEY_DELETE -> Key.KEY_DELETE;
            case GLFW_KEY_RIGHT -> Key.KEY_RIGHT;
            case GLFW_KEY_LEFT -> Key.KEY_LEFT;
            case GLFW_KEY_DOWN -> Key.KEY_DOWN;
            case GLFW_KEY_UP -> Key.KEY_UP;
            case GLFW_KEY_PAGE_UP -> Key.KEY_PAGE_UP;
            case GLFW_KEY_PAGE_DOWN -> Key.KEY_PAGE_DOWN;
            case GLFW_KEY_HOME -> Key.KEY_HOME;
            case GLFW_KEY_END -> Key.KEY_END;
            case GLFW_KEY_CAPS_LOCK -> Key.KEY_CAPS_LOCK;
            case GLFW_KEY_SCROLL_LOCK -> Key.KEY_SCROLL_LOCK;
            case GLFW_KEY_NUM_LOCK -> Key.KEY_NUM_LOCK;
            case GLFW_KEY_PRINT_SCREEN -> Key.KEY_PRINT_SCREEN;
            case GLFW_KEY_PAUSE -> Key.KEY_PAUSE;
            case GLFW_KEY_F1 -> Key.KEY_F1;
            case GLFW_KEY_F2 -> Key.KEY_F2;
            case GLFW_KEY_F3 -> Key.KEY_F3;
            case GLFW_KEY_F4 -> Key.KEY_F4;
            case GLFW_KEY_F5 -> Key.KEY_F5;
            case GLFW_KEY_F6 -> Key.KEY_F6;
            case GLFW_KEY_F7 -> Key.KEY_F7;
            case GLFW_KEY_F8 -> Key.KEY_F8;
            case GLFW_KEY_F9 -> Key.KEY_F9;
            case GLFW_KEY_F10 -> Key.KEY_F10;
            case GLFW_KEY_F11 -> Key.KEY_F11;
            case GLFW_KEY_F12 -> Key.KEY_F12;
            case GLFW_KEY_F13 -> Key.KEY_F13;
            case GLFW_KEY_F14 -> Key.KEY_F14;
            case GLFW_KEY_F15 -> Key.KEY_F15;
            case GLFW_KEY_F16 -> Key.KEY_F16;
            case GLFW_KEY_F17 -> Key.KEY_F17;
            case GLFW_KEY_F18 -> Key.KEY_F18;
            case GLFW_KEY_F19 -> Key.KEY_F19;
            case GLFW_KEY_F20 -> Key.KEY_F20;
            case GLFW_KEY_F21 -> Key.KEY_F21;
            case GLFW_KEY_F22 -> Key.KEY_F22;
            case GLFW_KEY_F23 -> Key.KEY_F23;
            case GLFW_KEY_F24 -> Key.KEY_F24;
            case GLFW_KEY_F25 -> Key.KEY_F25;
            case GLFW_KEY_KP_0 -> Key.KP_0;
            case GLFW_KEY_KP_1 -> Key.KP_1;
            case GLFW_KEY_KP_2 -> Key.KP_2;
            case GLFW_KEY_KP_3 -> Key.KP_3;
            case GLFW_KEY_KP_4 -> Key.KP_4;
            case GLFW_KEY_KP_5 -> Key.KP_5;
            case GLFW_KEY_KP_6 -> Key.KP_6;
            case GLFW_KEY_KP_7 -> Key.KP_7;
            case GLFW_KEY_KP_8 -> Key.KP_8;
            case GLFW_KEY_KP_9 -> Key.KP_9;
            case GLFW_KEY_KP_DECIMAL -> Key.KP_DECIMAL;
            case GLFW_KEY_KP_DIVIDE -> Key.KP_DIVIDE;
            case GLFW_KEY_KP_MULTIPLY -> Key.KP_MULTIPLY;
            case GLFW_KEY_KP_SUBTRACT -> Key.KP_SUBTRACT;
            case GLFW_KEY_KP_ADD -> Key.KP_ADD;
            case GLFW_KEY_KP_ENTER -> Key.KP_ENTER;
            case GLFW_KEY_KP_EQUAL -> Key.KP_EQUAL;
            case GLFW_KEY_LEFT_SHIFT -> Key.KEY_LEFT_SHIFT;
            case GLFW_KEY_LEFT_CONTROL -> Key.KEY_LEFT_CONTROL;
            case GLFW_KEY_LEFT_ALT -> Key.KEY_LEFT_ALT;
            case GLFW_KEY_LEFT_SUPER -> Key.KEY_LEFT_SUPER;
            case GLFW_KEY_RIGHT_SHIFT -> Key.KEY_RIGHT_SHIFT;
            case GLFW_KEY_RIGHT_CONTROL -> Key.KEY_RIGHT_CONTROL;
            case GLFW_KEY_RIGHT_ALT -> Key.KEY_RIGHT_ALT;
            case GLFW_KEY_RIGHT_SUPER -> Key.KEY_RIGHT_SUPER;
            case GLFW_KEY_MENU -> Key.KEY_MENU;
            default -> Key.KEY_UNKNOWN;
        };
    }
}
