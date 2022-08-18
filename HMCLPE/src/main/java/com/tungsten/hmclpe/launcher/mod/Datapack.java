package com.tungsten.hmclpe.launcher.mod;

import com.google.gson.JsonParseException;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.Logging;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.FileUtils;
import com.tungsten.hmclpe.utils.io.ZipTools;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;

public class Datapack {
    private boolean isMultiple;
    private final Path path;
    private final ArrayList<Pack> info = new ArrayList<>();

    public Datapack(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public ArrayList<Pack> getInfo() {
        return info;
    }

    public void installTo(Path worldPath) throws IOException {
        Path datapacks = worldPath.resolve("datapacks");

        Set<String> packs = new HashSet<>();
        for (Pack pack : info) packs.add(pack.getId());

        if (Files.isDirectory(datapacks)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(datapacks)) {
                for (Path datapack : directoryStream) {
                    if (Files.isDirectory(datapack) && packs.contains(FileUtils.getName(datapack)))
                        FileUtils.deleteDirectory(datapack.toFile());
                    else if (Files.isRegularFile(datapack) && packs.contains(FileUtils.getNameWithoutExtension(datapack)))
                        Files.delete(datapack);
                }
            }
        }

        if (isMultiple) {
            throw new IOException("Multiple datapacks is not supported yet");
        } else {
            FileUtils.copyFile(path.toFile(), datapacks.resolve(FileUtils.getName(path)).toFile());
        }
    }

    public void deletePack(Pack pack) throws IOException {
        Path subPath = pack.file;
        if (Files.isDirectory(subPath))
            FileUtils.deleteDirectory(subPath.toFile());
        else if (Files.isRegularFile(subPath))
            Files.delete(subPath);

        info.removeIf(p -> p.getId().equals(pack.getId()));
    }

    public void loadFromZip() throws IOException {
        if (ZipTools.isFileExist(path.toString(),"datapacks")) {
            isMultiple = true;
            com.tungsten.hmclpe.utils.file.FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/datapacks");
            com.tungsten.hmclpe.utils.file.FileUtils.createDirectory(AppManifest.DEFAULT_CACHE_DIR + "/datapacks");
            ZipTools.unzipFile(path.toString(), AppManifest.DEFAULT_CACHE_DIR + "/datapacks/multiple", false);
            loadFromDir(new File(AppManifest.DEFAULT_CACHE_DIR + "/datapacks/multiple/datapacks").toPath());
        }
        else if (ZipTools.isFileExist(path.toString(),"pack.mcmeta")) {
            isMultiple = false;
            try {
                PackMcMeta pack = JsonUtils.fromNonNullJson(ZipTools.readNormalMeta(path.toString(), "pack.mcmeta"), PackMcMeta.class);
                info.add(new Pack(path, FileUtils.getNameWithoutExtension(path), pack.getPackInfo().getDescription(), this));
            } catch (IOException | JsonParseException e) {
                Logging.LOG.log(Level.WARNING, "Failed to read datapack " + path, e);
            }
        }
        else {
            throw new IOException("Malformed datapack zip");
        }
    }

    public void loadFromDir() {
        try {
            loadFromDir(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromDir(Path dir) throws IOException {
        List<Pack> info = new ArrayList<>();

        if (Files.isDirectory(dir)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
                for (Path subDir : directoryStream) {
                    if (Files.isDirectory(subDir)) {
                        Path mcmeta = subDir.resolve("pack.mcmeta");
                        Path mcmetaDisabled = subDir.resolve("pack.mcmeta.disabled");

                        if (!Files.exists(mcmeta) && !Files.exists(mcmetaDisabled))
                            continue;

                        boolean enabled = Files.exists(mcmeta);

                        try {
                            PackMcMeta pack = enabled ? JsonUtils.fromNonNullJson(FileUtils.readText(mcmeta), PackMcMeta.class)
                                    : JsonUtils.fromNonNullJson(FileUtils.readText(mcmetaDisabled), PackMcMeta.class);
                            info.add(new Pack(enabled ? mcmeta : mcmetaDisabled, FileUtils.getName(subDir), pack.getPackInfo().getDescription(), this));
                        } catch (IOException | JsonParseException e) {
                            Logging.LOG.log(Level.WARNING, "Failed to read datapack " + subDir, e);
                        }
                    }
                    else if (Files.isRegularFile(subDir)) {
                        if (!ZipTools.isFileExist(subDir.toString(),"pack.mcmeta")) {
                            continue;
                        }
                        String name = FileUtils.getName(subDir);
                        if (name.endsWith(".disabled")) {
                            name = name.substring(0, name.length() - ".disabled".length());
                        }
                        if (!name.endsWith(".zip"))
                            continue;
                        name = StringUtils.substringBeforeLast(name, ".zip");

                        PackMcMeta pack = JsonUtils.fromNonNullJson(ZipTools.readNormalMeta(subDir.toString(),"pack.mcmeta"), PackMcMeta.class);
                        info.add(new Pack(subDir, name, pack.getPackInfo().getDescription(), this));
                    }
                }
            }
        }

        this.info.addAll(info);
    }

    public static class Pack {
        private Path file;
        private boolean active;
        private final String id;
        private final LocalModFile.Description description;
        private final Datapack datapack;

        public Pack(Path file, String id, LocalModFile.Description description, Datapack datapack) {
            this.file = file;
            this.id = id;
            this.description = description;
            this.datapack = datapack;

            Path f = Pack.this.file.toAbsolutePath();
            active = !DISABLED_EXT.equals(FileUtils.getExtension(f));
        }

        public String getId() {
            return id;
        }

        public LocalModFile.Description getDescription() {
            return description;
        }

        public Datapack getDatapack() {
            return datapack;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
            Path f = file.toAbsolutePath();
            Path newF;
            if (active)
                newF = f.getParent().resolve(FileUtils.getNameWithoutExtension(f));
            else
                newF = f.getParent().resolve(FileUtils.getName(f) + "." + DISABLED_EXT);

            try {
                Files.move(f, newF);
                Pack.this.file = newF;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static final String DISABLED_EXT = "disabled";
}