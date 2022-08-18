package com.tungsten.hmclpe.launcher.download.fabric;

public class FabricLoaderVersion {

    public String separator;
    public int build;
    public String maven;
    public String version;
    public boolean stable;

    public FabricLoaderVersion(String separator,int build,String maven,String version,boolean stable){
        this.separator = separator;
        this.build = build;
        this.maven = maven;
        this.version = version;
        this.stable = stable;
    }

}
