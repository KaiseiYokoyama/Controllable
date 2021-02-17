package com.mrcrayfish.controllable.joycon;


import com.sun.jna.Structure;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;

public class RawReport extends Structure implements Closeable {

    public short input_report_id;
    public short timer;
    public boolean is_powered;
    public boolean a_button;
    public boolean x_button;
    public boolean y_button;
    public boolean b_button;
    public boolean plus_button;
    public boolean r_stick_press;
    public boolean home_button;
    public boolean r_button;
    public boolean zr_button;
    public boolean dpad_right;
    public boolean dpad_up;
    public boolean dpad_left;
    public boolean dpad_down;
    public boolean minus_button;
    public boolean l_stick_press;
    public boolean capture_button;
    public boolean l_button;
    public boolean zl_button;
    public boolean sl_button;
    public boolean sr_button;
    public double left_stick_h;
    public double left_stick_v;
    public double right_stick_h;
    public double right_stick_v;
    public short vibrator_input_report;

//    public static class ByReference extends Report implements Structure.ByReference {}
//    public static class ByValue extends Report implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[] {
                "input_report_id",
                "timer",
                "is_powered",
                "a_button",
                "x_button",
                "y_button",
                "b_button",
                "plus_button",
                "r_stick_press",
                "home_button",
                "r_button",
                "zr_button",
                "dpad_right",
                "dpad_up",
                "dpad_left",
                "dpad_down",
                "minus_button",
                "l_stick_press",
                "capture_button",
                "l_button",
                "zl_button",
                "sl_button",
                "sr_button",
                "left_stick_h",
                "left_stick_v",
                "right_stick_h",
                "right_stick_v",
                "vibrator_input_report"
        });
    }

    @Override
    public void close() {
        // Turn off "auto-synch". If it is on, JNA will automatically read all fields
        // from the struct's memory and update them on the Java object. This synchronization
        // occurs after every native method call. If it occurs after we drop the struct, JNA
        // will try to read from the freed memory and cause a segmentation fault.
        setAutoSynch(false);
        // Send the struct back to rust for the memory to be freed
        JoyConRSHandler.INSTANCE.dropReport(this);
    }
}