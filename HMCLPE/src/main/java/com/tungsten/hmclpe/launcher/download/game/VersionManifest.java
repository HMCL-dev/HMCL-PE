package com.tungsten.hmclpe.launcher.download.game;

import java.util.Date;

public class VersionManifest {

    public LatestVersion latest;
    public Version[] versions;

    public VersionManifest (LatestVersion latest, Version[] versions){
        this.latest = latest;
        this.versions = versions;
    }

    public class LatestVersion{
        public String release;
        public String snapshot;

        public LatestVersion(String release,String snapshot){
            this.release = release;
            this.snapshot = snapshot;
        }
    }

    public class Version {
        public String id;
        public String type;
        public String url;
        public Date time;
        public Date releaseTime;

        public Version(String id, String type, String url, Date time, Date releaseTime){
            this.id = id;
            this.type = type;
            this.url = url;
            this.time = time;
            this.releaseTime = releaseTime;
        }
    }

}
