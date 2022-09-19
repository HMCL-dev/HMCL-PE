package com.tungsten.hmclpe.launcher.download;

import android.app.AlertDialog;
import android.content.Context;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.game.Arguments;
import com.tungsten.hmclpe.launcher.game.Library;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.utils.Lang;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PatchMerger {

    public interface ReMergeCallback{
        void onFailed();
    }

    public static Version reMergePatch(Context context, Version gameVersionJson, Version patch, String type, ReMergeCallback callback) {
        gameVersionJson = gameVersionJson.removePatchById(type);
        if (patch != null) {
            gameVersionJson = gameVersionJson.addPatch(patch);
        }
        final List<Version> patches = gameVersionJson.getPatches();
        for (Version v : gameVersionJson.getPatches()) {
            if (v.getId().equals("game")) {
                gameVersionJson = v.setId(v.getVersion()).setVersion(null).setPriority(null).addPatch(v);
                break;
            }
        }
        if (gameVersionJson == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_unknown_error_title));
            builder.setMessage(context.getString(R.string.dialog_unknown_error_msg));
            builder.setPositiveButton(context.getString(R.string.dialog_unknown_error_title), (dialogInterface, i) -> {});
            callback.onFailed();
            builder.create().show();
        }
        else {
            patches.sort((version, t1) -> {
                if (version.getPriority() > t1.getPriority()) {
                    return -1;
                }
                else if (version.getPriority() > t1.getPriority()) {
                    return 1;
                }
                else {
                    return 0;
                }
            });
            for (Version v : patches) {
                if (!v.getId().equals("game")) {
                    if (v.getId().equals("optifine")) {
                        gameVersionJson = PatchMerger.mergeOptifinePatch(gameVersionJson,v);
                    }
                    else {
                        gameVersionJson = PatchMerger.mergePatch(gameVersionJson,v);
                    }
                }
            }
        }
        return gameVersionJson;
    }

    public static Version mergePatch(Version gameVersionJson, Version patch) {
        gameVersionJson = gameVersionJson.addPatch(patch);
        gameVersionJson = gameVersionJson.setMainClass(patch.getMainClass());
        if (patch.getMinecraftArguments().isPresent()) {
            gameVersionJson = gameVersionJson.setMinecraftArguments(patch.getMinecraftArguments().get());
        }
        if (patch.getArguments().isPresent()) {
            if (gameVersionJson.getArguments().isPresent()) {
                gameVersionJson = gameVersionJson.setArguments(Arguments.merge(gameVersionJson.getArguments().get(),patch.getArguments().get()));
            }
            else {
                gameVersionJson = gameVersionJson.setArguments(patch.getArguments().get());
            }
        }
        List<Library> libraries = new ArrayList<>(Lang.merge(gameVersionJson.getLibraries(), patch.getLibraries()));
        for (Library library : gameVersionJson.getLibraries()) {
            for (Library lib : patch.getLibraries()) {
                if (library.equals(lib)) {
                    libraries.remove(lib);
                }
                if (library.getArtifactId().equals(lib.getArtifactId()) && !library.getVersion().equals(lib.getVersion())) {
                    libraries.remove(library);
                }
            }
        }
        gameVersionJson = gameVersionJson.setLibraries(libraries);
        return gameVersionJson;
    }

    public static Version mergeOptifinePatch(Version gameVersionJson, Version patch) {
        boolean forge = false;
        for (Version v : gameVersionJson.getPatches()) {
            if (v.getId().equals("forge")) {
                forge = true;
                break;
            }
        }
        gameVersionJson = gameVersionJson.addPatch(patch);
        if (patch.getMinecraftArguments().isPresent()) {
            gameVersionJson = gameVersionJson.setMinecraftArguments(patch.getMinecraftArguments().get());
        }
        if (forge) {
            if (patch.getArguments().isPresent()) {
                if (gameVersionJson.getArguments().isPresent()) {
                    gameVersionJson = gameVersionJson.setArguments(Arguments.merge(gameVersionJson.getArguments().get(),new Arguments().addGameArguments("--tweakClass", "optifine.OptiFineForgeTweaker")));
                }
                else {
                    gameVersionJson = gameVersionJson.setArguments(new Arguments().addGameArguments("--tweakClass", "optifine.OptiFineForgeTweaker"));
                }
            }
        }
        else {
            gameVersionJson = gameVersionJson.setMainClass(patch.getMainClass());
            if (patch.getArguments().isPresent()) {
                if (gameVersionJson.getArguments().isPresent()) {
                    gameVersionJson = gameVersionJson.setArguments(Arguments.merge(gameVersionJson.getArguments().get(),patch.getArguments().get()));
                }
                else {
                    gameVersionJson = gameVersionJson.setArguments(patch.getArguments().get());
                }
            }
        }
        List<Library> libraries = new ArrayList<>(Lang.merge(gameVersionJson.getLibraries(), patch.getLibraries()));
        for (Library library : gameVersionJson.getLibraries()) {
            for (Library lib : patch.getLibraries()) {
                if (library.equals(lib)) {
                    libraries.remove(lib);
                }
                if (library.getArtifactId().equals(lib.getArtifactId()) && !library.getVersion().equals(lib.getVersion())) {
                    libraries.remove(library);
                }
            }
        }
        gameVersionJson = gameVersionJson.setLibraries(libraries);
        return gameVersionJson;
    }

}
