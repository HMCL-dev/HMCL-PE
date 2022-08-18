package com.tungsten.hmclpe.auth.authlibinjector;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class AuthlibInjectorVersionInfo {
        @SerializedName("build_number")
        public int buildNumber;

        @SerializedName("version")
        public String version;

        @SerializedName("download_url")
        public String downloadUrl;

        @SerializedName("checksums")
        public Map<String, String> checksums;
    }