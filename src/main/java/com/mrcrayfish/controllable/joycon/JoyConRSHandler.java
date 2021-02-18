package com.mrcrayfish.controllable.joycon;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.Reference;
import com.sun.jna.Library;
import com.sun.jna.Native;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Locale;

public interface JoyConRSHandler extends Library {
    public static final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

    public static final boolean IS_LINUX = osName.contains("linux");
    public static final boolean IS_MAC = osName.contains("mac");
    public static final boolean IS_WINDOWS = osName.contains("windows");
    final static String JNA_LIBRARY_MAC = "libs/libjoycon_rs_jna.dylib";
    JoyConRSHandler INSTANCE = (JoyConRSHandler) NativeUtils.loadLibraryFromJar(JNA_LIBRARY_MAC, JoyConRSHandler.class);

    RawReport getLatestReport();

    /**
     * Free the memory used by a Report
     */
    void dropReport(RawReport report);

    boolean scan();
}
