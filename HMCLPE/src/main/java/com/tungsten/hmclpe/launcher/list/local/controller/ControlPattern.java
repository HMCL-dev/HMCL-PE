package com.tungsten.hmclpe.launcher.list.local.controller;

public class ControlPattern {

    public String name;
    public String author;
    public String versionName;
    public String describe;
    public int launcherVersion;

    public ControlPattern(String name,String author,String versionName,String describe,int launcherVersion){
        this.name = name;
        this.author = author;
        this.versionName = versionName;
        this.describe = describe;
        this.launcherVersion = launcherVersion;
    }

}
