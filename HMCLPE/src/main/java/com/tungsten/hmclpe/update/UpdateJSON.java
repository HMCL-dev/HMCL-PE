package com.tungsten.hmclpe.update;

public class UpdateJSON {

    public LauncherVersion latestRelease;
    public LauncherVersion latestPrerelease;

    public UpdateJSON (LauncherVersion latestRelease,LauncherVersion latestPrerelease) {
        this.latestRelease = latestRelease;
        this.latestPrerelease = latestPrerelease;
    }

}
