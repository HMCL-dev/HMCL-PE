package com.tungsten.hmclpe.launcher.setting.game;

import com.google.gson.Gson;
import com.tungsten.hmclpe.control.view.MenuView;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.game.child.MenuFloatSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.MenuViewSetting;
import com.tungsten.hmclpe.utils.file.FileStringUtils;

import java.io.File;

public class GameMenuSetting {

    public static final int GAME_MENU_VERSION = 4;

    public MenuFloatSetting menuFloatSetting;
    public MenuViewSetting menuViewSetting;
    public boolean menuSlideSetting;
    public boolean enableTouch;
    public boolean mousePatch;
    public boolean enableSensor;
    public int sensitivity;
    public boolean advanceInput;
    public boolean disableHalfScreen;
    public int touchMode;
    public int mouseMode;
    public float mouseSpeed;
    public int mouseSize;
    public boolean hideUI;
    public int version;

    public GameMenuSetting(MenuFloatSetting menuFloatSetting,MenuViewSetting menuViewSetting,boolean menuSlideSetting,boolean enableTouch,boolean mousePatch,boolean enableSensor,int sensitivity,boolean advanceInput,boolean disableHalfScreen,int touchMode,int mouseMode,float mouseSpeed,int mouseSize,boolean hideUI,int version){
        this.menuFloatSetting = menuFloatSetting;
        this.menuViewSetting = menuViewSetting;
        this.menuSlideSetting = menuSlideSetting;
        this.enableTouch = enableTouch;
        this.mousePatch = mousePatch;
        this.enableSensor = enableSensor;
        this.sensitivity = sensitivity;
        this.advanceInput = advanceInput;
        this.disableHalfScreen = disableHalfScreen;
        this.touchMode = touchMode;
        this.mouseMode = mouseMode;
        this.mouseSpeed = mouseSpeed;
        this.mouseSize = mouseSize;
        this.hideUI = hideUI;
        this.version = version;
    }

    public static GameMenuSetting getGameMenuSetting(){
        GameMenuSetting gameMenuSetting;
        String path = AppManifest.SETTING_DIR + "/game_menu_setting.json";
        if (!new File(path).exists()){
            gameMenuSetting = new GameMenuSetting(new MenuFloatSetting(true,true,0.5f,0.5f),
                    new MenuViewSetting(true, MenuView.MENU_MODE_LEFT,0.2f),
                    true,
                    true,
                    false,
                    false,
                    10,
                    false,
                    false,
                    0,
                    0,
                    1f,
                    16,
                    false,
                    GAME_MENU_VERSION);
            saveGameMenuSetting(gameMenuSetting);
        }
        else {
            String string = FileStringUtils.getStringFromFile(path);
            Gson gson = new Gson();
            gameMenuSetting = gson.fromJson(string,GameMenuSetting.class);
            if (gameMenuSetting.version == 0) {
                gameMenuSetting.enableTouch = true;
                gameMenuSetting.mousePatch = false;
                gameMenuSetting.enableSensor = false;
                gameMenuSetting.sensitivity = 10;
                gameMenuSetting.disableHalfScreen = false;
                gameMenuSetting.touchMode = 0;
                gameMenuSetting.mouseMode = 0;
                gameMenuSetting.mouseSpeed = 1f;
                gameMenuSetting.mouseSize = 16;
                gameMenuSetting.hideUI = false;
                gameMenuSetting.version = GAME_MENU_VERSION;
                saveGameMenuSetting(gameMenuSetting);
            }
            if (gameMenuSetting.version == 1) {
                gameMenuSetting.sensitivity = 10;
                gameMenuSetting.enableTouch = true;
                gameMenuSetting.mousePatch = false;
                gameMenuSetting.version = GAME_MENU_VERSION;
                saveGameMenuSetting(gameMenuSetting);
            }
            if (gameMenuSetting.version == 2) {
                gameMenuSetting.enableTouch = true;
                gameMenuSetting.mousePatch = false;
                gameMenuSetting.version = GAME_MENU_VERSION;
                saveGameMenuSetting(gameMenuSetting);
            }
            if (gameMenuSetting.version == 3) {
                gameMenuSetting.mousePatch = false;
                gameMenuSetting.version = GAME_MENU_VERSION;
                saveGameMenuSetting(gameMenuSetting);
            }
        }
        return gameMenuSetting;
    }

    public static void saveGameMenuSetting(GameMenuSetting gameMenuSetting){
        String path = AppManifest.SETTING_DIR + "/game_menu_setting.json";
        Gson gson = new Gson();
        String string = gson.toJson(gameMenuSetting);
        FileStringUtils.writeFile(path,string);
    }

}
