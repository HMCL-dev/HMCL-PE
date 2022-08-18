package com.tungsten.filepicker.interfaces;

import com.tungsten.filepicker.Constants;

/**
 * Created by Aditya on 4/18/2017.
 */
public interface IContextSwitcher {
    public void changeBottomNavMenu(Constants.CHOICE_MODE multiChoice);
    public void setNullToActionMode();
    public void reDrawFileList();
    public void switchMode(Constants.CHOICE_MODE mode);
}
