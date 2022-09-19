package cosine.boat;

import static cosine.boat.utils.Architecture.ARCH_ARM;
import static cosine.boat.utils.Architecture.ARCH_ARM64;
import static cosine.boat.utils.Architecture.ARCH_X86;
import static cosine.boat.utils.Architecture.ARCH_X86_64;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.util.*;

import cosine.boat.function.BoatLaunchCallback;
import cosine.boat.utils.Architecture;
import cosine.boat.utils.BoatUtils;

public class LoadMe {

    public static String BOAT_LIB_DIR;

    public static native int chdir(String path);
    public static native void redirectStdio(String file);
    public static native void setenv(String name, String value);
    public static native int dlopen(String name);
    public static native void patchLinker();
    public static native void setupExitTrap(Context context);
    public static native void setupJLI();
    public static native int jliLaunch(String[] args);

    static {
        System.loadLibrary("loadme");
    }

    public static int launchMinecraft(Handler handler,Context context, String javaPath, String home, boolean highVersion, Vector<String> args, String renderer, String gameDir, BoatLaunchCallback callback) {

        String arch = "";
        String march = "";
        if (Architecture.getDeviceArchitecture() == ARCH_ARM) {
            arch = "aarch32";
            march = "arm";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
            arch = "aarch64";
            march = "arm64";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86) {
            arch = "i386";
            march = "x86";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86_64) {
            arch = "amd64";
            march = "x86_64";
        }

        boolean isJava17 = javaPath.endsWith("JRE17");

        File serverFile = new File(javaPath + "/lib/" + (isJava17 ? "" : arch) + "/server/libjvm.so");
        String jvmLibraryPath = serverFile.exists() ? "server" : "client";

        handler.post(callback::onStart);

        BOAT_LIB_DIR = context.getDir("runtime",0).getAbsolutePath() + "/boat";

        if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
            patchLinker();
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86_64) {

        }

        try {

			setenv("HOME", home);
			setenv("JAVA_HOME" , javaPath);
			setenv("LIBGL_MIPMAP","3");
			setenv("LIBGL_NORMALIZE","1");
            setenv("LIBGL_VSYNC","1");
            setenv("LIBGL_NOINTOVLHACK", "1");


			if (renderer.equals("VirGL")) {
                setenv("LIBGL_NAME","libGL.so.1");
                setenv("LIBEGL_NAME","libEGL.so.1");
                setenv("LIBGL_DRIVERS_PATH",BOAT_LIB_DIR + "/renderer/virgl/");
                setenv("MESA_GL_VERSION_OVERRIDE","4.3");
                setenv("MESA_GLSL_VERSION_OVERRIDE","430");
                setenv("VIRGL_VTEST_SOCKET_NAME", context.getCacheDir().getAbsolutePath() + "/.virgl_test");
                setenv("GALLIUM_DRIVER","virpipe");
                setenv("MESA_GLSL_CACHE_DIR",context.getCacheDir().getAbsolutePath());
            }
			else {
                setenv("LIBGL_NAME","libGL.so");
                setenv("LIBEGL_NAME","libEGL_wrapper.so");
                if (isJava17) {
                    setenv("LIBGL_GL", "32");
                }
            }

            // openjdk
            if (isJava17) {
                dlopen(javaPath + "/lib/libfreetype.so");
                dlopen(javaPath + "/lib/libjli.so");
                dlopen(javaPath + "/lib/" + jvmLibraryPath + "/libjvm.so");
                dlopen(javaPath + "/lib/libverify.so");
                dlopen(javaPath + "/lib/libjava.so");
                dlopen(javaPath + "/lib/libnet.so");
                dlopen(javaPath + "/lib/libnio.so");
                dlopen(javaPath + "/lib/libawt.so");
                dlopen(javaPath + "/lib/libawt_headless.so");
                dlopen(javaPath + "/lib/libfontmanager.so");
                dlopen(javaPath + "/lib/libtinyiconv.so");
                dlopen(javaPath + "/lib/libinstrument.so");
            }
            else {
                dlopen(javaPath + "/lib/" + arch + "/libfreetype.so");
                dlopen(javaPath + "/lib/" + arch + "/jli/libjli.so");
                dlopen(javaPath + "/lib/" + arch + "/" + jvmLibraryPath + "/libjvm.so");
                dlopen(javaPath + "/lib/" + arch + "/libverify.so");
                dlopen(javaPath + "/lib/" + arch + "/libjava.so");
                dlopen(javaPath + "/lib/" + arch + "/libnet.so");
                dlopen(javaPath + "/lib/" + arch + "/libnio.so");
                dlopen(javaPath + "/lib/" + arch + "/libawt.so");
                dlopen(javaPath + "/lib/" + arch + "/libawt_headless.so");
                dlopen(javaPath + "/lib/" + arch + "/libfontmanager.so");
                dlopen(javaPath + "/lib/" + arch + "/libtinyiconv.so");
                dlopen(javaPath + "/lib/" + arch + "/libinstrument.so");
            }
            dlopen(BOAT_LIB_DIR + "/libs/" + march + "/libopenal.so.1");

            if (!renderer.equals("VirGL")) {
                dlopen(BOAT_LIB_DIR + "/renderer/gl4es/" + march + "/libGL.so");
                dlopen(BOAT_LIB_DIR + "/renderer/gl4es/" + march + "/libEGL_wrapper.so");
            }
            else {
                dlopen(BOAT_LIB_DIR + "/renderer/virgl/libexpat.so.1");
                dlopen(BOAT_LIB_DIR + "/renderer/virgl/libglapi.so.0");
                dlopen(BOAT_LIB_DIR + "/renderer/virgl/libGL.so.1");
                dlopen(BOAT_LIB_DIR + "/renderer/virgl/libEGL.so.1");
                dlopen(BOAT_LIB_DIR + "/renderer/virgl/swrast_dri.so");
            }

            if (!highVersion) {
                dlopen(BOAT_LIB_DIR + "/lwjgl-2/" + march + "/liblwjgl.so");
            }
            else {
                dlopen(BOAT_LIB_DIR + "/libs/" + march + "/libglfw.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/" + march + "/liblwjgl.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/" + march + "/liblwjgl_stb.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/" + march + "/liblwjgl_tinyfd.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/" + march + "/liblwjgl_opengl.so");
            }

            setupJLI();

            setupExitTrap(context);

            redirectStdio(home + "/boat_latest_log.txt");
            chdir(gameDir);

			String finalArgs[] = new String[args.size()];
            StringBuilder sb=new StringBuilder();
			for (int i = 0; i < args.size(); i++) {
                if (!args.get(i).equals(" ")) {
                    finalArgs[i] = args.get(i);
                    System.out.println("Minecraft Args:" + finalArgs[i]);
                    sb.append(finalArgs[i]+"\n");
                }
			}
            BoatUtils.writeFile(new File(home+"/params.txt"),sb.toString());
            int exitCode = jliLaunch(finalArgs);
            System.out.println("OpenJDK exited with code : " + exitCode);
        }
        catch (Exception e) {
            e.printStackTrace();
            handler.post(() -> {
                callback.onError(e);
            });
			return 1;
        }
		return 0;
    }

    public static int startVirGLService (Context context,String home,String tmpdir) {

        String arch = "";
        if (Architecture.getDeviceArchitecture() == ARCH_ARM) {
            arch = "aarch32";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
            arch = "aarch64";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86) {
            arch = "i386";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86_64) {
            arch = "amd64";
        }

        BOAT_LIB_DIR = context.getDir("runtime",0).getAbsolutePath() + "/boat";

        if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
            patchLinker();
        }

        try {
            redirectStdio(home + "/boat_service_log.txt");

            setenv("HOME", home);
            setenv("TMPDIR", tmpdir);
            setenv("VIRGL_VTEST_SOCKET_NAME",context.getCacheDir().getAbsolutePath() + "/.virgl_test");

            dlopen(BOAT_LIB_DIR + "/renderer/virgl/libepoxy.so.0");
            dlopen(BOAT_LIB_DIR + "/renderer/virgl/libvirglrenderer.so");

            setupJLI();
            chdir(home);
            String[] finalArgs = new String[]{BOAT_LIB_DIR + "/renderer/virgl/libvirgl_test_server.so",
                    "--no-loop-or-fork",
                    "--use-gles",
                    "--socket-name",
                    context.getCacheDir().getAbsolutePath() + "/.virgl_test"};
            System.out.println("Exited with code : " + jliLaunch(finalArgs));
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public static int launchJVM (String javaPath, ArrayList<String> args, String home) {

        String arch = "";
        if (Architecture.getDeviceArchitecture() == ARCH_ARM) {
            arch = "aarch32";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
            arch = "aarch64";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86) {
            arch = "i386";
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86_64) {
            arch = "amd64";
        }

        File serverFile = new File(javaPath + "/lib/" + arch + "/server/libjvm.so");
        String jvmLibraryPath = serverFile.exists() ? "server" : "client";

        if (Architecture.getDeviceArchitecture() == ARCH_ARM64) {
            patchLinker();
        }
        if (Architecture.getDeviceArchitecture() == ARCH_X86_64) {

        }

        try {
            setenv("HOME", home);
            setenv("JAVA_HOME" , javaPath);

            dlopen(javaPath + "/lib/" + arch + "/libfreetype.so");
            dlopen(javaPath + "/lib/" + arch + "/jli/libjli.so");
            dlopen(javaPath + "/lib/" + arch + "/" + jvmLibraryPath + "/libjvm.so");
            dlopen(javaPath + "/lib/" + arch + "/libverify.so");
            dlopen(javaPath + "/lib/" + arch + "/libjava.so");
            dlopen(javaPath + "/lib/" + arch + "/libnet.so");
            dlopen(javaPath + "/lib/" + arch + "/libnio.so");
            dlopen(javaPath + "/lib/" + arch + "/libawt.so");
            dlopen(javaPath + "/lib/" + arch + "/libawt_headless.so");
            dlopen(javaPath + "/lib/" + arch + "/libfontmanager.so");

            setupJLI();

            redirectStdio(home + "/boat_api_installer_log.txt");
            chdir(home);

            String finalArgs[] = new String[args.size()];
            for (int i = 0; i < args.size(); i++) {
                if (!args.get(i).equals(" ")) {
                    finalArgs[i] = args.get(i);
                    System.out.println("JVM Args:" + finalArgs[i]);
                }
            }
            System.out.println("ApiInstaller exited with code : " + jliLaunch(finalArgs));
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

}





