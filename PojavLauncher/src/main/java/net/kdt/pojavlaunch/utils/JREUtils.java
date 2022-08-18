package net.kdt.pojavlaunch.utils;

import static net.kdt.pojavlaunch.utils.Architecture.is64BitsDevice;

import android.app.Activity;
import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Surface;

import com.oracle.dalvik.VMLauncher;

import net.kdt.pojavlaunch.Logger;

import org.lwjgl.glfw.CallbackBridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class JREUtils {
    private JREUtils() {}

    public static String LD_LIBRARY_PATH;
    private static String nativeLibDir;
    public static Map<String, String> jreReleaseList;

    public static ArrayList<File> locateLibs(File path) {
        ArrayList<File> ret = new ArrayList<>();
        File[] list = path.listFiles();
        if(list != null) {for(File f : list) {
            if(f.isFile() && f.getName().endsWith(".so")) {
                ret.add(f);
            }else if(f.isDirectory()) {
                ret.addAll(locateLibs(f));
            }
        }}
        return ret;
    }

    public static void initJavaRuntime(String javaPath) {
        String path;
        if (javaPath.endsWith("default")){
            path = javaPath + "/lib/aarch64";
        }
        else {
            path = javaPath + "/lib";
        }
        dlopen(path + "/jli/libjli.so");
        dlopen(path + "/server/libjvm.so");
        dlopen(path + "/libverify.so");
        dlopen(path + "/libjava.so");
        dlopen(path + "/libnet.so");
        dlopen(path + "/libnio.so");
        dlopen(path + "/libawt.so");
        dlopen(path + "/libawt_headless.so");
        dlopen(path + "/libfreetype.so");
        dlopen(path + "/libfontmanager.so");
        dlopen(path + "/libtinyiconv.so");
        for(File f : locateLibs(new File(javaPath))) {
            dlopen(f.getAbsolutePath());
        }
        dlopen( nativeLibDir + "/libopenal.so");
    }

    public static Map<String, String> readJREReleaseProperties(String javaPath) throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        BufferedReader jreReleaseReader = new BufferedReader(new FileReader(javaPath + "/release"));
        String currLine;
        while ((currLine = jreReleaseReader.readLine()) != null) {
            if (!currLine.isEmpty() || currLine.contains("=")) {
                String[] keyValue = currLine.split("=");
                jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
            }
        }
        jreReleaseReader.close();
        return jreReleaseMap;
    }

    public static String jvmLibraryPath;
    public static void redirectAndPrintJRELog(Context context) {
        Log.v("jrelog","Log starts here");
        JREUtils.logToLogger(Logger.getInstance(context));
        Thread t = new Thread(new Runnable(){
            int failTime = 0;
            ProcessBuilder logcatPb;
            @Override
            public void run() {
                try {
                    if (logcatPb == null) {
                        logcatPb = new ProcessBuilder().command("logcat", /* "-G", "1mb", */ "-v", "brief", "-s", "jrelog:I", "LIBGL:I").redirectErrorStream(true);
                    }
                    
                    Log.i("jrelog-logcat","Clearing logcat");
                    new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
                    Log.i("jrelog-logcat","Starting logcat");
                    Process p = logcatPb.start();

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = p.getInputStream().read(buf)) != -1) {
                        String currStr = new String(buf, 0, len);
                        Logger.getInstance(context).appendToLog(currStr);
                    }
                    
                    if (p.waitFor() != 0) {
                        Log.e("jrelog-logcat", "Logcat exited with code " + p.exitValue());
                        failTime++;
                        Log.i("jrelog-logcat", (failTime <= 10 ? "Restarting logcat" : "Too many restart fails") + " (attempt " + failTime + "/10");
                        if (failTime <= 10) {
                            run();
                        } else {
                            Logger.getInstance(context).appendToLog("ERROR: Unable to get more log.");
                        }
                        return;
                    }
                } catch (Throwable e) {
                    Log.e("jrelog-logcat", "Exception on logging thread", e);
                    Logger.getInstance(context).appendToLog("Exception on logging thread:\n" + Log.getStackTraceString(e));
                }
            }
        });
        t.start();
        Log.i("jrelog-logcat","Logcat thread started");
    }

    public static void relocateLibPath(final Context ctx , String javaPath) throws IOException {

        String DIRNAME_HOME_JRE = "lib/aarch64";

        nativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;

        String libName = is64BitsDevice() ? "lib64" : "lib";
        StringBuilder ldLibraryPath = new StringBuilder();
        ldLibraryPath.append(
                javaPath + "/" +  DIRNAME_HOME_JRE + "/jli:" +
                        javaPath + "/" + DIRNAME_HOME_JRE + ":"
        );
        ldLibraryPath.append(
                "/system/" + libName + ":" +
                        "/vendor/" + libName + ":" +
                        "/vendor/" + libName + "/hw:" +
                        nativeLibDir
        );
        LD_LIBRARY_PATH = ldLibraryPath.toString();
    }

    public static void setJavaEnvironment(Activity activity,String javaPath,String home,String renderer,String glesVersion) throws Throwable {
        Map<String, String> envMap = new ArrayMap<>();
        envMap.put("POJAV_NATIVEDIR", activity.getApplicationInfo().nativeLibraryDir);
        envMap.put("JAVA_HOME", javaPath);
        envMap.put("HOME", home);
        envMap.put("TMPDIR", activity.getCacheDir().getAbsolutePath());
        envMap.put("LIBGL_MIPMAP", "3");

        // On certain GLES drivers, overloading default functions shader hack fails, so disable it
        envMap.put("LIBGL_NOINTOVLHACK", "1");

        envMap.put("LIBGL_GL", "21");

        //envMap.put("LIBGL_SHRINK","0");

        //envMap.put("LIBGL_USEVBO","0");

        // Fix white color on banner and sheep, since GL4ES 1.1.5
        envMap.put("LIBGL_NORMALIZE", "1");

        envMap.put("LIBGL_ES",glesVersion);
   
        envMap.put("MESA_GLSL_CACHE_DIR", activity.getCacheDir().getAbsolutePath());
        if (renderer != null) {
            envMap.put("MESA_GL_VERSION_OVERRIDE", renderer.equals("opengles3_virgl")?"4.3":"4.6");
            envMap.put("MESA_GLSL_VERSION_OVERRIDE", renderer.equals("opengles3_virgl")?"430":"460");
        }
        envMap.put("force_glsl_extensions_warn", "true");
        envMap.put("allow_higher_compat_version", "true");
        envMap.put("allow_glsl_extension_directive_midshader", "true");
        envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
        envMap.put("VTEST_SOCKET_NAME", activity.getCacheDir().getAbsolutePath() + "/.virgl_test");

        envMap.put("LD_LIBRARY_PATH", LD_LIBRARY_PATH);
        envMap.put("PATH", javaPath + "/bin:" + Os.getenv("PATH"));
        
        envMap.put("REGAL_GL_VENDOR", "Android");
        envMap.put("REGAL_GL_RENDERER", "Regal");
        envMap.put("REGAL_GL_VERSION", "4.5");
        if(renderer != null) {
            if (renderer.equals("opengles2_5") || renderer.equals("opengles3") || renderer.equals("opengles3_vgpu")) {
                renderer = "opengles2";
            }
            envMap.put("POJAV_RENDERER", renderer);
        }
        envMap.put("AWTSTUB_WIDTH", Integer.toString(CallbackBridge.windowWidth > 0 ? CallbackBridge.windowWidth : CallbackBridge.physicalWidth));
        envMap.put("AWTSTUB_HEIGHT", Integer.toString(CallbackBridge.windowHeight > 0 ? CallbackBridge.windowHeight : CallbackBridge.physicalHeight));

        for (Map.Entry<String, String> env : envMap.entrySet()) {
            Logger.getInstance(activity).appendToLog("Added custom env: " + env.getKey() + "=" + env.getValue());
            Os.setenv(env.getKey(), env.getValue(), true);
        }

        File serverFile = new File(javaPath + "/lib/server/libjvm.so");
        jvmLibraryPath = javaPath + "/lib/" + (serverFile.exists() ? "server" : "client");
        Log.d("DynamicLoader","Base LD_LIBRARY_PATH: " + LD_LIBRARY_PATH);
        Log.d("DynamicLoader","Internal LD_LIBRARY_PATH: " + jvmLibraryPath + ":" + LD_LIBRARY_PATH);
        setLdLibraryPath(jvmLibraryPath + ":" + LD_LIBRARY_PATH);

        // return ldLibraryPath;
    }
    
    public static int launchJavaVM(final Activity activity,String javaPath,String home,String renderer,final List<String> JVMArgs,String gameDir,String glesVersion) throws Throwable {
        relocateLibPath(activity,javaPath);

        setJavaEnvironment(activity,javaPath,home,renderer,glesVersion);

        loadGraphicsLibrary(renderer);

        List<String> userArgs = new ArrayList<>();

        userArgs.addAll(JVMArgs);
        
        initJavaRuntime(javaPath);
        setupExitTrap(activity);
        chdir(gameDir);
        userArgs.add(0,"java");

        final int exitCode = VMLauncher.launchJVM((String[]) userArgs.toArray(new String[0]));
        Logger.getInstance(activity).appendToLog("Java Exit code: " + exitCode);
        return exitCode;
    }

    public static int launchAPIInstaller(Activity activity,String javaPath, Vector<String> args, String home) {
        try {
            redirectAndPrintJRELog(activity);
            relocateLibPath(activity,javaPath);
            Os.setenv("HOME", home, true);
            Os.setenv("JAVA_HOME" , javaPath, true);
            File serverFile = new File(javaPath + "/lib/server/libjvm.so");
            jvmLibraryPath = javaPath + "/lib/" + (serverFile.exists() ? "server" : "client");
            Log.d("DynamicLoader","Base LD_LIBRARY_PATH: "+LD_LIBRARY_PATH);
            Log.d("DynamicLoader","Internal LD_LIBRARY_PATH: "+jvmLibraryPath+":"+LD_LIBRARY_PATH);
            setLdLibraryPath(jvmLibraryPath+":"+LD_LIBRARY_PATH);
            List<String> userArgs = new ArrayList<>();
            userArgs.addAll(args);
            initJavaRuntime(javaPath);
            setupExitTrap(activity.getApplication());
            chdir(home);
            userArgs.add(0,"java");
            final int exitCode = VMLauncher.launchJVM((String[]) userArgs.toArray(new String[0]));
            Logger.getInstance(activity).appendToLog("Java Exit code: " + exitCode);
            return exitCode;
        } catch (ErrnoException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<String> getJavaArgs(Context context) {
        String[] overridableArguments = new String[]{
                "-Dglfwstub.windowWidth=" + CallbackBridge.windowWidth,
                "-Dglfwstub.windowHeight=" + CallbackBridge.windowHeight,
                "-Dglfwstub.initEgl=false",
                "-Dext.net.resolvPath=" + new File(context.getFilesDir().getParent(),"resolv.conf").getAbsolutePath(),
                "-Dlog4j2.formatMsgNoLookups=true"
        };

        List<String> userArguments = new ArrayList<>();

        //Add all the arguments
        userArguments.addAll(Arrays.asList(overridableArguments));
        return userArguments;
    }

    public static String getGraphicsLibrary(String renderer) {
        String renderLibrary;
        switch (renderer){
            case "opengles2":
            case "opengles2_5":
            case "opengles3_vgpu" :
            case "opengles3":
                renderLibrary = "libgl4es_114.so";
                break;
            case "opengles3_virgl":
            case "vulkan_zink":
                renderLibrary = "libOSMesa_8.so";
                break;
            default:
                Log.w("RENDER_LIBRARY", "No renderer selected, defaulting to opengles2");
                renderLibrary = "libgl4es_114.so";
                break;
        }
        return renderLibrary;
    }

    /**
     * Open the render library in accordance to the settings.
     * It will fallback if it fails to load the library.
     * @return The name of the loaded library
     */
    public static String loadGraphicsLibrary(String renderer) {
        if(renderer == null) return null;
        String renderLibrary;
        switch (renderer){
            case "opengles2":
            case "opengles2_5":
            case "opengles3_vgpu" :
            case "opengles3":
                renderLibrary = "libgl4es_114.so";
                break;
            case "opengles3_virgl":
            case "vulkan_zink":
                renderLibrary = "libOSMesa_8.so";
                break;
            default:
                Log.w("RENDER_LIBRARY", "No renderer selected, defaulting to opengles2");
                renderLibrary = "libgl4es_114.so";
                break;
        }

        if (!dlopen(renderLibrary) && !dlopen(findInLdLibPath(renderLibrary))) {
            Log.e("RENDER_LIBRARY","Failed to load renderer " + renderLibrary + ". Falling back to GL4ES 1.1.4");
            renderer = "opengles2";
            renderLibrary = "libgl4es_114.so";
            dlopen(nativeLibDir + "/libgl4es_114.so");
        }
        return renderLibrary;
    }

    public static String findInLdLibPath(String libName) {
        if(Os.getenv("LD_LIBRARY_PATH") == null) {
            try {
                if (LD_LIBRARY_PATH != null) {
                    Os.setenv("LD_LIBRARY_PATH", LD_LIBRARY_PATH, true);
                }else{
                    return libName;
                }
            }catch (ErrnoException e) {
                e.printStackTrace();
                return libName;
            }
        }
        for (String libPath : Os.getenv("LD_LIBRARY_PATH").split(":")) {
            File f = new File(libPath, libName);
            if (f.exists() && f.isFile()) {
                return f.getAbsolutePath();
            }
        }
        return libName;
    }

    /**
     * Remove the argument from the list, if it exists
     * If the argument exists multiple times, they will all be removed.
     * @param argList The argument list to purge
     * @param argStart The argument to purge from the list.
     */
    private static void purgeArg(List<String> argList, String argStart) {
        for(int i = 0; i < argList.size(); i++) {
            final String arg = argList.get(i);
            if(arg.startsWith(argStart)) {
                argList.remove(i);
            }
        }
    }
    private static final int EGL_OPENGL_ES_BIT = 0x0001;
    private static final int EGL_OPENGL_ES2_BIT = 0x0004;
    private static final int EGL_OPENGL_ES3_BIT_KHR = 0x0040;
    private static boolean hasExtension(String extensions, String name) {
        int start = extensions.indexOf(name);
        while (start >= 0) {
            // check that we didn't find a prefix of a longer extension name
            int end = start + name.length();
            if (end == extensions.length() || extensions.charAt(end) == ' ') {
                return true;
            }
            start = extensions.indexOf(name, end);
        }
        return false;
    }
    private static int getDetectedVersion() {
        /*
         * Get all the device configurations and check the EGL_RENDERABLE_TYPE attribute
         * to determine the highest ES version supported by any config. The
         * EGL_KHR_create_context extension is required to check for ES3 support; if the
         * extension is not present this test will fail to detect ES3 support. This
         * effectively makes the extension mandatory for ES3-capable devices.
         */
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] numConfigs = new int[1];
        if (egl.eglInitialize(display, null)) {
            try {
                boolean checkES3 = hasExtension(egl.eglQueryString(display, EGL10.EGL_EXTENSIONS),
                        "EGL_KHR_create_context");
                if (egl.eglGetConfigs(display, null, 0, numConfigs)) {
                    EGLConfig[] configs = new EGLConfig[numConfigs[0]];
                    if (egl.eglGetConfigs(display, configs, numConfigs[0], numConfigs)) {
                        int highestEsVersion = 0;
                        int[] value = new int[1];
                        for (int i = 0; i < numConfigs[0]; i++) {
                            if (egl.eglGetConfigAttrib(display, configs[i],
                                    EGL10.EGL_RENDERABLE_TYPE, value)) {
                                if (checkES3 && ((value[0] & EGL_OPENGL_ES3_BIT_KHR) ==
                                        EGL_OPENGL_ES3_BIT_KHR)) {
                                    if (highestEsVersion < 3) highestEsVersion = 3;
                                } else if ((value[0] & EGL_OPENGL_ES2_BIT) == EGL_OPENGL_ES2_BIT) {
                                    if (highestEsVersion < 2) highestEsVersion = 2;
                                } else if ((value[0] & EGL_OPENGL_ES_BIT) == EGL_OPENGL_ES_BIT) {
                                    if (highestEsVersion < 1) highestEsVersion = 1;
                                }
                            } else {
                                Log.w("glesDetect", "Getting config attribute with "
                                        + "EGL10#eglGetConfigAttrib failed "
                                        + "(" + i + "/" + numConfigs[0] + "): "
                                        + egl.eglGetError());
                            }
                        }
                        return highestEsVersion;
                    } else {
                        Log.e("glesDetect", "Getting configs with EGL10#eglGetConfigs failed: "
                                + egl.eglGetError());
                        return -1;
                    }
                } else {
                    Log.e("glesDetect", "Getting number of configs with EGL10#eglGetConfigs failed: "
                            + egl.eglGetError());
                    return -2;
                }
            } finally {
                egl.eglTerminate(display);
            }
        } else {
            Log.e("glesDetect", "Couldn't initialize EGL.");
            return -3;
        }
    }
    public static native int chdir(String path);
    public static native void logToLogger(final Logger logger);
    public static native boolean dlopen(String libPath);
    public static native void setLdLibraryPath(String ldLibraryPath);
    public static native void setupBridgeWindow(Surface surface);
    public static native void setupExitTrap(Context context);
    // Obtain AWT screen pixels to render on Android SurfaceView
    public static native int[] renderAWTScreenFrame(/* Object canvas, int width, int height */);
    static {
        System.loadLibrary("pojavexec");
        System.loadLibrary("pojavexec_awt");
        dlopen("libxhook.so");
        System.loadLibrary("istdio");
    }
}
