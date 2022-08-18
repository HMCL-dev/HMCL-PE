package com.tungsten.hmclpe.launcher.game;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ExtractRules {
    public static final ExtractRules EMPTY = new ExtractRules();

    private final List<String> exclude;

    public ExtractRules() {
        this.exclude = Collections.emptyList();
    }

    public ExtractRules(List<String> exclude) {
        this.exclude = new LinkedList<>(exclude);
    }

    public List<String> getExclude() {
        return Collections.unmodifiableList(exclude);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean shouldExtract(String path) {
        return exclude.stream().noneMatch(path::startsWith);
    }

}
