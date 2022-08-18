package com.tungsten.hmclpe.control;

/** Simple interface for sending chars through whatever bridge will be necessary */
public interface CharacterSenderStrategy {
    /** Called when there is a character to delete, may be called multiple times in a row */
    void sendBackspace(int launcher);

    /** Called when we want to send enter specifically */
    void sendEnter(int launcher);

    /** Called when there is a character to send, may be called multiple times in a row */
    void sendChar(int launcher, char character);

}