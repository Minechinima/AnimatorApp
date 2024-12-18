package com.minechinima.ui.input.events;

import javax.annotation.Nullable;

public enum Input {
    //TODO imgui keys....
    KEY_UNKNOWN(-1, 0),
    KEY_SPACE(32, 524),
    KEY_APOSTROPHE(39, 584),
    KEY_COMMA(44, 585),
    KEY_MINUS(45, 586),
    KEY_PERIOD(46, 587),
    KEY_SLASH(47, 588),
    KEY_0(48, 536),
    KEY_1(49, 537),
    KEY_2(50, 538),
    KEY_3(51, 539),
    KEY_4(52, 540),
    KEY_5(53, 541),
    KEY_6(54, 542),
    KEY_7(55, 543),
    KEY_8(56, 544),
    KEY_9(57, 545),
    KEY_SEMICOLON(59, 589),
    KEY_EQUAL(61, 590),
    KEY_A(65, 546),
    KEY_B(66, 547),
    KEY_C(67, 548),
    KEY_D(68, 549),
    KEY_E(69, 550),
    KEY_F(70, 551),
    KEY_G(71, 552),
    KEY_H(72, 553),
    KEY_I(73, 554),
    KEY_J(74, 555),
    KEY_K(75, 556),
    KEY_L(76, 557),
    KEY_M(77, 558),
    KEY_N(78, 559),
    KEY_O(79, 560),
    KEY_P(80, 561),
    KEY_Q(81, 562),
    KEY_R(82, 563),
    KEY_S(83, 564),
    KEY_T(84, 565),
    KEY_U(85, 566),
    KEY_V(86, 567),
    KEY_W(87, 568),
    KEY_X(88, 569),
    KEY_Y(89, 570),
    KEY_Z(90, 571),
    KEY_LEFT_BRACKET(91, 591),
    KEY_BACKSLASH(92, 592),
    KEY_RIGHT_BRACKET(93, 593),
    KEY_GRAVE_ACCENT(96, 594),
    KEY_WORLD_1(161),
    KEY_WORLD_2(162),
    KEY_ESCAPE(256, 526),
    KEY_ENTER(257, 525),
    KEY_TAB(258, 512),
    KEY_BACKSPACE(259, 523),
    KEY_INSERT(260, 521),
    KEY_DELETE(261, 522),
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
    KEY_F1(290, 572),
    KEY_F2(291, 573),
    KEY_F3(292, 574),
    KEY_F4(293, 575),
    KEY_F5(294, 576),
    KEY_F6(295, 577),
    KEY_F7(296, 578),
    KEY_F8(297, 579),
    KEY_F9(298, 580),
    KEY_F10(299, 581),
    KEY_F11(300, 582),
    KEY_F12(301, 583),
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
    KP_0(320, 600),
    KP_1(321, 601),
    KP_2(322, 602),
    KP_3(323, 603),
    KP_4(324, 604),
    KP_5(325, 605),
    KP_6(326, 606),
    KP_7(327, 607),
    KP_8(328, 608),
    KP_9(329, 609),
    KP_DECIMAL(330, 610),
    KP_DIVIDE(331, 611),
    KP_MULTIPLY(332, 612),
    KP_SUBTRACT(333, 613),
    KP_ADD(334, 614),
    KP_ENTER(335, 615),
    KP_EQUAL(336, 616),
    KEY_LEFT_SHIFT(340, 528),
    KEY_LEFT_CONTROL(341, 527),
    KEY_LEFT_ALT(342, 529),
    KEY_LEFT_SUPER(343, 530),
    KEY_RIGHT_SHIFT(344, 532),
    KEY_RIGHT_CONTROL(345, 531),
    KEY_RIGHT_ALT(346, 533),
    KEY_RIGHT_SUPER(347, 534),
    KEY_MENU(348, 535),
    MOD_SHIFT(1),
    MOD_CONTROL(2),
    MOD_ALT(4),
    MOD_SUPER(8),
    MOD_CAPS_LOCK(16),
    MOD_NUM_LOCK(32),
    MOUSE_BUTTON_1(0),
    MOUSE_BUTTON_2(1),
    MOUSE_BUTTON_3(2),
    MOUSE_BUTTON_4(3),
    MOUSE_BUTTON_5(4),
    MOUSE_BUTTON_6(5),
    MOUSE_BUTTON_7(6),
    MOUSE_BUTTON_8(7),
    MOUSE_BUTTON_LAST(7),
    MOUSE_BUTTON_LEFT(0),
    MOUSE_BUTTON_RIGHT(1),
    MOUSE_BUTTON_MIDDLE(2),
    JOYSTICK_1(0),
    JOYSTICK_2(1),
    JOYSTICK_3(2),
    JOYSTICK_4(3),
    JOYSTICK_5(4),
    JOYSTICK_6(5),
    JOYSTICK_7(6),
    JOYSTICK_8(7),
    JOYSTICK_9(8),
    JOYSTICK_10(9),
    JOYSTICK_11(10),
    JOYSTICK_12(11),
    JOYSTICK_13(12),
    JOYSTICK_14(13),
    JOYSTICK_15(14),
    JOYSTICK_16(15),
    GAMEPAD_BUTTON_A(0),
    GAMEPAD_BUTTON_B(1),
    GAMEPAD_BUTTON_X(2),
    GAMEPAD_BUTTON_Y(3),
    GAMEPAD_BUTTON_LEFT_BUMPER(4),
    GAMEPAD_BUTTON_RIGHT_BUMPER(5),
    GAMEPAD_BUTTON_BACK(6),
    GAMEPAD_BUTTON_START(7),
    GAMEPAD_BUTTON_GUIDE(8),
    GAMEPAD_BUTTON_LEFT_THUMB(9),
    GAMEPAD_BUTTON_RIGHT_THUMB(10),
    GAMEPAD_BUTTON_DPAD_UP(11),
    GAMEPAD_BUTTON_DPAD_RIGHT(12),
    GAMEPAD_BUTTON_DPAD_DOWN(13),
    GAMEPAD_BUTTON_DPAD_LEFT(14),
    GAMEPAD_AXIS_LEFT_X(0),
    GAMEPAD_AXIS_LEFT_Y(1),
    GAMEPAD_AXIS_RIGHT_X(2),
    GAMEPAD_AXIS_RIGHT_Y(3),
    GAMEPAD_AXIS_LEFT_TRIGGER(4),
    GAMEPAD_AXIS_RIGHT_TRIGGER(5);

    private final int glfwCode;
    private final int imguiCode;

    Input(int glfwCode) {
        this.glfwCode = glfwCode;
        this.imguiCode = -1;
    }

    Input(int value, int imguiCode) {
        this.glfwCode = value;
        this.imguiCode = imguiCode;
    }

    public int glfwCode() {
        return glfwCode;
    }

    public int imguiCode() {
        return glfwCode;
    }

    @Nullable
    public static Input fromGFLWCode(int code) {
        for (Input key : Input.values()) {
            if (key.glfwCode == code) {
                return key;
            }
        }

        return null;
    }

    @Nullable
    public static Input fromImGuiCode(int code) {
        for (Input key : Input.values()) {
            if (key.imguiCode == code) {
                return key;
            }
        }

        return null;
    }
}