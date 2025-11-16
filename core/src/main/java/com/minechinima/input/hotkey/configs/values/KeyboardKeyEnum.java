package com.minechinima.input.hotkey.configs.values;

import com.minechinima.input.hotkey.configs.AbstractHotKeyKeyConfigGroupStruct;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import imgui.flag.ImGuiKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * All available keyboard keys for the hotkey configuration.
 */
public enum KeyboardKeyEnum implements ILangKeyProvider, AbstractHotKeyKeyConfigGroupStruct.IKeyEnum {
    KEY_SPACE(ImGuiKey.Space),
    KEY_APOSTROPHE(ImGuiKey.Apostrophe),
    KEY_COMMA(ImGuiKey.Comma),
    KEY_MINUS(ImGuiKey.Minus),
    KEY_PERIOD(ImGuiKey.Period),
    KEY_SLASH(ImGuiKey.Slash),
    KEY_0(ImGuiKey._0),
    KEY_1(ImGuiKey._1),
    KEY_2(ImGuiKey._2),
    KEY_3(ImGuiKey._3),
    KEY_4(ImGuiKey._4),
    KEY_5(ImGuiKey._5),
    KEY_6(ImGuiKey._6),
    KEY_7(ImGuiKey._7),
    KEY_8(ImGuiKey._8),
    KEY_9(ImGuiKey._9),
    KEY_SEMICOLON(ImGuiKey.Semicolon),
    KEY_EQUAL(ImGuiKey.Equal),
    KEY_A(ImGuiKey.A),
    KEY_B(ImGuiKey.B),
    KEY_C(ImGuiKey.C),
    KEY_D(ImGuiKey.D),
    KEY_E(ImGuiKey.E),
    KEY_F(ImGuiKey.F),
    KEY_G(ImGuiKey.G),
    KEY_H(ImGuiKey.H),
    KEY_I(ImGuiKey.I),
    KEY_J(ImGuiKey.J),
    KEY_K(ImGuiKey.K),
    KEY_L(ImGuiKey.L),
    KEY_M(ImGuiKey.M),
    KEY_N(ImGuiKey.N),
    KEY_O(ImGuiKey.O),
    KEY_P(ImGuiKey.P),
    KEY_Q(ImGuiKey.Q),
    KEY_R(ImGuiKey.R),
    KEY_S(ImGuiKey.S),
    KEY_T(ImGuiKey.T),
    KEY_U(ImGuiKey.U),
    KEY_V(ImGuiKey.V),
    KEY_W(ImGuiKey.W),
    KEY_X(ImGuiKey.X),
    KEY_Y(ImGuiKey.Y),
    KEY_Z(ImGuiKey.Z),
    KEY_LEFT_BRACKET(ImGuiKey.LeftBracket),
    KEY_BACKSLASH(ImGuiKey.Backslash),
    KEY_RIGHT_BRACKET(ImGuiKey.RightBracket),
    KEY_GRAVE_ACCENT(ImGuiKey.GraveAccent),
    KEY_WORLD_1(161),
    KEY_WORLD_2(162),
    KEY_ESCAPE(256),
    KEY_ENTER(257),
    KEY_TAB(258),
    KEY_BACKSPACE(259),
    KEY_INSERT(521),
    KEY_DELETE(522),
    KEY_RIGHT(262),
    KEY_LEFT(263),
    KEY_DOWN(264),
    KEY_UP(265),
    KEY_PAGE_UP(266),
    KEY_PAGE_DOWN(267),
    KEY_HOME(268),
    KEY_END(269),
    KEY_CAPS_LOCK(280),
    KEY_SCROLL_LOCK(281),
    KEY_NUM_LOCK(282),
    KEY_PRINT_SCREEN(283),
    KEY_PAUSE(284),
    KEY_F1(572),
    KEY_F2(573),
    KEY_F3(574),
    KEY_F4(575),
    KEY_F5(576),
    KEY_F6(577),
    KEY_F7(578),
    KEY_F8(579),
    KEY_F9(580),
    KEY_F10(581),
    KEY_F11(582),
    KEY_F12(583),
    KEY_F13(302),
    KEY_F14(303),
    KEY_F15(304),
    KEY_F16(305),
    KEY_F17(306),
    KEY_F18(307),
    KEY_F19(308),
    KEY_F20(309),
    KEY_F21(310),
    KEY_F22(311),
    KEY_F23(312),
    KEY_F24(313),
    KEY_F25(314),
    KP_0(600),
    KP_1(601),
    KP_2(602),
    KP_3(603),
    KP_4(604),
    KP_5(605),
    KP_6(606),
    KP_7(607),
    KP_8(608),
    KP_9(609),
    KP_DECIMAL(610),
    KP_DIVIDE(611),
    KP_MULTIPLY(612),
    KP_SUBTRACT(613),
    KP_ADD(614),
    KP_ENTER(615),
    KP_EQUAL(616),
    KEY_LEFT_SHIFT(528),
    KEY_LEFT_CONTROL(527),
    KEY_LEFT_ALT(529),
    KEY_LEFT_SUPER(530),
    KEY_RIGHT_SHIFT(532),
    KEY_RIGHT_CONTROL(531),
    KEY_RIGHT_ALT(533),
    KEY_RIGHT_SUPER(534),
    KEY_MENU(535);

    public static final IGenericRegistry<KeyboardKeyEnum> REGISTRY = new IGenericRegistry<>() {
        @Override
        public KeyboardKeyEnum getNew(int id) {
            return KeyboardKeyEnum.fromImGuiCode(id);
        }

        @Override
        public Integer getID(KeyboardKeyEnum value) {
            return value.imguiCode;
        }

        @Override
        public Map<Integer, KeyboardKeyEnum> getAllNew() {
            return Stream.of(KeyboardKeyEnum.values()).collect(Collectors.toMap(this::getID, v -> v));
        }

        @Override
        public <T extends KeyboardKeyEnum> void register(int id, Class<T> clazz, Supplier<T> constructor) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    };

    /**
     * The imguicode for the key.
     * <br><br>
     * We need a hardcoded identifier that will not change for
     * robust serialization in the hotkey data.
     */
    private final int imguiCode;

    KeyboardKeyEnum(int imguiKey) {
        this.imguiCode = imguiKey;
    }

    public int imguiCode() {
        return this.imguiCode;
    }

    public boolean equalsRawKey(Key key) {
        return this.imguiCode == key.imguiCode();
    }

    @Nullable
    public static KeyboardKeyEnum fromImGuiCode(int code) {
        switch (code) {
            case ImGuiKey.Space: return KEY_SPACE;
            case ImGuiKey.Apostrophe: return KEY_APOSTROPHE;
            case ImGuiKey.Comma: return KEY_COMMA;
            case ImGuiKey.Minus: return KEY_MINUS;
            case ImGuiKey.Period: return KEY_PERIOD;
            case ImGuiKey.Slash: return KEY_SLASH;
            case ImGuiKey._0: return KEY_0;
            case ImGuiKey._1: return KEY_1;
            case ImGuiKey._2: return KEY_2;
            case ImGuiKey._3: return KEY_3;
            case ImGuiKey._4: return KEY_4;
            case ImGuiKey._5: return KEY_5;
            case ImGuiKey._6: return KEY_6;
            case ImGuiKey._7: return KEY_7;
            case ImGuiKey._8: return KEY_8;
            case ImGuiKey._9: return KEY_9;
            case ImGuiKey.Semicolon: return KEY_SEMICOLON;
            case ImGuiKey.Equal: return KEY_EQUAL;
            case ImGuiKey.A: return KEY_A;
            case ImGuiKey.B: return KEY_B;
            case ImGuiKey.C: return KEY_C;
            case ImGuiKey.D: return KEY_D;
            case ImGuiKey.E: return KEY_E;
            case ImGuiKey.F: return KEY_F;
            case ImGuiKey.G: return KEY_G;
            case ImGuiKey.H: return KEY_H;
            case ImGuiKey.I: return KEY_I;
            case ImGuiKey.J: return KEY_J;
            case ImGuiKey.K: return KEY_K;
            case ImGuiKey.L: return KEY_L;
            case ImGuiKey.M: return KEY_M;
            case ImGuiKey.N: return KEY_N;
            case ImGuiKey.O: return KEY_O;
            case ImGuiKey.P: return KEY_P;
            case ImGuiKey.Q: return KEY_Q;
            case ImGuiKey.R: return KEY_R;
            case ImGuiKey.S: return KEY_S;
            case ImGuiKey.T: return KEY_T;
            case ImGuiKey.U: return KEY_U;
            case ImGuiKey.V: return KEY_V;
            case ImGuiKey.W: return KEY_W;
            case ImGuiKey.X: return KEY_X;
            case ImGuiKey.Y: return KEY_Y;
            case ImGuiKey.Z: return KEY_Z;
            case ImGuiKey.LeftBracket: return KEY_LEFT_BRACKET;
            case ImGuiKey.Backslash: return KEY_BACKSLASH;
            case ImGuiKey.RightBracket: return KEY_RIGHT_BRACKET;
            case ImGuiKey.GraveAccent: return KEY_GRAVE_ACCENT;
            case 161: return KEY_WORLD_1;
            case 162: return KEY_WORLD_2;
            case 256: return KEY_ESCAPE;
            case 257: return KEY_ENTER;
            case 258: return KEY_TAB;
            case 259: return KEY_BACKSPACE;
            case 521: return KEY_INSERT;
            case 522: return KEY_DELETE;
            case 262: return KEY_RIGHT;
            case 263: return KEY_LEFT;
            case 264: return KEY_DOWN;
            case 265: return KEY_UP;
            case 266: return KEY_PAGE_UP;
            case 267: return KEY_PAGE_DOWN;
            case 268: return KEY_HOME;
            case 269: return KEY_END;
            case 280: return KEY_CAPS_LOCK;
            case 281: return KEY_SCROLL_LOCK;
            case 282: return KEY_NUM_LOCK;
            case 283: return KEY_PRINT_SCREEN;
            case 284: return KEY_PAUSE;
            case 572: return KEY_F1;
            case 573: return KEY_F2;
            case 574: return KEY_F3;
            case 575: return KEY_F4;
            case 576: return KEY_F5;
            case 577: return KEY_F6;
            case 578: return KEY_F7;
            case 579: return KEY_F8;
            case 580: return KEY_F9;
            case 581: return KEY_F10;
            case 582: return KEY_F11;
            case 583: return KEY_F12;
            case 302: return KEY_F13;
            case 303: return KEY_F14;
            case 304: return KEY_F15;
            case 305: return KEY_F16;
            case 306: return KEY_F17;
            case 307: return KEY_F18;
            case 308: return KEY_F19;
            case 309: return KEY_F20;
            case 310: return KEY_F21;
            case 311: return KEY_F22;
            case 312: return KEY_F23;
            case 313: return KEY_F24;
            case 314: return KEY_F25;
            case 600: return KP_0;
            case 601: return KP_1;
            case 602: return KP_2;
            case 603: return KP_3;
            case 604: return KP_4;
            case 605: return KP_5;
            case 606: return KP_6;
            case 607: return KP_7;
            case 608: return KP_8;
            case 609: return KP_9;
            case 610: return KP_DECIMAL;
            case 611: return KP_DIVIDE;
            case 612: return KP_MULTIPLY;
            case 613: return KP_SUBTRACT;
            case 614: return KP_ADD;
            case 615: return KP_ENTER;
            case 616: return KP_EQUAL;
            case 528: return KEY_LEFT_SHIFT;
            case 527: return KEY_LEFT_CONTROL;
            case 529: return KEY_LEFT_ALT;
            case 530: return KEY_LEFT_SUPER;
            case 532: return KEY_RIGHT_SHIFT;
            case 531: return KEY_RIGHT_CONTROL;
            case 533: return KEY_RIGHT_ALT;
            case 534: return KEY_RIGHT_SUPER;
            case 535: return KEY_MENU;
            default: return null;
        }
    }

    @Override
    public String title() {
        return this.name();
    }

    @Override
    public @Nullable String tooltip() {
        return this.name();
    }

    @Override
    public int keyCode() {
        return this.imguiCode;
    }
}
