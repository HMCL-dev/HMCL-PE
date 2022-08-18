package com.tungsten.hmclpe.launcher.game;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tungsten.hmclpe.utils.Lang;
import com.tungsten.hmclpe.utils.platform.Architecture;
import com.tungsten.hmclpe.utils.platform.OperatingSystem;

import java.util.regex.Pattern;

public final class OSRestriction {

    private final OperatingSystem name;
    private final String version;
    private final String arch;

    public OSRestriction() {
        this(OperatingSystem.UNKNOWN);
    }

    public OSRestriction(OperatingSystem name) {
        this(name, null);
    }

    public OSRestriction(OperatingSystem name, String version) {
        this(name, version, null);
    }

    public OSRestriction(OperatingSystem name, String version, String arch) {
        this.name = name;
        this.version = version;
        this.arch = arch;
    }

    public OperatingSystem getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getArch() {
        return arch;
    }

    public boolean allow() {
        if (name != OperatingSystem.UNKNOWN && name != OperatingSystem.CURRENT_OS)
            return false;

        if (version != null)
            if (Lang.test(() -> !Pattern.compile(version).matcher(OperatingSystem.SYSTEM_VERSION).matches()))
                return false;

        if (arch != null)
            return !Lang.test(() -> !Pattern.compile(arch).matcher(Architecture.SYSTEM_ARCH.getCheckedName()).matches());

        return true;
    }

}
