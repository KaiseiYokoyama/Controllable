package com.mrcrayfish.controllable.joycon;

import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class Report {
    public short input_report_id;
    public short timer;
    public boolean isPowered;
    public LinkedList<Buttons> pressedButtons;
    public Stick leftStick;
    public Stick rightStick;
    public short vibrator_input_report;

    public Report(RawReport raw) {
        this.input_report_id = raw.input_report_id;
        this.timer = raw.timer;
        this.isPowered = raw.is_powered;
        this.vibrator_input_report = raw.vibrator_input_report;

        // pressed buttons
        LinkedList<Buttons> pressedButtons = new LinkedList();
        {
            // A button
            if (raw.a_button) pressedButtons.add(Buttons.A);
            if (raw.x_button) pressedButtons.add(Buttons.X);
            if (raw.y_button) pressedButtons.add(Buttons.Y);
            if (raw.b_button) pressedButtons.add(Buttons.B);
            if (raw.plus_button) pressedButtons.add(Buttons.Plus);
            if (raw.r_stick_press) pressedButtons.add(Buttons.RStick);
            if (raw.home_button) pressedButtons.add(Buttons.Home);
            if (raw.r_button) pressedButtons.add(Buttons.R);
            if (raw.zr_button) pressedButtons.add(Buttons.ZR);
            if (raw.dpad_right) pressedButtons.add(Buttons.Right);
            if (raw.dpad_up) pressedButtons.add(Buttons.Up);
            if (raw.dpad_left) pressedButtons.add(Buttons.Left);
            if (raw.dpad_down) pressedButtons.add(Buttons.Down);
            if (raw.minus_button) pressedButtons.add(Buttons.Minus);
            if (raw.l_stick_press) pressedButtons.add(Buttons.LStick);
            if (raw.capture_button) pressedButtons.add(Buttons.Capture);
            if (raw.l_button) pressedButtons.add(Buttons.L);
            if (raw.zl_button) pressedButtons.add(Buttons.ZL);
            if (raw.sl_button) pressedButtons.add(Buttons.SL);
            if (raw.sr_button) pressedButtons.add(Buttons.SR);
        }
        this.pressedButtons = pressedButtons;

        // sticks
        Stick leftStick = new Stick(raw.left_stick_h, raw.left_stick_v, raw.l_stick_press);
        Stick rightStick = new Stick(raw.right_stick_h, raw.right_stick_v, raw.r_stick_press);
        this.leftStick = leftStick;
        this.rightStick = rightStick;
    }

    public enum Buttons {
        A,
        X,
        Y,
        B,
        Plus,
        RStick,
        Home,
        R,
        ZR,
        Right,
        Up,
        Left,
        Down,
        Minus,
        LStick,
        Capture,
        L,
        ZL,
        SL,
        SR,
        ChargingGrip;

        public static final String[] NAMES = {
                "controllable.button.a",
                "controllable.button.x",
                "controllable.button.y",
                "controllable.button.b",
                "controllable.button.plus",
                "controllable.button.rstick",
                "controllable.button.home",
                "controllable.button.r",
                "controllable.button.zr",
                "controllable.button.right",
                "controllable.button.up",
                "controllable.button.left",
                "controllable.button.down",
                "controllable.button.minus",
                "controllable.button.lstick",
                "controllable.button.capture",
                "controllable.button.l",
                "controllable.button.zl",
                "controllable.button.sl",
                "controllable.button.sr",
                "controllable.button.charginggrip",
        };

        @Nullable
        public static String getNameForButton(Buttons button) {
            return NAMES[button.index()];
        }

        @Nullable
        public static Buttons getButtonFromName(String name) {
            return Buttons.values()[ArrayUtils.indexOf(NAMES, name)];
        }

        public int index() {
            return ArrayUtils.indexOf(Buttons.values(), this);
        }

    }

    public static class Stick {
        public double horizontal;
        public double vertical;
        public boolean pressed;

        public Stick(double horizontal, double vertical, boolean pressed) {
            this.horizontal = horizontal;
            this.vertical = vertical;
            this.pressed = pressed;
        }
    }
}
