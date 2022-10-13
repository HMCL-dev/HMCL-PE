package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.kdt.pojavlaunch.function.PojavCallback;
import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.utils.Tools;

import org.lwjgl.glfw.CallbackBridge;

import java.util.Vector;

public class BaseMainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    public TextureView minecraftGLView;
    public float scaleFactor = 1.0F;
    public static boolean isInputStackCall;

    public PojavCallback pojavCallback;

    boolean mouseMode;
    int output = 0;

    protected void init(String gameDir , boolean highVersion) {

        isInputStackCall = highVersion;

        minecraftGLView = findViewById(R.id.main_game_render_view);
        minecraftGLView.setOpaque(false);

        minecraftGLView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        startMouseThread();
        pojavCallback.onSurfaceTextureAvailable(surfaceTexture,i,i1);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        pojavCallback.onSurfaceTextureSizeChanged(surfaceTexture,i,i1);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        if (output == 1) {
            pojavCallback.onPicOutput();
            output++;
        }
        if (output < 1) {
            output++;
        }
    }

    public static void onExit(Context ctx, int code) {
        ((BaseMainActivity) ctx).pojavCallback.onExit(code);
    }

    public void startGame(String javaPath,String home,boolean highVersion,final Vector<String> args, String renderer,String gameDir,String glesVersion) {
        Thread JVMThread = new Thread(() -> {
            runOnUiThread(() -> {
                pojavCallback.onStart();
            });
            try {
                JREUtils.redirectAndPrintJRELog(this);
                Tools.launchMinecraft(this, javaPath,home,renderer, args,gameDir,glesVersion);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                runOnUiThread(() -> {
                    pojavCallback.onError(new Exception(throwable));
                });
            }
        }, "JVM Main thread");
        JVMThread.setPriority(Thread.MAX_PRIORITY);
        JVMThread.start();
    }

    public void startMouseThread() {
        Thread virtualMouseGrabThread = new Thread(() -> {
            while (true) {
                if (!CallbackBridge.isGrabbing() && mouseMode) {
                    mouseModeHandler.sendEmptyMessage(1);
                    mouseMode = false;
                }
                if (CallbackBridge.isGrabbing() && !mouseMode) {
                    mouseModeHandler.sendEmptyMessage(0);
                    mouseMode = true;
                }
            }
        }, "VirtualMouseGrabThread");
        virtualMouseGrabThread.setPriority(Thread.MIN_PRIORITY);
        virtualMouseGrabThread.start();
    }

    @SuppressLint("HandlerLeak")
    public final Handler mouseModeHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                pojavCallback.onCursorModeChange(0);
            }
            if (msg.what == 1) {
                pojavCallback.onCursorModeChange(1);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 1);
    }

    @Override
    protected void onStop() {
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 0);
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (minecraftGLView != null && minecraftGLView.getSurfaceTexture() != null) {
            minecraftGLView.post(() -> {
                pojavCallback.onSurfaceTextureSizeChanged(minecraftGLView.getSurfaceTexture(),minecraftGLView.getWidth(),minecraftGLView.getHeight());
            });
        }
    }
}
