package com.tungsten.hmclpe.launcher.mod.hmcl;

import com.tungsten.hmclpe.launcher.mod.ModpackManifest;
import com.tungsten.hmclpe.launcher.mod.ModpackProvider;

public final class HMCLModpackManifest implements ModpackManifest {
    public static final HMCLModpackManifest INSTANCE = new HMCLModpackManifest();

    private HMCLModpackManifest() {}

    @Override
    public ModpackProvider getProvider() {
        return HMCLModpackProvider.INSTANCE;
    }
}