package com.tungsten.hmclpe.skin.body.cube;

import com.tungsten.hmclpe.skin.body.MainCube;

public class Hat extends MainCube
{
    protected float[] hat_texcoords;
    
    public Hat(float scale) {
        super(9.0f * scale, 9.0f * scale, 9.0f * scale, 0.0f * scale, 12.0f * scale, 0.0f * scale, 0.25f, 0.0f, 1.0f, 0.0f, 5.0f, -5.0f);
        this.AddTextures(this.hat_texcoords = new float[] { 0.625f, 0.25f, 0.625f, 0.125f, 0.75f, 0.125f, 0.75f, 0.25f, 0.625f, 0.125f, 0.625f, 0.0f, 0.75f, 0.0f, 0.75f, 0.125f, 0.75f, 0.125f, 0.75f, 0.0f, 0.875f, 0.0f, 0.875f, 0.125f, 0.75f, 0.25f, 0.75f, 0.125f, 0.875f, 0.125f, 0.875f, 0.25f, 0.5f, 0.25f, 0.5f, 0.125f, 0.625f, 0.125f, 0.625f, 0.25f, 0.875f, 0.25f, 0.875f, 0.125f, 1.0f, 0.125f, 1.0f, 0.25f });
    }
}
