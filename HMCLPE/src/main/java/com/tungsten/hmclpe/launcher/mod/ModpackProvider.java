package com.tungsten.hmclpe.launcher.mod;

import com.google.gson.JsonParseException;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface ModpackProvider {

    String getName();

    /**
     * @param zipFile the opened modpack zip file.
     * @param file the modpack zip file path.
     * @param encoding encoding of zip file.
     * @throws IOException if the file is not a valid zip file.
     * @throws JsonParseException if the manifest.json is missing or malformed.
     * @return the manifest.
     */
    Modpack readManifest(ZipFile zipFile, Path file, Charset encoding) throws IOException, JsonParseException;
}