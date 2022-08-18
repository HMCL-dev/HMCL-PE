package com.tungsten.hmclpe.auth.yggdrasil;

import androidx.annotation.Nullable;

import com.google.gson.JsonParseException;

import java.util.Map;

import com.google.gson.annotations.JsonAdapter;
import com.tungsten.hmclpe.utils.gson.tools.Validation;
import com.tungsten.hmclpe.utils.string.StringUtils;

/**
 *
 * @author huang
 */
public final class User implements Validation {

    private final String id;

    @JsonAdapter(PropertyMapSerializer.class)
    private final Map<String, String> properties;

    public User(String id) {
        this(id, null);
    }

    public User(String id, @Nullable Map<String, String> properties) {
        this.id = id;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(id))
            throw new JsonParseException("User id cannot be empty.");
    }
}