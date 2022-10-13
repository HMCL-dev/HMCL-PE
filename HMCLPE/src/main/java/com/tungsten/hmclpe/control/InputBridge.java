package com.tungsten.hmclpe.control;

import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;

import org.lwjgl.glfw.CallbackBridge;

import cosine.boat.BoatInput;
import cosine.boat.keyboard.BoatKeycodes;

public class InputBridge {

    public static final int MOUSE_LEFT        = 0;
    public static final int MOUSE_RIGHT       = 1;
    public static final int MOUSE_MIDDLE      = 2;
    public static final int MOUSE_SCROLL_UP   = 3;
    public static final int MOUSE_SCROLL_DOWN = 4;

    public static void sendEvent(int launcher,int keyCode,boolean press) {
        if (keyCode == 0 || keyCode == 1 || keyCode == 2 || keyCode == 3 || keyCode == 4) {
            sendMouseEvent(launcher,keyCode,press);
        }
        else {
            sendKeycode(launcher,getKeyCode(launcher,keyCode),press);
        }
    }

    public static void sendKeycode(int launcher,int keyCode,boolean press){
        if (launcher == 1){
            BoatInput.setKey(keyCode,0,press);
        }
        if (launcher == 2){
            CallbackBridge.sendKeyPress(keyCode, CallbackBridge.getCurrentMods(), press);
        }
    }

    public static void sendMouseEvent(int launcher,int bridge,boolean press){
        if (launcher == 1){
            BoatInput.setMouseButton(getMouseEvent(launcher,bridge),press);
        }
        if (launcher == 2){
            if (getMouseEvent(launcher,bridge) == 10) {
                if (press){
                    CallbackBridge.sendScroll(0, 1d);
                }
            }
            else if (getMouseEvent(launcher,bridge) == 11) {
                if (press){
                    CallbackBridge.sendScroll(0, -1d);
                }
            }
            else {
                CallbackBridge.sendMouseButton(getMouseEvent(launcher,bridge),press);
            }
        }
    }

    public static boolean setPointer(int launcher,int x,int y){
        if (launcher == 1){
            BoatInput.setPointer(x,y);
            return true;
        }
        if (launcher == 2){
            return CallbackBridge.sendCursorPos(x,y);
        }
        return false;
    }

    public static void sendKeyChar(int launcher,char keyChar) {
        if (launcher == 1) {
            BoatInput.setKey(0,keyChar,true);
            BoatInput.setKey(0,keyChar,false);
        }
        if (launcher == 2) {
            CallbackBridge.sendChar(keyChar,CallbackBridge.getCurrentMods());
        }
    }

    public static int getMouseEvent(int launcher,int bridge){
        switch (bridge) {
            case MOUSE_LEFT:
                if (launcher == 1){
                    return BoatInput.Button1;
                }
                else {
                    return LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT;
                }
            case MOUSE_RIGHT:
                if (launcher == 1){
                    return BoatInput.Button3;
                }
                else {
                    return LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT;
                }
            case MOUSE_MIDDLE:
                if (launcher == 1){
                    return BoatInput.Button2;
                }
                else {
                    return LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_MIDDLE;
                }
            case MOUSE_SCROLL_UP:
                if (launcher == 1){
                    return BoatInput.Button4;
                }
                else {
                    return 10;
                }
            case MOUSE_SCROLL_DOWN:
                if (launcher == 1){
                    return BoatInput.Button5;
                }
                else {
                    return 11;
                }
            default:
                return -1;
        }
    }

    public static int getKeyCode(int launcher,int raw) {
        if (launcher == 1) {
            int code;
            switch (raw) {
                case LwjglGlfwKeycode.GLFW_KEY_F1:
                    code = BoatKeycodes.KEY_F1;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F2:
                    code = BoatKeycodes.KEY_F2;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F3:
                    code = BoatKeycodes.KEY_F3;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F4:
                    code = BoatKeycodes.KEY_F4;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F5:
                    code = BoatKeycodes.KEY_F5;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F6:
                    code = BoatKeycodes.KEY_F6;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F7:
                    code = BoatKeycodes.KEY_F7;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F8:
                    code = BoatKeycodes.KEY_F8;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F9:
                    code = BoatKeycodes.KEY_F9;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F10:
                    code = BoatKeycodes.KEY_F10;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F11:
                    code = BoatKeycodes.KEY_F11;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F12:
                    code = BoatKeycodes.KEY_F12;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F13:
                    code = BoatKeycodes.KEY_F13;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F14:
                    code = BoatKeycodes.KEY_F14;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_ESCAPE:
                    code = BoatKeycodes.KEY_ESC;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_SCROLL_LOCK:
                    code = BoatKeycodes.KEY_SCROLLLOCK;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_PAUSE:
                    code = BoatKeycodes.KEY_PAUSE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_GRAVE_ACCENT:
                    code = BoatKeycodes.KEY_GRAVE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_MINUS:
                    code = BoatKeycodes.KEY_MINUS;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_EQUAL:
                    code = BoatKeycodes.KEY_EQUAL;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_BACKSPACE:
                    code = BoatKeycodes.KEY_BACKSPACE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_TAB:
                    code = BoatKeycodes.KEY_TAB;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_LEFT_BRACKET:
                    code = BoatKeycodes.KEY_LEFTBRACE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_RIGHT_BRACKET:
                    code = BoatKeycodes.KEY_RIGHTBRACE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_BACKSLASH:
                    code = BoatKeycodes.KEY_BACKSLASH;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_CAPS_LOCK:
                    code = BoatKeycodes.KEY_CAPSLOCK;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_SEMICOLON:
                    code = BoatKeycodes.KEY_SEMICOLON;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_APOSTROPHE:
                    code = BoatKeycodes.KEY_APOSTROPHE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_ENTER:
                    code = BoatKeycodes.KEY_ENTER;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_LEFT_SHIFT:
                    code = BoatKeycodes.KEY_LEFTSHIFT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_COMMA:
                    code = BoatKeycodes.KEY_COMMA;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_PERIOD:
                    code = BoatKeycodes.KEY_DOT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_SLASH:
                    code = BoatKeycodes.KEY_SLASH;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_RIGHT_SHIFT:
                    code = BoatKeycodes.KEY_RIGHTSHIFT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_LEFT_CONTROL:
                    code = BoatKeycodes.KEY_LEFTCTRL;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_LEFT_ALT:
                    code = BoatKeycodes.KEY_LEFTALT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_SPACE:
                    code = BoatKeycodes.KEY_SPACE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_RIGHT_ALT:
                    code = BoatKeycodes.KEY_RIGHTALT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_RIGHT_CONTROL:
                    code = BoatKeycodes.KEY_RIGHTCTRL;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_INSERT:
                    code = BoatKeycodes.KEY_INSERT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_HOME:
                    code = BoatKeycodes.KEY_HOME;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_PAGE_UP:
                    code = BoatKeycodes.KEY_PAGEUP;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_DELETE:
                    code = BoatKeycodes.KEY_DELETE;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_END:
                    code = BoatKeycodes.KEY_END;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_PAGE_DOWN:
                    code = BoatKeycodes.KEY_PAGEDOWN;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_UP:
                    code = BoatKeycodes.KEY_UP;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_DOWN:
                    code = BoatKeycodes.KEY_DOWN;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_LEFT:
                    code = BoatKeycodes.KEY_LEFT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_RIGHT:
                    code = BoatKeycodes.KEY_RIGHT;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_NUM_LOCK:
                    code = BoatKeycodes.KEY_NUMLOCK;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_A:
                    code = BoatKeycodes.KEY_A;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_B:
                    code = BoatKeycodes.KEY_B;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_C:
                    code = BoatKeycodes.KEY_C;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_D:
                    code = BoatKeycodes.KEY_D;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_E:
                    code = BoatKeycodes.KEY_E;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_F:
                    code = BoatKeycodes.KEY_F;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_G:
                    code = BoatKeycodes.KEY_G;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_H:
                    code = BoatKeycodes.KEY_H;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_I:
                    code = BoatKeycodes.KEY_I;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_J:
                    code = BoatKeycodes.KEY_J;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_K:
                    code = BoatKeycodes.KEY_K;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_L:
                    code = BoatKeycodes.KEY_L;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_M:
                    code = BoatKeycodes.KEY_M;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_N:
                    code = BoatKeycodes.KEY_N;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_O:
                    code = BoatKeycodes.KEY_O;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_P:
                    code = BoatKeycodes.KEY_P;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_Q:
                    code = BoatKeycodes.KEY_Q;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_R:
                    code = BoatKeycodes.KEY_R;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_S:
                    code = BoatKeycodes.KEY_S;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_T:
                    code = BoatKeycodes.KEY_T;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_U:
                    code = BoatKeycodes.KEY_U;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_V:
                    code = BoatKeycodes.KEY_V;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_W:
                    code = BoatKeycodes.KEY_W;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_X:
                    code = BoatKeycodes.KEY_X;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_Y:
                    code = BoatKeycodes.KEY_Y;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_Z:
                    code = BoatKeycodes.KEY_Z;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_0:
                    code = BoatKeycodes.KEY_0;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_1:
                    code = BoatKeycodes.KEY_1;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_2:
                    code = BoatKeycodes.KEY_2;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_3:
                    code = BoatKeycodes.KEY_3;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_4:
                    code = BoatKeycodes.KEY_4;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_5:
                    code = BoatKeycodes.KEY_5;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_6:
                    code = BoatKeycodes.KEY_6;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_7:
                    code = BoatKeycodes.KEY_7;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_8:
                    code = BoatKeycodes.KEY_8;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_9:
                    code = BoatKeycodes.KEY_9;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_0:
                    code = BoatKeycodes.KEY_KP0;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_1:
                    code = BoatKeycodes.KEY_KP1;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_2:
                    code = BoatKeycodes.KEY_KP2;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_3:
                    code = BoatKeycodes.KEY_KP3;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_4:
                    code = BoatKeycodes.KEY_KP4;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_5:
                    code = BoatKeycodes.KEY_KP5;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_6:
                    code = BoatKeycodes.KEY_KP6;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_7:
                    code = BoatKeycodes.KEY_KP7;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_8:
                    code = BoatKeycodes.KEY_KP8;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_9:
                    code = BoatKeycodes.KEY_KP9;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_ADD:
                    code = BoatKeycodes.KEY_KPPLUS;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_DIVIDE:
                    code = BoatKeycodes.KEY_KPSLASH;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_ENTER:
                    code = BoatKeycodes.KEY_KPENTER;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_EQUAL:
                    code = BoatKeycodes.KEY_KPEQUAL;
                    break;
                case LwjglGlfwKeycode.GLFW_KEY_KP_SUBTRACT:
                    code = BoatKeycodes.KEY_KPMINUS;
                    break;
                default:
                    code = raw;
                    break;
            }

            return code;
        }
        else {
            return raw;
        }
    }

}
