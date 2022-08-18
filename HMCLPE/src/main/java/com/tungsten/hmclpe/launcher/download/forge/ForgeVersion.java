package com.tungsten.hmclpe.launcher.download.forge;

import androidx.annotation.Nullable;

import com.google.gson.JsonParseException;
import com.tungsten.hmclpe.utils.gson.tools.Validation;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author huangyuhui
 */
public class ForgeVersion implements Validation {

    private final String branch;
    private final int build;
    private final String mcversion;
    private final String modified;
    private final String version;
    private final List<File> files;

    /**
     * No-arg constructor for Gson.
     */
    @SuppressWarnings("unused")
    public ForgeVersion() {
        this(null, 0, "", null, "", Collections.emptyList());
    }

    public ForgeVersion(String branch, int build, String mcversion, String modified, String version, List<File> files) {
        this.branch = branch;
        this.build = build;
        this.mcversion = mcversion;
        this.modified = modified;
        this.version = version;
        this.files = files;
    }

    @Nullable
    public String getBranch() {
        return branch;
    }

    public int getBuild() {
        return build;
    }

    public String getGameVersion() {
        return mcversion;
    }

    @Nullable
    public String getModified() {
        return modified;
    }

    public String getVersion() {
        return version;
    }

    public List<File> getFiles() {
        return files;
    }

    @Override
    public void validate() throws JsonParseException {
        if (files == null)
            throw new JsonParseException("ForgeVersion files cannot be null");
        if (version == null)
            throw new JsonParseException("ForgeVersion version cannot be null");
        if (mcversion == null)
            throw new JsonParseException("ForgeVersion mcversion cannot be null");
    }

    public static final class File {
        private final String format;
        private final String category;
        private final String hash;

        public File() {
            this("", "", "");
        }

        public File(String format, String category, String hash) {
            this.format = format;
            this.category = category;
            this.hash = hash;
        }

        public String getFormat() {
            return format;
        }

        public String getCategory() {
            return category;
        }

        public String getHash() {
            return hash;
        }
    }
}