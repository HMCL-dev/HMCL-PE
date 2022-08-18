package com.tungsten.hmclpe.control;

import net.kdt.pojavlaunch.keyboard.LWJGLGLFWKeycode;

import org.lwjgl.glfw.CallbackBridge;

import cosine.boat.BoatInput;
import cosine.boat.keyboard.BoatKeycodes;

public class LwjglCharSender implements CharacterSenderStrategy {
    @Override
    public void sendBackspace(int launcher) {
        if (launcher == 1) {
            BoatInput.setKey(BoatKeycodes.KEY_BACKSPACE, '\u0008',true);
        }
        else if (launcher == 2) {
            CallbackBridge.sendKeycode(LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE, '\u0008', 0, 0, true);
        }
    }

    @Override
    public void sendEnter(int launcher) {
        if (launcher == 1) {
            BoatInput.setKey(BoatKeycodes.KEY_ENTER,0,true);
            BoatInput.setKey(BoatKeycodes.KEY_ENTER,0,false);
        }
        else if (launcher == 2) {
            CallbackBridge.sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        }
    }

    @Override
    public void sendChar(int launcher, char character) {
        if (launcher == 1) {
            BoatInput.setKey(0,character,true);
            BoatInput.setKey(0,character,false);
        }
        else if (launcher == 2) {
            CallbackBridge.sendChar(character, 0);
        }
    }
}