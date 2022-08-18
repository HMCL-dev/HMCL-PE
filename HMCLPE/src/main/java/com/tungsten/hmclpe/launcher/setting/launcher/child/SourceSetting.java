package com.tungsten.hmclpe.launcher.setting.launcher.child;

public class SourceSetting {

    public boolean autoSelect;
    public int autoSourceType;
    public int fixSourceType;

    public SourceSetting(boolean autoSelect,int autoSourceType,int fixSourceType) {
        this.autoSelect = autoSelect;
        this.autoSourceType = autoSourceType;
        this.fixSourceType = fixSourceType;
    }

}
