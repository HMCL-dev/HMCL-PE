package com.tungsten.hmclpe.launcher.download.optifine;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OptifineVersion implements Serializable {

    @SerializedName("_id")
    public String id;

    @SerializedName("mcversion")
    public String mcVersion;

    @SerializedName("patch")
    public String patch;

    @SerializedName("type")
    public String type;

    @SerializedName("__v")
    public int __v;

    @SerializedName("filename")
    public String fileName;

    public OptifineVersion(String id,String mcVersion,String patch,String type,int __v,String fileName){
        this.id = id;
        this.mcVersion = mcVersion;
        this.patch = patch;
        this.type = type;
        this.__v = __v;
        this.fileName = fileName;
    }

}
