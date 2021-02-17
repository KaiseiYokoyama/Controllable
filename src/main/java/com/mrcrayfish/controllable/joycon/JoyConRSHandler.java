package com.mrcrayfish.controllable.joycon;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface JoyConRSHandler extends Library {
    String JNA_LIBRARY_NAME = "joycon_rs_jna";
//    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(JNA_LIBRARY_NAME);

    JoyConRSHandler INSTANCE = (JoyConRSHandler) Native.load(JNA_LIBRARY_NAME, JoyConRSHandler.class);

    RawReport getLatestReport();

    /**
     * Free the memory used by a Report
     */
    void dropReport(RawReport report);

    boolean scan();
}
