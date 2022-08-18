package com.tungsten.hmclpe.launcher.game;

import com.google.gson.annotations.SerializedName;

public class LoggingInfo {

    @SerializedName("file")
    public IdDownloadInfo file;
    @SerializedName("argument")
    public String argument;
    @SerializedName("type")
    public String type;

    public LoggingInfo() {
        this(new IdDownloadInfo());
    }

    public LoggingInfo(IdDownloadInfo file) {
        this(file, "");
    }

    public LoggingInfo(IdDownloadInfo file, String argument) {
        this(file, argument, "");
    }

    public LoggingInfo(IdDownloadInfo file, String argument, String type) {
        this.file = file;
        this.argument = argument;
        this.type = type;
    }

}
