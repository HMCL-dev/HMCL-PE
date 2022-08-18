package com.tungsten.hmclpe.auth.offline;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.tungsten.hmclpe.auth.yggdrasil.TextureModel;
import com.tungsten.hmclpe.utils.string.StringUtils;

public class SkinJson {
    private final String username;
    private final String skin;
    private final String cape;
    private final String elytra;

    @SerializedName(value = "textures", alternate = { "skins" })
    private final TextureJson textures;

    public SkinJson(String username, String skin, String cape, String elytra, TextureJson textures) {
        this.username = username;
        this.skin = skin;
        this.cape = cape;
        this.elytra = elytra;
        this.textures = textures;
    }

    public boolean hasSkin() {
        return StringUtils.isNotBlank(username);
    }

    @Nullable
    public TextureModel getModel() {
        if (textures != null && textures.slim != null) {
            return TextureModel.ALEX;
        } else if (textures != null && textures.defaultSkin != null) {
            return TextureModel.STEVE;
        } else {
            return null;
        }
    }

    public String getAlexModelHash() {
        if (textures != null && textures.slim != null) {
            return textures.slim;
        } else {
            return null;
        }
    }

    public String getSteveModelHash() {
        if (textures != null && textures.defaultSkin != null) {
            return textures.defaultSkin;
        } else return skin;
    }

    public String getHash() {
        TextureModel model = getModel();
        if (model == TextureModel.ALEX)
            return getAlexModelHash();
        else if (model == TextureModel.STEVE)
            return getSteveModelHash();
        else
            return null;
    }

    public String getCapeHash() {
        if (textures != null && textures.cape != null) {
            return textures.cape;
        } else return cape;
    }

    public static class TextureJson {
        @SerializedName("default")
        private final String defaultSkin;

        private final String slim;
        private final String cape;
        private final String elytra;

        public TextureJson(String defaultSkin, String slim, String cape, String elytra) {
            this.defaultSkin = defaultSkin;
            this.slim = slim;
            this.cape = cape;
            this.elytra = elytra;
        }
    }
}