package com.tungsten.hmclpe.launcher.download.forge;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.utils.gson.tools.Validation;

/**
 *
 * @author huangyuhui
 */
public final class ForgeInstallProfile implements Validation {

    @SerializedName("install")
    private final ForgeInstall install;

    @SerializedName("versionInfo")
    private final Version versionInfo;

    public ForgeInstallProfile(ForgeInstall install, Version versionInfo) {
        this.install = install;
        this.versionInfo = versionInfo;
    }

    public ForgeInstall getInstall() {
        return install;
    }

    public Version getVersionInfo() {
        return versionInfo;
    }

    @Override
    public void validate() throws JsonParseException {
        if (install == null)
            throw new JsonParseException("InstallProfile install cannot be null");

        if (versionInfo == null)
            throw new JsonParseException("InstallProfile versionInfo cannot be null");
    }
}