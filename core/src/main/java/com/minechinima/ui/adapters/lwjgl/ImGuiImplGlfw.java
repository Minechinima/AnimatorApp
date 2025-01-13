package com.minechinima.ui.adapters.lwjgl;

import com.minechinima.input.raw.*;
import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import imgui.*;
import imgui.callback.*;
import imgui.lwjgl3.glfw.ImGuiImplGlfwNative;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.system.Callback;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ImGuiImplGlfw implements IInputEventListener<InputEvent> {
    protected static final String OS = System.getProperty("os.name", "generic").toLowerCase();
    protected static final boolean IS_WINDOWS;
    protected static final boolean IS_APPLE;
    protected Data data = null;
    private final Properties props = new Properties();
    private boolean glfwHawWindowTopmost;
    private boolean glfwHasWindowHovered;
    private boolean glfwHasWindowAlpha;
    private boolean glfwHasPerMonitorDpi;
    private boolean glfwHasFocusWindow;
    private boolean glfwHasFocusOnShow;
    private boolean glfwHasMonitorWorkArea;
    private boolean glfwHasOsxWindowPosFix;
    private boolean glfwHasNewCursors;
    private boolean glfwHasMousePassthrough;
    private boolean glfwHasGamepadApi;
    private boolean glfwHasGetKeyName;
    private LWJGLInputEventManager inputEventManager;

    public ImGuiImplGlfw(LWJGLInputEventManager inputEventManager) {
        this.inputEventManager = inputEventManager;

        inputEventManager.addListener(this, InputEvent.class);
    }

    protected ImStrSupplier getClipboardTextFn() {
        return new ImStrSupplier() {
            public String get() {
                String clipboardString = GLFW.glfwGetClipboardString(ImGuiImplGlfw.this.data.window);
                return clipboardString != null ? clipboardString : "";
            }
        };
    }

    protected ImStrConsumer setClipboardTextFn() {
        return new ImStrConsumer() {
            public void accept(String text) {
                GLFW.glfwSetClipboardString(ImGuiImplGlfw.this.data.window, text);
            }
        };
    }

    protected int glfwKeyToImGuiKey(int glfwKey) {
        switch (glfwKey) {
            case 32:
                return 524;
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 40:
            case 41:
            case 42:
            case 43:
            case 58:
            case 60:
            case 62:
            case 63:
            case 64:
            case 94:
            case 95:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case 255:
            case 270:
            case 271:
            case 272:
            case 273:
            case 274:
            case 275:
            case 276:
            case 277:
            case 278:
            case 279:
            case 285:
            case 286:
            case 287:
            case 288:
            case 289:
            case 302:
            case 303:
            case 304:
            case 305:
            case 306:
            case 307:
            case 308:
            case 309:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 315:
            case 316:
            case 317:
            case 318:
            case 319:
            case 337:
            case 338:
            case 339:
            default:
                return 0;
            case 39:
                return 584;
            case 44:
                return 585;
            case 45:
                return 586;
            case 46:
                return 587;
            case 47:
                return 588;
            case 48:
                return 536;
            case 49:
                return 537;
            case 50:
                return 538;
            case 51:
                return 539;
            case 52:
                return 540;
            case 53:
                return 541;
            case 54:
                return 542;
            case 55:
                return 543;
            case 56:
                return 544;
            case 57:
                return 545;
            case 59:
                return 589;
            case 61:
                return 590;
            case 65:
                return 546;
            case 66:
                return 547;
            case 67:
                return 548;
            case 68:
                return 549;
            case 69:
                return 550;
            case 70:
                return 551;
            case 71:
                return 552;
            case 72:
                return 553;
            case 73:
                return 554;
            case 74:
                return 555;
            case 75:
                return 556;
            case 76:
                return 557;
            case 77:
                return 558;
            case 78:
                return 559;
            case 79:
                return 560;
            case 80:
                return 561;
            case 81:
                return 562;
            case 82:
                return 563;
            case 83:
                return 564;
            case 84:
                return 565;
            case 85:
                return 566;
            case 86:
                return 567;
            case 87:
                return 568;
            case 88:
                return 569;
            case 89:
                return 570;
            case 90:
                return 571;
            case 91:
                return 591;
            case 92:
                return 592;
            case 93:
                return 593;
            case 96:
                return 594;
            case 256:
                return 526;
            case 257:
                return 525;
            case 258:
                return 512;
            case 259:
                return 523;
            case 260:
                return 521;
            case 261:
                return 522;
            case 262:
                return 514;
            case 263:
                return 513;
            case 264:
                return 516;
            case 265:
                return 515;
            case 266:
                return 517;
            case 267:
                return 518;
            case 268:
                return 519;
            case 269:
                return 520;
            case 280:
                return 595;
            case 281:
                return 596;
            case 282:
                return 597;
            case 283:
                return 598;
            case 284:
                return 599;
            case 290:
                return 572;
            case 291:
                return 573;
            case 292:
                return 574;
            case 293:
                return 575;
            case 294:
                return 576;
            case 295:
                return 577;
            case 296:
                return 578;
            case 297:
                return 579;
            case 298:
                return 580;
            case 299:
                return 581;
            case 300:
                return 582;
            case 301:
                return 583;
            case 320:
                return 600;
            case 321:
                return 601;
            case 322:
                return 602;
            case 323:
                return 603;
            case 324:
                return 604;
            case 325:
                return 605;
            case 326:
                return 606;
            case 327:
                return 607;
            case 328:
                return 608;
            case 329:
                return 609;
            case 330:
                return 610;
            case 331:
                return 611;
            case 332:
                return 612;
            case 333:
                return 613;
            case 334:
                return 614;
            case 335:
                return 615;
            case 336:
                return 616;
            case 340:
                return 528;
            case 341:
                return 527;
            case 342:
                return 529;
            case 343:
                return 530;
            case 344:
                return 532;
            case 345:
                return 531;
            case 346:
                return 533;
            case 347:
                return 534;
            case 348:
                return 535;
        }
    }

    protected void updateKeyModifiers(KeyEvent event) {
        ImGuiIO io = ImGui.getIO();
        //io.addKeyEvent(641, event.ctrlPressed());
        //io.addKeyEvent(642, event.shiftPressed());
        //io.addKeyEvent(643, event.altPressed());
        //io.addKeyEvent(644, event.superPressed());
    }

    public void mouseButtonCallback(KeyEvent event) {
        /*
        if (this.data.prevUserCallbackMousebutton != null && window == this.data.window) {
            this.data.prevUserCallbackMousebutton.invoke(window, button, action, mods);
        }
         */

        this.updateKeyModifiers(event);
        ImGuiIO io = ImGui.getIO();
        int button = switch (event.getCurrentKey()) {
            case MOUSE_BUTTON_1 -> 0;
            case MOUSE_BUTTON_2 -> 1;
            case MOUSE_BUTTON_3 -> 2;
            case MOUSE_BUTTON_4 -> 3;
            case MOUSE_BUTTON_5 -> 4;
            case MOUSE_BUTTON_6 -> 6;
                  default -> -1;
        };

        if (button >= 0 && button < 5) {
            io.addMouseButtonEvent(button, event.getInputType() == InputType.PRESS);
        }
    }

    public void scrollCallback(long window, double xOffset, double yOffset) {
        if (this.data.prevUserCallbackScroll != null && window == this.data.window) {
            this.data.prevUserCallbackScroll.invoke(window, xOffset, yOffset);
        }

        ImGuiIO io = ImGui.getIO();
        io.addMouseWheelEvent((float)xOffset, (float)yOffset);
    }

    protected int translateUntranslatedKey(int key, int scancode) {
        if (!this.glfwHasGetKeyName) {
            return key;
        } else if (key >= 320 && key <= 336) {
            return key;
        } else {
            int resultKey = key;
            String keyName = GLFW.glfwGetKeyName(key, scancode);
            if (keyName != null && keyName.length() > 2 && keyName.charAt(0) != 0 && keyName.charAt(1) == 0) {
                if (keyName.charAt(0) >= '0' && keyName.charAt(0) <= '9') {
                    resultKey = 48 + (keyName.charAt(0) - 48);
                } else if (keyName.charAt(0) >= 'A' && keyName.charAt(0) <= 'Z') {
                    resultKey = 65 + (keyName.charAt(0) - 65);
                } else {
                    int index = "`-=[]\\,;'./".indexOf(keyName.charAt(0));
                    if (index != -1) {
                        resultKey = this.props.charKeys[index];
                    }
                }
            }

            return resultKey;
        }
    }

    public void keyCallback(KeyEvent event) {
        /*
        if (this.data.prevUserCallbackKey != null && window == this.data.window) {
            this.data.prevUserCallbackKey.invoke(window, keycode, scancode, action, mods);
        }
         */

        if (event.getInputType() == InputType.PRESS || event.getInputType() == InputType.RELEASE) {
            this.updateKeyModifiers(event);
            /*
            if (keycode >= 0 && keycode < this.data.keyOwnerWindows.length) {
                this.data.keyOwnerWindows[keycode] = action == 1 ? window : -1L;
            }
             */

            //int key = this.translateUntranslatedKey(keycode, scancode);
            ImGuiIO io = ImGui.getIO();
            int imguiKey = this.glfwKeyToImGuiKey(event.getCurrentKey().imguiCode());
            io.addKeyEvent(imguiKey, event.getInputType() == InputType.PRESS);
            //io.setKeyEventNativeData(imguiKey, k, scancode);
        }
    }

    public void windowFocusCallback(long window, boolean focused) {
        if (this.data.prevUserCallbackWindowFocus != null && window == this.data.window) {
            this.data.prevUserCallbackWindowFocus.invoke(window, focused);
        }

        ImGui.getIO().addFocusEvent(focused);
    }

    public void cursorPosCallback(long window, double x, double y) {
        if (this.data.prevUserCallbackCursorPos != null && window == this.data.window) {
            this.data.prevUserCallbackCursorPos.invoke(window, x, y);
        }

        float posX = (float)x;
        float posY = (float)y;
        ImGuiIO io = ImGui.getIO();
        if (io.hasConfigFlags(1024)) {
            GLFW.glfwGetWindowPos(window, this.props.windowX, this.props.windowY);
            posX += (float)this.props.windowX[0];
            posY += (float)this.props.windowY[0];
        }

        io.addMousePosEvent(posX, posY);
        this.data.lastValidMousePos.set(posX, posY);
    }

    public void cursorEnterCallback(long window, boolean entered) {
        if (this.data.prevUserCallbackCursorEnter != null && window == this.data.window) {
            this.data.prevUserCallbackCursorEnter.invoke(window, entered);
        }

        ImGuiIO io = ImGui.getIO();
        if (entered) {
            this.data.mouseWindow = window;
            io.addMousePosEvent(this.data.lastValidMousePos.x, this.data.lastValidMousePos.y);
        } else if (this.data.mouseWindow == window) {
            io.getMousePos(this.data.lastValidMousePos);
            this.data.mouseWindow = -1L;
            io.addMousePosEvent(Float.MIN_VALUE, Float.MIN_VALUE);
        }

    }

    public void charCallback(long window, int c) {
        if (this.data.prevUserCallbackChar != null && window == this.data.window) {
            this.data.prevUserCallbackChar.invoke(window, c);
        }

        ImGui.getIO().addInputCharacter(c);
    }

    public void monitorCallback(long window, int event) {
        this.data.wantUpdateMonitors = true;
    }

    public void installCallbacks(long window) {
        this.data.prevUserCallbackWindowFocus = GLFW.glfwSetWindowFocusCallback(window, this::windowFocusCallback);
        this.data.prevUserCallbackCursorEnter = GLFW.glfwSetCursorEnterCallback(window, this::cursorEnterCallback);
        //this.data.prevUserCallbackCursorPos = GLFW.glfwSetCursorPosCallback(window, inputEventManager::onCursor);
        //this.data.prevUserCallbackMousebutton = GLFW.glfwSetMouseButtonCallback(window, inputEventManager::onMouseButton);
        //this.data.prevUserCallbackScroll = GLFW.glfwSetScrollCallback(window, inputEventManager::onScroll);
        //this.data.prevUserCallbackKey = GLFW.glfwSetKeyCallback(window, inputEventManager::onKey);
        this.data.prevUserCallbackChar = GLFW.glfwSetCharCallback(window, this::charCallback);
        this.data.prevUserCallbackMonitor = GLFW.glfwSetMonitorCallback(this::monitorCallback);
        this.data.installedCallbacks = true;
    }

    protected void freeCallback(Callback cb) {
        if (cb != null) {
            cb.free();
        }

    }

    public void restoreCallbacks(long window) {
        this.freeCallback(GLFW.glfwSetWindowFocusCallback(window, this.data.prevUserCallbackWindowFocus));
        this.freeCallback(GLFW.glfwSetCursorEnterCallback(window, this.data.prevUserCallbackCursorEnter));
        this.freeCallback(GLFW.glfwSetCursorPosCallback(window, this.data.prevUserCallbackCursorPos));
        this.freeCallback(GLFW.glfwSetMouseButtonCallback(window, this.data.prevUserCallbackMousebutton));
        this.freeCallback(GLFW.glfwSetScrollCallback(window, this.data.prevUserCallbackScroll));
        this.freeCallback(GLFW.glfwSetKeyCallback(window, this.data.prevUserCallbackKey));
        this.freeCallback(GLFW.glfwSetCharCallback(window, this.data.prevUserCallbackChar));
        this.freeCallback(GLFW.glfwSetMonitorCallback(this.data.prevUserCallbackMonitor));
        this.data.installedCallbacks = false;
        this.data.prevUserCallbackWindowFocus = null;
        this.data.prevUserCallbackCursorEnter = null;
        this.data.prevUserCallbackCursorPos = null;
        this.data.prevUserCallbackMousebutton = null;
        this.data.prevUserCallbackScroll = null;
        this.data.prevUserCallbackKey = null;
        this.data.prevUserCallbackChar = null;
        this.data.prevUserCallbackMonitor = null;
    }

    protected Data newData() {
        return new Data();
    }

    public boolean init(long window, boolean installCallbacks) {
        int[] major = new int[1];
        int[] minor = new int[1];
        int[] rev = new int[1];
        GLFW.glfwGetVersion(major, minor, rev);
        int version = major[0] * 1000 + minor[0] * 100 + rev[0] * 10;
        this.glfwHawWindowTopmost = version >= 3200;
        this.glfwHasWindowHovered = version >= 3300;
        this.glfwHasWindowAlpha = version >= 3300;
        this.glfwHasPerMonitorDpi = version >= 3300;
        this.glfwHasFocusWindow = version >= 3200;
        this.glfwHasFocusOnShow = version >= 3300;
        this.glfwHasMonitorWorkArea = version >= 3300;
        this.glfwHasOsxWindowPosFix = version >= 3310;
        this.glfwHasNewCursors = version >= 3400;
        this.glfwHasMousePassthrough = version >= 3400;
        this.glfwHasGamepadApi = version >= 3300;
        this.glfwHasGetKeyName = version >= 3200;
        ImGuiIO io = ImGui.getIO();
        io.setBackendPlatformName("imgui-java_impl_glfw");
        io.addBackendFlags(1030);
        if (this.glfwHasMousePassthrough || this.glfwHasWindowHovered && IS_WINDOWS) {
            io.addBackendFlags(2048);
        }

        this.data = this.newData();
        this.data.window = window;
        this.data.time = (double)0.0F;
        this.data.wantUpdateMonitors = true;
        io.setGetClipboardTextFn(this.getClipboardTextFn());
        io.setSetClipboardTextFn(this.setClipboardTextFn());
        GLFWErrorCallback prevErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI) null);
        this.data.mouseCursors[0] = GLFW.glfwCreateStandardCursor(221185);
        this.data.mouseCursors[1] = GLFW.glfwCreateStandardCursor(221186);
        this.data.mouseCursors[3] = GLFW.glfwCreateStandardCursor(221190);
        this.data.mouseCursors[4] = GLFW.glfwCreateStandardCursor(221189);
        this.data.mouseCursors[7] = GLFW.glfwCreateStandardCursor(221188);
        if (this.glfwHasNewCursors) {
            this.data.mouseCursors[2] = GLFW.glfwCreateStandardCursor(221193);
            this.data.mouseCursors[5] = GLFW.glfwCreateStandardCursor(221192);
            this.data.mouseCursors[6] = GLFW.glfwCreateStandardCursor(221191);
            this.data.mouseCursors[8] = GLFW.glfwCreateStandardCursor(221194);
        } else {
            this.data.mouseCursors[2] = GLFW.glfwCreateStandardCursor(221185);
            this.data.mouseCursors[5] = GLFW.glfwCreateStandardCursor(221185);
            this.data.mouseCursors[6] = GLFW.glfwCreateStandardCursor(221185);
            this.data.mouseCursors[8] = GLFW.glfwCreateStandardCursor(221185);
        }

        GLFW.glfwSetErrorCallback(prevErrorCallback);
        if (installCallbacks) {
            this.installCallbacks(window);
        }

        this.updateMonitors();
        GLFW.glfwSetMonitorCallback(this::monitorCallback);
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        mainViewport.setPlatformHandle(window);
        if (IS_WINDOWS) {
            mainViewport.setPlatformHandleRaw(GLFWNativeWin32.glfwGetWin32Window(window));
        }

        if (io.hasConfigFlags(1024)) {
            this.initPlatformInterface();
        }

        return true;
    }

    public void shutdown() {
        ImGuiIO io = ImGui.getIO();
        this.shutdownPlatformInterface();
        if (this.data.installedCallbacks) {
            this.restoreCallbacks(this.data.window);
        }

        for(int cursorN = 0; cursorN < 9; ++cursorN) {
            GLFW.glfwDestroyCursor(this.data.mouseCursors[cursorN]);
        }

        io.setBackendPlatformName((String)null);
        this.data = null;
    }

    protected void updateMouseData() {
        ImGuiIO io = ImGui.getIO();
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        int mouseViewportId = 0;
        io.getMousePos(this.props.mousePosPrev);

        for(int n = 0; n < platformIO.getViewportsSize(); ++n) {
            ImGuiViewport viewport = platformIO.getViewports(n);
            long window = viewport.getPlatformHandle();
            boolean isWindowFocused = GLFW.glfwGetWindowAttrib(window, 131073) != 0;
            if (isWindowFocused) {
                if (io.getWantSetMousePos()) {
                    GLFW.glfwSetCursorPos(window, (double)(this.props.mousePosPrev.x - viewport.getPosX()), (double)(this.props.mousePosPrev.y - viewport.getPosY()));
                }

                if (this.data.mouseWindow == -1L) {
                    GLFW.glfwGetCursorPos(window, this.props.mouseX, this.props.mouseY);
                    double mouseX = this.props.mouseX[0];
                    double mouseY = this.props.mouseY[0];
                    if (io.hasConfigFlags(1024)) {
                        GLFW.glfwGetWindowPos(window, this.props.windowX, this.props.windowY);
                        mouseX += (double)this.props.windowX[0];
                        mouseY += (double)this.props.windowY[0];
                    }

                    this.data.lastValidMousePos.set((float)mouseX, (float)mouseY);
                    io.addMousePosEvent((float)mouseX, (float)mouseY);
                }
            }

            if (this.glfwHasMousePassthrough || this.glfwHasWindowHovered && IS_WINDOWS) {
                boolean windowNoInput = viewport.hasFlags(128);
                if (this.glfwHasMousePassthrough) {
                    GLFW.glfwSetWindowAttrib(window, 131085, windowNoInput ? 1 : 0);
                }

                if (GLFW.glfwGetWindowAttrib(window, 131083) == 1 && !windowNoInput) {
                    mouseViewportId = viewport.getID();
                }
            }
        }

        if (io.hasBackendFlags(2048)) {
            io.addMouseViewportEvent(mouseViewportId);
        }

    }

    protected void updateMouseCursor() {
        ImGuiIO io = ImGui.getIO();
        if (!io.hasConfigFlags(32) && GLFW.glfwGetInputMode(this.data.window, 208897) != 212995) {
            int imguiCursor = ImGui.getMouseCursor();
            ImGuiPlatformIO platformIO = ImGui.getPlatformIO();

            for(int n = 0; n < platformIO.getViewportsSize(); ++n) {
                long windowPtr = platformIO.getViewports(n).getPlatformHandle();
                if (imguiCursor != -1 && !io.getMouseDrawCursor()) {
                    GLFW.glfwSetCursor(windowPtr, this.data.mouseCursors[imguiCursor] != 0L ? this.data.mouseCursors[imguiCursor] : this.data.mouseCursors[0]);
                    GLFW.glfwSetInputMode(windowPtr, 208897, 212993);
                } else {
                    GLFW.glfwSetInputMode(windowPtr, 208897, 212994);
                }
            }

        }
    }

    private float saturate(float v) {
        return v < 0.0F ? 0.0F : (v > 1.0F ? 1.0F : v);
    }

    protected void updateGamepads() {
        ImGuiIO io = ImGui.getIO();
        if (io.hasConfigFlags(2)) {
            io.removeBackendFlags(1);
            MapButton mapButton;
            MapAnalog mapAnalog;
            if (this.glfwHasGamepadApi) {
                label148: {
                    try (GLFWGamepadState gamepad = GLFWGamepadState.create()) {
                        if (GLFW.glfwGetGamepadState(0, gamepad)) {
                            mapButton = (keyNo, buttonNo, _unused) -> io.addKeyEvent(keyNo, gamepad.buttons(buttonNo) != 0);
                            mapAnalog = (keyNo, axisNo, _unused, v0, v1) -> {
                                float v = gamepad.axes(axisNo);
                                v = (v - v0) / (v1 - v0);
                                io.addKeyAnalogEvent(keyNo, v > 0.1F, this.saturate(v));
                            };
                            break label148;
                        }
                    }

                    return;
                }
            } else {
                FloatBuffer axes = GLFW.glfwGetJoystickAxes(0);
                ByteBuffer buttons = GLFW.glfwGetJoystickButtons(0);
                if (axes == null || axes.limit() == 0 || buttons == null || buttons.limit() == 0) {
                    return;
                }

                mapButton = (keyNo, buttonNo, _unused) -> io.addKeyEvent(keyNo, buttons.limit() > buttonNo && buttons.get(buttonNo) == 1);
                mapAnalog = (keyNo, axisNo, _unused, v0, v1) -> {
                    float v = axes.limit() > axisNo ? axes.get(axisNo) : v0;
                    v = (v - v0) / (v1 - v0);
                    io.addKeyAnalogEvent(keyNo, v > 0.1F, this.saturate(v));
                };
            }

            io.addBackendFlags(1);
            mapButton.run(617, 7, 7);
            mapButton.run(618, 6, 6);
            mapButton.run(620, 0, 0);
            mapButton.run(622, 1, 1);
            mapButton.run(621, 2, 2);
            mapButton.run(619, 3, 3);
            mapButton.run(625, 14, 13);
            mapButton.run(626, 12, 11);
            mapButton.run(623, 11, 10);
            mapButton.run(624, 13, 12);
            mapButton.run(627, 4, 4);
            mapButton.run(628, 5, 5);
            mapAnalog.run(629, 4, 4, -0.75F, 1.0F);
            mapAnalog.run(630, 5, 5, -0.75F, 1.0F);
            mapButton.run(631, 9, 8);
            mapButton.run(632, 10, 9);
            mapAnalog.run(635, 0, 0, -0.25F, -1.0F);
            mapAnalog.run(636, 0, 0, 0.25F, 1.0F);
            mapAnalog.run(633, 1, 1, -0.25F, -1.0F);
            mapAnalog.run(634, 1, 1, 0.25F, 1.0F);
            mapAnalog.run(639, 2, 2, -0.25F, -1.0F);
            mapAnalog.run(640, 2, 2, 0.25F, 1.0F);
            mapAnalog.run(637, 3, 3, -0.25F, -1.0F);
            mapAnalog.run(638, 3, 3, 0.25F, 1.0F);
        }
    }

    protected void updateMonitors() {
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        PointerBuffer monitors = GLFW.glfwGetMonitors();
        if (monitors == null) {
            System.err.println("Unable to get monitors!");
        } else {
            platformIO.resizeMonitors(0);

            for(int n = 0; n < monitors.limit(); ++n) {
                long monitor = monitors.get(n);
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
                if (vidMode == null) {
                    System.err.println("Unable to get video mode!");
                    return;
                }

                GLFW.glfwGetMonitorPos(monitor, this.props.monitorX, this.props.monitorY);
                float mainPosX = (float)this.props.monitorX[0];
                float mainPosY = (float)this.props.monitorY[0];
                float mainSizeX = (float)vidMode.width();
                float mainSizeY = (float)vidMode.height();
                float workPosX = 0.0F;
                float workPosY = 0.0F;
                float workSizeX = 0.0F;
                float workSizeY = 0.0F;
                if (this.glfwHasMonitorWorkArea) {
                    GLFW.glfwGetMonitorWorkarea(monitor, this.props.monitorWorkAreaX, this.props.monitorWorkAreaY, this.props.monitorWorkAreaWidth, this.props.monitorWorkAreaHeight);
                    if (this.props.monitorWorkAreaWidth[0] > 0 && this.props.monitorWorkAreaHeight[0] > 0) {
                        workPosX = (float)this.props.monitorWorkAreaX[0];
                        workPosY = (float)this.props.monitorWorkAreaY[0];
                        workSizeX = (float)this.props.monitorWorkAreaWidth[0];
                        workSizeY = (float)this.props.monitorWorkAreaHeight[0];
                    }
                }

                float dpiScale = 0.0F;
                if (this.glfwHasPerMonitorDpi) {
                    GLFW.glfwGetMonitorContentScale(monitor, this.props.monitorContentScaleX, this.props.monitorContentScaleY);
                    dpiScale = this.props.monitorContentScaleX[0];
                }

                platformIO.pushMonitors(mainPosX, mainPosY, mainSizeX, mainSizeY, workPosX, workPosY, workSizeX, workSizeY, dpiScale);
            }

            this.data.wantUpdateMonitors = false;
        }
    }

    public void newFrame() {
        ImGuiIO io = ImGui.getIO();
        GLFW.glfwGetWindowSize(this.data.window, this.props.windowW, this.props.windowH);
        GLFW.glfwGetFramebufferSize(this.data.window, this.props.displayW, this.props.displayH);
        io.setDisplaySize((float)this.props.windowW[0], (float)this.props.windowH[0]);
        if (this.props.windowW[0] > 0 && this.props.windowH[0] > 0) {
            float scaleX = (float)this.props.displayW[0] / (float)this.props.windowW[0];
            float scaleY = (float)this.props.displayH[0] / (float)this.props.windowH[0];
            io.setDisplayFramebufferScale(scaleX, scaleY);
        }

        if (this.data.wantUpdateMonitors) {
            this.updateMonitors();
        }

        double currentTime = GLFW.glfwGetTime();
        io.setDeltaTime(this.data.time > (double)0.0F ? (float)(currentTime - this.data.time) : 0.016666668F);
        this.data.time = currentTime;
        this.updateMouseData();
        this.updateMouseCursor();
        this.updateGamepads();
    }

    private void windowCloseCallback(long windowId) {
        ImGuiViewport vp = ImGui.findViewportByPlatformHandle(windowId);
        if (vp.isValidPtr()) {
            vp.setPlatformRequestClose(true);
        }

    }

    private void windowPosCallback(long windowId, int xPos, int yPos) {
        ImGuiViewport vp = ImGui.findViewportByPlatformHandle(windowId);
        if (!vp.isNotValidPtr()) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                boolean ignoreEvent = ImGui.getFrameCount() <= vd.ignoreWindowPosEventFrame + 1;
                if (ignoreEvent) {
                    return;
                }
            }

            vp.setPlatformRequestMove(true);
        }
    }

    private void windowSizeCallback(long windowId, int width, int height) {
        ImGuiViewport vp = ImGui.findViewportByPlatformHandle(windowId);
        if (!vp.isNotValidPtr()) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                boolean ignoreEvent = ImGui.getFrameCount() <= vd.ignoreWindowSizeEventFrame + 1;
                if (ignoreEvent) {
                    return;
                }
            }

            vp.setPlatformRequestResize(true);
        }
    }

    protected void initPlatformInterface() {
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        platformIO.setPlatformCreateWindow(new CreateWindowFunction());
        platformIO.setPlatformDestroyWindow(new DestroyWindowFunction());
        platformIO.setPlatformShowWindow(new ShowWindowFunction());
        platformIO.setPlatformGetWindowPos(new GetWindowPosFunction());
        platformIO.setPlatformSetWindowPos(new SetWindowPosFunction());
        platformIO.setPlatformGetWindowSize(new GetWindowSizeFunction());
        platformIO.setPlatformSetWindowSize(new SetWindowSizeFunction());
        platformIO.setPlatformSetWindowTitle(new SetWindowTitleFunction());
        platformIO.setPlatformSetWindowFocus(new SetWindowFocusFunction());
        platformIO.setPlatformGetWindowFocus(new GetWindowFocusFunction());
        platformIO.setPlatformGetWindowMinimized(new GetWindowMinimizedFunction());
        platformIO.setPlatformSetWindowAlpha(new SetWindowAlphaFunction());
        platformIO.setPlatformRenderWindow(new RenderWindowFunction());
        platformIO.setPlatformSwapBuffers(new SwapBuffersFunction());
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ViewportData vd = new ViewportData();
        vd.window = this.data.window;
        vd.windowOwned = false;
        mainViewport.setPlatformUserData(vd);
        mainViewport.setPlatformHandle(this.data.window);
    }

    protected void shutdownPlatformInterface() {
        ImGui.destroyPlatformWindows();
    }

    static {
        IS_WINDOWS = OS.contains("win");
        IS_APPLE = OS.contains("mac") || OS.contains("darwin");
    }

    @Override
    public void onEvent(InputEvent event) {
        if (event instanceof KeyEvent) {
            this.onKey((KeyEvent) event);
        } else if (event instanceof MouseEvent) {
            this.onMouse((MouseEvent) event);
        } else if (event instanceof ScrollEvent) {
            this.onScroll((ScrollEvent) event);
        }
    }

    public void onKey(KeyEvent event) {
        switch (event.getInputType()) {
            case PRESS, RELEASE, REPEAT -> {
                if (event.getCurrentKey().isMouse()) {
                    this.mouseButtonCallback(event);
                } else {
                    this.keyCallback(event);
                }
            }
        }
    }

    public void onMouse(MouseEvent event) {
        this.cursorPosCallback(event.getWindow(), event.getX(), event.getY());
    }

    public void onScroll(ScrollEvent event) {
        this.scrollCallback(event.getWindow(), event.getXOffset(), event.getYOffset());
    }

    protected static class Data {
        protected long window = -1L;
        protected double time = (double)0.0F;
        protected long mouseWindow = -1L;
        protected long[] mouseCursors = new long[9];
        protected ImVec2 lastValidMousePos = new ImVec2();
        protected long[] keyOwnerWindows = new long[348];
        protected boolean installedCallbacks = false;
        protected boolean wantUpdateMonitors = true;
        protected GLFWWindowFocusCallback prevUserCallbackWindowFocus = null;
        protected GLFWCursorPosCallback prevUserCallbackCursorPos = null;
        protected GLFWCursorEnterCallback prevUserCallbackCursorEnter = null;
        protected GLFWMouseButtonCallback prevUserCallbackMousebutton = null;
        protected GLFWScrollCallback prevUserCallbackScroll = null;
        protected GLFWKeyCallback prevUserCallbackKey = null;
        protected GLFWCharCallback prevUserCallbackChar = null;
        protected GLFWMonitorCallback prevUserCallbackMonitor = null;

        protected Data() {
        }
    }

    private static final class Properties {
        private final int[] windowW;
        private final int[] windowH;
        private final int[] windowX;
        private final int[] windowY;
        private final int[] displayW;
        private final int[] displayH;
        private final ImVec2 mousePosPrev;
        private final double[] mouseX;
        private final double[] mouseY;
        private final int[] monitorX;
        private final int[] monitorY;
        private final int[] monitorWorkAreaX;
        private final int[] monitorWorkAreaY;
        private final int[] monitorWorkAreaWidth;
        private final int[] monitorWorkAreaHeight;
        private final float[] monitorContentScaleX;
        private final float[] monitorContentScaleY;
        private final String charNames;
        private final int[] charKeys;

        private Properties() {
            this.windowW = new int[1];
            this.windowH = new int[1];
            this.windowX = new int[1];
            this.windowY = new int[1];
            this.displayW = new int[1];
            this.displayH = new int[1];
            this.mousePosPrev = new ImVec2();
            this.mouseX = new double[1];
            this.mouseY = new double[1];
            this.monitorX = new int[1];
            this.monitorY = new int[1];
            this.monitorWorkAreaX = new int[1];
            this.monitorWorkAreaY = new int[1];
            this.monitorWorkAreaWidth = new int[1];
            this.monitorWorkAreaHeight = new int[1];
            this.monitorContentScaleX = new float[1];
            this.monitorContentScaleY = new float[1];
            this.charNames = "`-=[]\\,;'./";
            this.charKeys = new int[]{96, 45, 61, 91, 93, 92, 44, 59, 39, 46, 47};
        }
    }

    private static final class ViewportData {
        long window;
        boolean windowOwned;
        int ignoreWindowPosEventFrame;
        int ignoreWindowSizeEventFrame;

        private ViewportData() {
            this.window = -1L;
            this.windowOwned = false;
            this.ignoreWindowPosEventFrame = -1;
            this.ignoreWindowSizeEventFrame = -1;
        }
    }

    private final class CreateWindowFunction extends ImPlatformFuncViewport {
        private CreateWindowFunction() {
        }

        public void accept(ImGuiViewport vp) {
            ViewportData vd = new ViewportData();
            vp.setPlatformUserData(vd);
            GLFW.glfwWindowHint(131076, 0);
            GLFW.glfwWindowHint(131073, 0);
            if (ImGuiImplGlfw.this.glfwHasFocusOnShow) {
                GLFW.glfwWindowHint(131084, 0);
            }

            GLFW.glfwWindowHint(131077, vp.hasFlags(8) ? 0 : 1);
            if (ImGuiImplGlfw.this.glfwHawWindowTopmost) {
                GLFW.glfwWindowHint(131079, vp.hasFlags(512) ? 1 : 0);
            }

            vd.window = GLFW.glfwCreateWindow((int)vp.getSizeX(), (int)vp.getSizeY(), "No Title Yet", 0L, ImGuiImplGlfw.this.data.window);
            vd.windowOwned = true;
            vp.setPlatformHandle(vd.window);
            if (ImGuiImplGlfw.IS_WINDOWS) {
                vp.setPlatformHandleRaw(GLFWNativeWin32.glfwGetWin32Window(vd.window));
            }

            GLFW.glfwSetWindowPos(vd.window, (int)vp.getPosX(), (int)vp.getPosY());
            GLFW.glfwSetWindowFocusCallback(vd.window, ImGuiImplGlfw.this::windowFocusCallback);
            GLFW.glfwSetCursorEnterCallback(vd.window, ImGuiImplGlfw.this::cursorEnterCallback);
            GLFW.glfwSetCursorPosCallback(vd.window, inputEventManager::onCursor);
            GLFW.glfwSetMouseButtonCallback(vd.window, inputEventManager::onMouseButton);
            GLFW.glfwSetScrollCallback(vd.window, inputEventManager::onScroll);
            GLFW.glfwSetKeyCallback(vd.window, inputEventManager::onKey);
            GLFW.glfwSetCharCallback(vd.window, ImGuiImplGlfw.this::charCallback);
            GLFW.glfwSetWindowCloseCallback(vd.window, (x$0) -> ImGuiImplGlfw.this.windowCloseCallback(x$0));
            GLFW.glfwSetWindowPosCallback(vd.window, (x$0, x$1, x$2) -> ImGuiImplGlfw.this.windowPosCallback(x$0, x$1, x$2));
            GLFW.glfwSetWindowSizeCallback(vd.window, (x$0, x$1, x$2) -> ImGuiImplGlfw.this.windowSizeCallback(x$0, x$1, x$2));
            GLFW.glfwMakeContextCurrent(vd.window);
            GLFW.glfwSwapInterval(0);
        }
    }

    private final class DestroyWindowFunction extends ImPlatformFuncViewport {
        private DestroyWindowFunction() {
        }

        public void accept(ImGuiViewport vp) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null && vd.windowOwned) {
                if (!ImGuiImplGlfw.this.glfwHasMousePassthrough && ImGuiImplGlfw.this.glfwHasWindowHovered && ImGuiImplGlfw.IS_WINDOWS) {
                }

                for(int i = 0; i < ImGuiImplGlfw.this.data.keyOwnerWindows.length; ++i) {
                    if (ImGuiImplGlfw.this.data.keyOwnerWindows[i] == vd.window) {
                        inputEventManager.onKey(vd.window, i, 0, 0, 0);
                    }
                }

                Callbacks.glfwFreeCallbacks(vd.window);
                GLFW.glfwDestroyWindow(vd.window);
                vd.window = -1L;
            }

            vp.setPlatformHandle(-1L);
            vp.setPlatformUserData((Object)null);
        }
    }

    private static final class ShowWindowFunction extends ImPlatformFuncViewport {
        private ShowWindowFunction() {
        }

        public void accept(ImGuiViewport vp) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                if (ImGuiImplGlfw.IS_WINDOWS && vp.hasFlags(16)) {
                    ImGuiImplGlfwNative.win32hideFromTaskBar(vp.getPlatformHandleRaw());
                }

                GLFW.glfwShowWindow(vd.window);
            }
        }
    }

    private static final class GetWindowPosFunction extends ImPlatformFuncViewportSuppImVec2 {
        private final int[] posX;
        private final int[] posY;

        private GetWindowPosFunction() {
            this.posX = new int[1];
            this.posY = new int[1];
        }

        public void get(ImGuiViewport vp, ImVec2 dst) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                this.posX[0] = 0;
                this.posY[0] = 0;
                GLFW.glfwGetWindowPos(vd.window, this.posX, this.posY);
                dst.set((float)this.posX[0], (float)this.posY[0]);
            }
        }
    }

    private static final class SetWindowPosFunction extends ImPlatformFuncViewportImVec2 {
        private SetWindowPosFunction() {
        }

        public void accept(ImGuiViewport vp, ImVec2 value) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                vd.ignoreWindowPosEventFrame = ImGui.getFrameCount();
                GLFW.glfwSetWindowPos(vd.window, (int)value.x, (int)value.y);
            }
        }
    }

    private static final class GetWindowSizeFunction extends ImPlatformFuncViewportSuppImVec2 {
        private final int[] width;
        private final int[] height;

        private GetWindowSizeFunction() {
            this.width = new int[1];
            this.height = new int[1];
        }

        public void get(ImGuiViewport vp, ImVec2 dst) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                this.width[0] = 0;
                this.height[0] = 0;
                GLFW.glfwGetWindowSize(vd.window, this.width, this.height);
                dst.x = (float)this.width[0];
                dst.y = (float)this.height[0];
            }
        }
    }

    private final class SetWindowSizeFunction extends ImPlatformFuncViewportImVec2 {
        private final int[] x;
        private final int[] y;
        private final int[] width;
        private final int[] height;

        private SetWindowSizeFunction() {
            this.x = new int[1];
            this.y = new int[1];
            this.width = new int[1];
            this.height = new int[1];
        }

        public void accept(ImGuiViewport vp, ImVec2 value) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                if (ImGuiImplGlfw.IS_APPLE && !ImGuiImplGlfw.this.glfwHasOsxWindowPosFix) {
                    this.x[0] = 0;
                    this.y[0] = 0;
                    this.width[0] = 0;
                    this.height[0] = 0;
                    GLFW.glfwGetWindowPos(vd.window, this.x, this.y);
                    GLFW.glfwGetWindowSize(vd.window, this.width, this.height);
                    GLFW.glfwSetWindowPos(vd.window, this.x[0], this.y[0] - this.height[0] + (int)value.y);
                }

                vd.ignoreWindowSizeEventFrame = ImGui.getFrameCount();
                GLFW.glfwSetWindowSize(vd.window, (int)value.x, (int)value.y);
            }
        }
    }

    private static final class SetWindowTitleFunction extends ImPlatformFuncViewportString {
        private SetWindowTitleFunction() {
        }

        public void accept(ImGuiViewport vp, String value) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                GLFW.glfwSetWindowTitle(vd.window, value);
            }

        }
    }

    private final class SetWindowFocusFunction extends ImPlatformFuncViewport {
        private SetWindowFocusFunction() {
        }

        public void accept(ImGuiViewport vp) {
            if (ImGuiImplGlfw.this.glfwHasFocusWindow) {
                ViewportData vd = (ViewportData)vp.getPlatformUserData();
                if (vd != null) {
                    GLFW.glfwFocusWindow(vd.window);
                }
            }

        }
    }

    private static final class GetWindowFocusFunction extends ImPlatformFuncViewportSuppBoolean {
        private GetWindowFocusFunction() {
        }

        public boolean get(ImGuiViewport vp) {
            ViewportData data = (ViewportData)vp.getPlatformUserData();
            return GLFW.glfwGetWindowAttrib(data.window, 131073) != 0;
        }
    }

    private static final class GetWindowMinimizedFunction extends ImPlatformFuncViewportSuppBoolean {
        private GetWindowMinimizedFunction() {
        }

        public boolean get(ImGuiViewport vp) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                return GLFW.glfwGetWindowAttrib(vd.window, 131074) != 0;
            } else {
                return false;
            }
        }
    }

    private final class SetWindowAlphaFunction extends ImPlatformFuncViewportFloat {
        private SetWindowAlphaFunction() {
        }

        public void accept(ImGuiViewport vp, float value) {
            if (ImGuiImplGlfw.this.glfwHasWindowAlpha) {
                ViewportData vd = (ViewportData)vp.getPlatformUserData();
                if (vd != null) {
                    GLFW.glfwSetWindowOpacity(vd.window, value);
                }
            }

        }
    }

    private static final class RenderWindowFunction extends ImPlatformFuncViewport {
        private RenderWindowFunction() {
        }

        public void accept(ImGuiViewport vp) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                GLFW.glfwMakeContextCurrent(vd.window);
            }

        }
    }

    private static final class SwapBuffersFunction extends ImPlatformFuncViewport {
        private SwapBuffersFunction() {
        }

        public void accept(ImGuiViewport vp) {
            ViewportData vd = (ViewportData)vp.getPlatformUserData();
            if (vd != null) {
                GLFW.glfwMakeContextCurrent(vd.window);
                GLFW.glfwSwapBuffers(vd.window);
            }

        }
    }

    @FunctionalInterface
    private interface MapAnalog {
        void run(int var1, int var2, int var3, float var4, float var5);
    }

    @FunctionalInterface
    private interface MapButton {
        void run(int var1, int var2, int var3);
    }
}
