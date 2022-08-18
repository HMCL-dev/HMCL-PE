package com.tungsten.hmclpe.launcher.mod.mcbbs;

import com.google.gson.JsonParseException;
import com.tungsten.hmclpe.launcher.mod.Modpack;
import com.tungsten.hmclpe.launcher.mod.ModpackProvider;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.IOUtils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public final class McbbsModpackProvider implements ModpackProvider {
    public static final McbbsModpackProvider INSTANCE = new McbbsModpackProvider();

    @Override
    public String getName() {
        return "Mcbbs";
    }

    private static Modpack fromManifestFile(String json, Charset encoding) throws IOException, JsonParseException {
        McbbsModpackManifest manifest = JsonUtils.fromNonNullJson(json, McbbsModpackManifest.class);
        return manifest.toModpack(encoding);
    }

    @Override
    public Modpack readManifest(ZipFile zip, Path file, Charset encoding) throws IOException, JsonParseException {
        ZipArchiveEntry mcbbsPackMeta = zip.getEntry("mcbbs.packmeta");
        if (mcbbsPackMeta != null) {
            return fromManifestFile(IOUtils.readFullyAsString(zip.getInputStream(mcbbsPackMeta)), encoding);
        }
        ZipArchiveEntry manifestJson = zip.getEntry("manifest.json");
        if (manifestJson != null) {
            return fromManifestFile(IOUtils.readFullyAsString(zip.getInputStream(manifestJson)), encoding);
        }
        throw new IOException("`mcbbs.packmeta` or `manifest.json` cannot be found");
    }
}