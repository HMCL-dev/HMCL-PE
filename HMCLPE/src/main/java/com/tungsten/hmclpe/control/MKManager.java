package com.tungsten.hmclpe.control;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;

import org.lwjgl.glfw.CallbackBridge;

public class MKManager implements View.OnKeyListener, View.OnCapturedPointerListener, View.OnGenericMotionListener {

    private final MenuHelper menuHelper;
    
    private boolean capslockMode;
    private boolean shiftMode;

    public MKManager(MenuHelper menuHelper) {
        this.menuHelper = menuHelper;
        menuHelper.baseLayout.setFocusable(true);
        menuHelper.baseLayout.setOnCapturedPointerListener(this);
        menuHelper.baseLayout.setOnGenericMotionListener(this);
        menuHelper.baseLayout.setOnKeyListener(this);
        menuHelper.baseLayout.requestFocus();
        menuHelper.baseLayout.requestPointerCapture();
        System.out.println("----------------------------------MKManager initialized!");
    }

    public void enableCursor() {

    }

    public void disableCursor() {

    }

    public boolean handleMouseEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_BUTTON_PRESS) {
            if (motionEvent.getActionButton() == MotionEvent.BUTTON_PRIMARY) {
                InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_LEFT, true);
            }
            else if (motionEvent.getActionButton() == MotionEvent.BUTTON_SECONDARY || motionEvent.getActionButton() == MotionEvent.BUTTON_BACK) {
                InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_RIGHT, true);
            }
            else if (motionEvent.getActionButton() == MotionEvent.BUTTON_TERTIARY) {
                InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_MIDDLE, true);
            }
        }
        else if (motionEvent.getActionMasked() == MotionEvent.ACTION_BUTTON_RELEASE) {
            if (motionEvent.getActionButton() == MotionEvent.BUTTON_PRIMARY) {
                InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_LEFT, false);
            }
            else if (motionEvent.getActionButton() == MotionEvent.BUTTON_SECONDARY || motionEvent.getActionButton() == MotionEvent.BUTTON_BACK) {
                InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_RIGHT, false);
            }
            else if (motionEvent.getActionButton() == MotionEvent.BUTTON_TERTIARY) {
                InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_MIDDLE, false);
            }
        }
        else if (motionEvent.getActionMasked() == MotionEvent.ACTION_SCROLL) {
            if (menuHelper.launcher == 2) {
                CallbackBridge.sendScroll(motionEvent.getAxisValue(MotionEvent.AXIS_HSCROLL), motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL));
            }
            else {
                if (motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL) > 0) {
                    for (int i = 0;i < Math.abs((int) motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL));i++) {
                        InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_SCROLL_UP, true);
                    }
                }
                if (motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0) {
                    for (int i = 0;i < Math.abs((int) motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL));i++) {
                        InputBridge.sendMouseEvent(menuHelper.launcher, InputBridge.MOUSE_SCROLL_DOWN, true);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        if (!menuHelper.touchCharInput.isEnabled()) {
            menuHelper.baseLayout.requestFocus();
            menuHelper.baseLayout.requestPointerCapture();
        }
        return true;
    }

    @Override
    public boolean onCapturedPointer(View view, MotionEvent motionEvent) {
        if (menuHelper.gameCursorMode == 0) {
            float targetX;
            float targetY;
            if (menuHelper.cursorX + motionEvent.getX() * menuHelper.gameMenuSetting.mouseSpeed < 0){
                targetX = 0;
            }
            else if (menuHelper.cursorX + motionEvent.getX() * menuHelper.gameMenuSetting.mouseSpeed > menuHelper.baseLayout.getWidth()){
                targetX = menuHelper.baseLayout.getWidth();
            }
            else {
                targetX = menuHelper.cursorX + motionEvent.getX() * menuHelper.gameMenuSetting.mouseSpeed;
            }
            if (menuHelper.cursorY + motionEvent.getY() * menuHelper.gameMenuSetting.mouseSpeed < 0){
                targetY = 0;
            }
            else if (menuHelper.cursorY + motionEvent.getY() * menuHelper.gameMenuSetting.mouseSpeed > menuHelper.baseLayout.getHeight()){
                targetY = menuHelper.baseLayout.getHeight();
            }
            else {
                targetY = menuHelper.cursorY + motionEvent.getY() * menuHelper.gameMenuSetting.mouseSpeed;
            }
            menuHelper.cursorX = targetX;
            menuHelper.cursorY = targetY;
            menuHelper.pointerX = targetX;
            menuHelper.pointerY = targetY;
            InputBridge.setPointer(menuHelper.launcher,(int) (targetX * menuHelper.scaleFactor),(int) (targetY * menuHelper.scaleFactor));
        }
        else {
            menuHelper.pointerX += motionEvent.getX() * menuHelper.gameMenuSetting.mouseSpeed;
            menuHelper.pointerY += motionEvent.getY() * menuHelper.gameMenuSetting.mouseSpeed;
            menuHelper.currentX = menuHelper.pointerX;
            menuHelper.currentY = menuHelper.pointerY;
            InputBridge.setPointer(menuHelper.launcher,(int) (menuHelper.pointerX * menuHelper.scaleFactor),(int) (menuHelper.pointerY * menuHelper.scaleFactor));
        }
        return handleMouseEvent(motionEvent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if((keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD) {
            if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) return true; //We already listen to it.
            menuHelper.touchCharInput.dispatchKeyEvent(keyEvent);
            return true;
        }
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_UNKNOWN:
            case KeyEvent.KEYCODE_BACK:
                return true;
            case KeyEvent.KEYCODE_ENTER:
                if (!menuHelper.enterLock && keyEvent.getAction() == KeyEvent.ACTION_UP && menuHelper.touchCharInput != null && !menuHelper.touchCharInput.isEnabled()) {
                    menuHelper.touchCharInput.switchKeyboardState();
                }
                if (menuHelper.enterLock) {
                    menuHelper.enterLock = false;
                }
                return true;
            case KeyEvent.KEYCODE_POUND:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_3, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_AT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_2, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_PLUS:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_ADD, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F1:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F1, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F2:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F2, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F3:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F3, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F4:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F4, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F5:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F5, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F6:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F6, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F7:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F7, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F8:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F8, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F9:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F9, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F10:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F10, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F11:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F11, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F12:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F12, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_0:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_0, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_1:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_1, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_2:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_2, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_3:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_3, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_4:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_4, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_5:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_5, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_6:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_6, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_7:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_7, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_8:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_8, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_9:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_9, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_A:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_B:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_B, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_C:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_C, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_D:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_E:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_E, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_F:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_F, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_G:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_G, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_H:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_H, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_I:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_I, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_J:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_J, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_K:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_K, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_L:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_L, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_M:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_M, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_N:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_N, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_O:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_O, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_P:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_P, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_Q:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_Q, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_R:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_R, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_S:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_T:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_U:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_U, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_V:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_V, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_W:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_X:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_X, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_Y:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_Y, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_Z:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_Z, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUM_LOCK:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_NUM_LOCK, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_DIVIDE, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_MULTIPLY, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_SUBTRACT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_ADD:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_ADD, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_DOT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_PERIOD, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_COMMA:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_COMMA, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_EQUALS:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_EQUAL, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_0:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_0, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_1:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_1, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_2:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_2, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_3:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_3, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_4:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_4, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_5:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_5, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_6:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_6, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_7:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_7, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_8:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_8, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_NUMPAD_9:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_KP_9, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_ESCAPE:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ESCAPE, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_GRAVE:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_GRAVE_ACCENT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_MINUS:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_MINUS, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_EQUALS:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_EQUAL, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_DEL:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_BACKSPACE, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_TAB:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_TAB, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT_BRACKET, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_RIGHT_BRACKET, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_BACKSLASH:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_BACKSLASH, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_CAPS_LOCK:
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    capslockMode = !capslockMode;
                }
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_CAPS_LOCK, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_SEMICOLON:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_SEMICOLON, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_APOSTROPHE:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_APOSTROPHE, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_SHIFT_LEFT:
                shiftMode = keyEvent.getAction() == KeyEvent.ACTION_DOWN;
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT_SHIFT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                shiftMode = keyEvent.getAction() == KeyEvent.ACTION_DOWN;
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_RIGHT_SHIFT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_COMMA:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_COMMA, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_PERIOD:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_PERIOD, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_SLASH:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_SLASH, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_CTRL_LEFT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT_CONTROL, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_RIGHT_CONTROL, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_ALT_LEFT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT_ALT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_ALT_RIGHT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_RIGHT_ALT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_SPACE:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_SPACE, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_UP, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_DOWN, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_RIGHT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_INSERT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_INSERT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_HOME:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_HOME, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_BREAK:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_PAUSE, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_PAGE_UP:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_PAGE_UP, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_PAGE_DOWN, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                break;
        }
        if (menuHelper.gameCursorMode == 0 && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_0:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? ')' : '0');
                    break;
                case KeyEvent.KEYCODE_1:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '!' : '1');
                    break;
                case KeyEvent.KEYCODE_2:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '@' : '2');
                    break;
                case KeyEvent.KEYCODE_3:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '#' : '3');
                    break;
                case KeyEvent.KEYCODE_4:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '$' : '4');
                    break;
                case KeyEvent.KEYCODE_5:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '%' : '5');
                    break;
                case KeyEvent.KEYCODE_6:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '^' : '6');
                    break;
                case KeyEvent.KEYCODE_7:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '&' : '7');
                    break;
                case KeyEvent.KEYCODE_8:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '*' : '8');
                    break;
                case KeyEvent.KEYCODE_9:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '(' : '9');
                    break;
                case KeyEvent.KEYCODE_A:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'a' : 'A') : (shiftMode ? 'A' : 'a'));
                    break;
                case KeyEvent.KEYCODE_B:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'b' : 'B') : (shiftMode ? 'B' : 'b'));
                    break;
                case KeyEvent.KEYCODE_C:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'c' : 'C') : (shiftMode ? 'C' : 'c'));
                    break;
                case KeyEvent.KEYCODE_D:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'd' : 'D') : (shiftMode ? 'D' : 'd'));
                    break;
                case KeyEvent.KEYCODE_E:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'e' : 'E') : (shiftMode ? 'E' : 'e'));
                    break;
                case KeyEvent.KEYCODE_F:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'f' : 'F') : (shiftMode ? 'F' : 'f'));
                    break;
                case KeyEvent.KEYCODE_G:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'g' : 'G') : (shiftMode ? 'G' : 'g'));
                    break;
                case KeyEvent.KEYCODE_H:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'h' : 'H') : (shiftMode ? 'H' : 'h'));
                    break;
                case KeyEvent.KEYCODE_I:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'i' : 'I') : (shiftMode ? 'I' : 'i'));
                    break;
                case KeyEvent.KEYCODE_J:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'j' : 'J') : (shiftMode ? 'J' : 'j'));
                    break;
                case KeyEvent.KEYCODE_K:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'k' : 'K') : (shiftMode ? 'K' : 'k'));
                    break;
                case KeyEvent.KEYCODE_L:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'l' : 'L') : (shiftMode ? 'L' : 'l'));
                    break;
                case KeyEvent.KEYCODE_M:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'm' : 'M') : (shiftMode ? 'M' : 'm'));
                    break;
                case KeyEvent.KEYCODE_N:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'n' : 'N') : (shiftMode ? 'N' : 'n'));
                    break;
                case KeyEvent.KEYCODE_O:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'o' : 'O') : (shiftMode ? 'O' : 'o'));
                    break;
                case KeyEvent.KEYCODE_P:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'p' : 'P') : (shiftMode ? 'P' : 'p'));
                    break;
                case KeyEvent.KEYCODE_Q:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'q' : 'Q') : (shiftMode ? 'Q' : 'q'));
                    break;
                case KeyEvent.KEYCODE_R:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'r' : 'R') : (shiftMode ? 'R' : 'r'));
                    break;
                case KeyEvent.KEYCODE_S:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 's' : 'S') : (shiftMode ? 'S' : 's'));
                    break;
                case KeyEvent.KEYCODE_T:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 't' : 'T') : (shiftMode ? 'T' : 't'));
                    break;
                case KeyEvent.KEYCODE_U:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'u' : 'U') : (shiftMode ? 'U' : 'u'));
                    break;
                case KeyEvent.KEYCODE_V:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'v' : 'V') : (shiftMode ? 'V' : 'v'));
                    break;
                case KeyEvent.KEYCODE_W:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'w' : 'W') : (shiftMode ? 'W' : 'w'));
                    break;
                case KeyEvent.KEYCODE_X:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'x' : 'X') : (shiftMode ? 'X' : 'x'));
                    break;
                case KeyEvent.KEYCODE_Y:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'y' : 'Y') : (shiftMode ? 'Y' : 'y'));
                    break;
                case KeyEvent.KEYCODE_Z:
                    InputBridge.sendKeyChar(menuHelper.launcher, capslockMode ? (shiftMode ? 'z' : 'Z') : (shiftMode ? 'Z' : 'z'));
                    break;
                case KeyEvent.KEYCODE_NUMPAD_DOT:
                    InputBridge.sendKeyChar(menuHelper.launcher, '.');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_COMMA:
                    InputBridge.sendKeyChar(menuHelper.launcher, ',');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_0:
                    InputBridge.sendKeyChar(menuHelper.launcher, '0');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_1:
                    InputBridge.sendKeyChar(menuHelper.launcher, '1');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_2:
                    InputBridge.sendKeyChar(menuHelper.launcher, '2');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_3:
                    InputBridge.sendKeyChar(menuHelper.launcher, '3');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_4:
                    InputBridge.sendKeyChar(menuHelper.launcher, '4');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_5:
                    InputBridge.sendKeyChar(menuHelper.launcher, '5');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_6:
                    InputBridge.sendKeyChar(menuHelper.launcher, '6');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_7:
                    InputBridge.sendKeyChar(menuHelper.launcher, '7');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_8:
                    InputBridge.sendKeyChar(menuHelper.launcher, '8');
                    break;
                case KeyEvent.KEYCODE_NUMPAD_9:
                    InputBridge.sendKeyChar(menuHelper.launcher, '9');
                    break;
                case KeyEvent.KEYCODE_GRAVE:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '~' : '`');
                    break;
                case KeyEvent.KEYCODE_MINUS:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '_' : '-');
                    break;
                case KeyEvent.KEYCODE_EQUALS:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '+' : '=');
                    break;
                case KeyEvent.KEYCODE_LEFT_BRACKET:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '{' : '[');
                    break;
                case KeyEvent.KEYCODE_RIGHT_BRACKET:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '}' : ']');
                    break;
                case KeyEvent.KEYCODE_BACKSLASH:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '|' : '\\');
                    break;
                case KeyEvent.KEYCODE_SEMICOLON:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? ':' : ';');
                    break;
                case KeyEvent.KEYCODE_APOSTROPHE:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '"' : '\'');
                    break;
                case KeyEvent.KEYCODE_COMMA:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '<' : ',');
                    break;
                case KeyEvent.KEYCODE_PERIOD:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '>' : '.');
                    break;
                case KeyEvent.KEYCODE_SLASH:
                    InputBridge.sendKeyChar(menuHelper.launcher, shiftMode ? '?' : '/');
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    InputBridge.sendKeyChar(menuHelper.launcher, ' ');
                    break;
            }
        }
        return true;
    }
}
