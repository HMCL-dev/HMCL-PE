package com.tungsten.hmclpe.launcher.game;

import androidx.annotation.Nullable;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.tungsten.hmclpe.utils.gson.tools.TolerableValidationException;
import com.tungsten.hmclpe.utils.gson.tools.Validation;
import com.tungsten.hmclpe.utils.platform.Architecture;
import com.tungsten.hmclpe.utils.platform.OperatingSystem;
import com.tungsten.hmclpe.utils.string.ToStringBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A class that describes a Minecraft dependency.
 *
 * @author huangyuhui
 */
public class Library implements Comparable<Library>, Validation {

    @SerializedName("name")
    private final Artifact artifact;
    private final String url;
    private final LibrariesDownloadInfo downloads;
    private final ExtractRules extract;
    private final Map<OperatingSystem, String> natives;
    private final List<CompatibilityRule> rules;
    private final List<String> checksums;

    @SerializedName(value = "hint", alternate = {"MMC-hint"})
    private final String hint;

    @SerializedName(value = "filename", alternate = {"MMC-filename"})
    private final String fileName;

    public Library(Artifact artifact) {
        this(artifact, null, null);
    }

    public Library(Artifact artifact, String url) {
        this(artifact,url,null);
    }

    public Library(Artifact artifact, String url, LibrariesDownloadInfo downloads) {
        this(artifact, url, downloads, null, null, null, null, null, null);
    }

    public Library(Artifact artifact, String url, LibrariesDownloadInfo downloads, List<String> checksums, ExtractRules extract, Map<OperatingSystem, String> natives, List<CompatibilityRule> rules, String hint, String filename) {
        this.artifact = artifact;
        this.url = url;
        this.downloads = downloads;
        this.extract = extract;
        this.natives = natives;
        this.rules = rules;
        this.checksums = checksums;
        this.hint = hint;
        this.fileName = filename;
    }

    public String getGroupId() {
        return artifact.getGroup();
    }

    public String getArtifactId() {
        return artifact.getName();
    }

    public String getArtifactFileName() {
        return artifact.getFileName();
    }

    public String getName() {
        return artifact.toString();
    }

    public String getVersion() {
        return artifact.getVersion();
    }

    public String getClassifier() {
        if (artifact.getClassifier() == null)
            if (natives != null && natives.containsKey(OperatingSystem.CURRENT_OS))
                return natives.get(OperatingSystem.CURRENT_OS).replace("${arch}", Architecture.SYSTEM_ARCH.getBits().getBit());
            else
                return null;
        else
            return artifact.getClassifier();
    }

    public ExtractRules getExtract() {
        return extract == null ? ExtractRules.EMPTY : extract;
    }

    public boolean appliesToCurrentEnvironment() {
        return CompatibilityRule.appliesToCurrentEnvironment(rules);
    }

    public boolean isNative() {
        return natives != null && appliesToCurrentEnvironment();
    }

    protected LibraryDownloadInfo getRawDownloadInfo() {
        if (downloads != null) {
            if (isNative())
                return downloads.getClassifiers().get(getClassifier());
            else
                return downloads.getArtifact();
        } else {
            return null;
        }
    }

    public String getPath() {
        LibraryDownloadInfo temp = getRawDownloadInfo();
        if (temp != null && temp.getPath() != null)
            return temp.getPath();
        else
            return artifact.setClassifier(getClassifier()).getPath();
    }

    public LibraryDownloadInfo getDownload() {
        LibraryDownloadInfo temp = getRawDownloadInfo();
        String path = getPath();
        return new LibraryDownloadInfo(path,
                Optional.ofNullable(temp).map(LibraryDownloadInfo::getUrl).orElse(Optional.ofNullable(url).orElse("https://libraries.minecraft.net/") + path),
                temp != null ? temp.getSha1() : null,
                temp != null ? temp.getSize() : 0
        );
    }

    public boolean hasDownloadURL() {
        LibraryDownloadInfo temp = getRawDownloadInfo();
        if (temp != null) return temp.getUrl() != null;
        else return url != null;
    }

    public List<String> getChecksums() {
        return checksums;
    }

    public List<CompatibilityRule> getRules() {
        return rules;
    }

    /**
     * Hint for how to locate the library file.
     * @return null for default, "local" for location in version/&lt;version&gt;/libraries/filename
     */
    @Nullable
    public String getHint() {
        return hint;
    }

    /**
     * Available when hint is "local"
     * @return the filename of the local library in version/&lt;version&gt;/libraries/$filename
     */
    @Nullable
    public String getFileName() {
        return fileName;
    }

    public boolean is(String groupId, String artifactId) {
        return getGroupId().equals(groupId) && getArtifactId().equals(artifactId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).toString();
    }

    @Override
    public int compareTo(Library o) {
        if (getName().compareTo(o.getName()) == 0)
            return Boolean.compare(isNative(), o.isNative());
        else
            return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Library))
            return false;

        Library other = (Library) obj;
        return getName().equals(other.getName()) && (isNative() == other.isNative());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), isNative());
    }

    public Library setClassifier(String classifier) {
        return new Library(artifact.setClassifier(classifier), url, downloads, checksums, extract, natives, rules, hint, fileName);
    }

    @Override
    public void validate() throws JsonParseException, TolerableValidationException {
        if (artifact == null)
            throw new JsonParseException("Library.name cannot be null");
    }
}