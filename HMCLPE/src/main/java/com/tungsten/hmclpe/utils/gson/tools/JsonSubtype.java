package com.tungsten.hmclpe.utils.gson.tools;

public @interface JsonSubtype {
    Class<?> clazz();

    String name();
}
