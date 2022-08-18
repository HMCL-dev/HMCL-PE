package com.tungsten.hmclpe.launcher.game;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;
import com.tungsten.hmclpe.utils.Lang;
import com.tungsten.hmclpe.utils.platform.OperatingSystem;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author huangyuhui
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public final class Arguments {

    @SerializedName("game")
    private final List<Argument> game;
    @SerializedName("jvm")
    private final List<Argument> jvm;

    public Arguments() {
        this(null, null);
    }

    public Arguments(List<Argument> game, List<Argument> jvm) {
        this.game = game;
        this.jvm = jvm;
    }

    @Nullable
    public List<Argument> getGame() {
        return game == null ? null : Collections.unmodifiableList(game);
    }

    public Arguments withGame(List<Argument> game) {
        return new Arguments(game, jvm);
    }

    @Nullable
    public List<Argument> getJvm() {
        return jvm == null ? null : Collections.unmodifiableList(jvm);
    }

    public Arguments withJvm(List<Argument> jvm) {
        return new Arguments(game, jvm);
    }

    public Arguments addGameArguments(String... gameArguments) {
        return addGameArguments(Arrays.asList(gameArguments));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Arguments addGameArguments(List<String> gameArguments) {
        List<Argument> list = gameArguments.stream().map(StringArgument::new).collect(Collectors.toList());
        return new Arguments(Lang.merge(getGame(), list), getJvm());
    }

    public Arguments addJVMArguments(String... jvmArguments) {
        return addJVMArguments(Arrays.asList(jvmArguments));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Arguments addJVMArguments(List<String> jvmArguments) {
        List<Argument> list = jvmArguments.stream().map(StringArgument::new).collect(Collectors.toList());
        return new Arguments(getGame(), Lang.merge(getJvm(), list));
    }

    public static Arguments merge(Arguments a, Arguments b) {
        if (a == null)
            return b;
        else if (b == null)
            return a;
        else
            return new Arguments(
                    a.game == null && b.game == null ? null : Lang.merge(a.game, b.game),
                    a.jvm == null && b.jvm == null ? null : Lang.merge(a.jvm, b.jvm));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> parseStringArguments(List<String> arguments, Map<String, String> keys) {
        return arguments.stream().filter(Objects::nonNull).flatMap(str -> new StringArgument(str).toString(keys, Collections.emptyMap()).stream()).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> parseArguments(List<Argument> arguments, Map<String, String> keys) {
        return parseArguments(arguments, keys, Collections.emptyMap());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> parseArguments(List<Argument> arguments, Map<String, String> keys, Map<String, Boolean> features) {
        return arguments.stream().filter(Objects::nonNull).flatMap(arg -> arg.toString(keys, features).stream()).collect(Collectors.toList());
    }

    public static final List<Argument> DEFAULT_JVM_ARGUMENTS;
    public static final List<Argument> DEFAULT_GAME_ARGUMENTS;

    static {
        List<Argument> jvm = new LinkedList<>();
        jvm.add(new RuledArgument(Collections.singletonList(new CompatibilityRule(CompatibilityRule.Action.ALLOW, new OSRestriction(OperatingSystem.WINDOWS))), Collections.singletonList("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump")));
        jvm.add(new RuledArgument(Collections.singletonList(new CompatibilityRule(CompatibilityRule.Action.ALLOW, new OSRestriction(OperatingSystem.WINDOWS, "^10\\."))), Arrays.asList("-Dos.name=Windows 10", "-Dos.version=10.0")));
        jvm.add(new StringArgument("-Djava.library.path=${natives_directory}"));
        jvm.add(new StringArgument("-Dminecraft.launcher.brand=${launcher_name}"));
        jvm.add(new StringArgument("-Dminecraft.launcher.version=${launcher_version}"));
        jvm.add(new StringArgument("-cp"));
        jvm.add(new StringArgument("${classpath}"));
        DEFAULT_JVM_ARGUMENTS = Collections.unmodifiableList(jvm);

        List<Argument> game = new LinkedList<>();
        game.add(new RuledArgument(Collections.singletonList(new CompatibilityRule(CompatibilityRule.Action.ALLOW, null, Collections.singletonMap("has_custom_resolution", true))), Arrays.asList("--width", "${resolution_width}", "--height", "${resolution_height}")));
        DEFAULT_GAME_ARGUMENTS = Collections.unmodifiableList(game);
    }
}
