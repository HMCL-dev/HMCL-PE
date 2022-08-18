package com.tungsten.hmclpe.launcher.mod;

import com.google.gson.JsonParseException;
import com.moandjiezana.toml.Toml;
import com.tungsten.hmclpe.utils.io.ZipTools;
import com.tungsten.hmclpe.utils.string.StringUtils;

import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class ForgeNewModMetadata {

    private final String modLoader;

    private final String loaderVersion;

    private final String logoFile;

    private final String license;

    private final List<Mod> mods;

    public ForgeNewModMetadata() {
        this("", "", "", "", Collections.emptyList());
    }

    public ForgeNewModMetadata(String modLoader, String loaderVersion, String logoFile, String license, List<Mod> mods) {
        this.modLoader = modLoader;
        this.loaderVersion = loaderVersion;
        this.logoFile = logoFile;
        this.license = license;
        this.mods = mods;
    }

    public String getModLoader() {
        return modLoader;
    }

    public String getLoaderVersion() {
        return loaderVersion;
    }

    public String getLogoFile() {
        return logoFile;
    }

    public String getLicense() {
        return license;
    }

    public List<Mod> getMods() {
        return mods;
    }

    public static class Mod {
        private final String modId;
        private final String version;
        private final String displayName;
        private final String side;
        private final String displayURL;
        private final String authors;
        private final String description;

        public Mod() {
            this("", "", "", "", "", "", "");
        }

        public Mod(String modId, String version, String displayName, String side, String displayURL, String authors, String description) {
            this.modId = modId;
            this.version = version;
            this.displayName = displayName;
            this.side = side;
            this.displayURL = displayURL;
            this.authors = authors;
            this.description = description;
        }

        public String getModId() {
            return modId;
        }

        public String getVersion() {
            return version;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getSide() {
            return side;
        }

        public String getDisplayURL() {
            return displayURL;
        }

        public String getAuthors() {
            return authors;
        }

        public String getDescription() {
            return description;
        }
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile) throws IOException, JsonParseException {
        String s = ZipTools.readNormalMeta(modFile.toString(),"META-INF/mods.toml");
        InputStream inputStream = ZipTools.getFileInputStream(modFile.toString(),"META-INF/MANIFEST.MF");
        if (StringUtils.isBlank(s)) {
            throw new IOException("File " + modFile + " is not a Forge1.13+ mod.");
        }
        ForgeNewModMetadata metadata = new Toml().read(s).to(ForgeNewModMetadata.class);
        if (metadata == null || metadata.getMods().isEmpty())
            throw new IOException("Mod " + modFile + " `mods.toml` is malformed..");
        Mod mod = metadata.getMods().get(0);
        String jarVersion = "";
        if (inputStream != null) {
            try {
                Manifest manifest = new Manifest(inputStream);
                jarVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream.close();
        }
        return new LocalModFile(modManager, modManager.getLocalMod(mod.getModId(), ModLoaderType.FORGE), modFile, mod.getDisplayName(), new LocalModFile.Description(mod.getDescription()),
                mod.getAuthors(), mod.getVersion().replace("${file.jarVersion}", jarVersion), "",
                mod.getDisplayURL(),
                metadata.getLogoFile());
    }
}