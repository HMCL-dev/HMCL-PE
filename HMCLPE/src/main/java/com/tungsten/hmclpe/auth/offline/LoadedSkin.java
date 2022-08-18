package com.tungsten.hmclpe.auth.offline;

import com.tungsten.hmclpe.auth.yggdrasil.TextureModel;

public class LoadedSkin {
    private final TextureModel model;
    private final Texture skin;
    private final Texture cape;

    public LoadedSkin(TextureModel model, Texture skin, Texture cape) {
        this.model = model;
        this.skin = skin;
        this.cape = cape;
    }

    public TextureModel getModel() {
        return model;
    }

    public Texture getSkin() {
        return skin;
    }

    public Texture getCape() {
        return cape;
    }
}