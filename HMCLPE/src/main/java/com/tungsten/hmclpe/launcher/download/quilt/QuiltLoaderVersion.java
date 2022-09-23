package com.tungsten.hmclpe.launcher.download.quilt;

public class QuiltLoaderVersion {

    public String separator;
    public int build;
    public String maven;
    public String version;

    public QuiltLoaderVersion(String separator, int build, String maven, String version){
        this.separator = separator;
        this.build = build;
        this.maven = maven;
        this.version = version;
    }

}