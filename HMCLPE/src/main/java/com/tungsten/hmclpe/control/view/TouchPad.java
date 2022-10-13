package com.tungsten.hmclpe.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.InputBridge;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;
import com.tungsten.hmclpe.launcher.launch.MCOptionUtils;
import com.tungsten.hmclpe.utils.io.SocketServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Objects;

@SuppressLint("ViewConstructor")
public class TouchPad extends View {

    private String rayTraceResultType;

    private static final String RAYTRACE_RESULT_TYPE_UNKNOWN = "UNKNOWN";
    private static final String RAYTRACE_RESULT_TYPE_MISS = "MISS";
    private static final String RAYTRACE_RESULT_TYPE_BLOCK = "BLOCK";
    private static final String RAYTRACE_RESULT_TYPE_ENTITY = "ENTITY";

    private final int launcher;
    private final int screenWidth;
    private final int screenHeight;
    private final MenuHelper menuHelper;

    private final Bitmap bitmap;
    private float startCursorX;
    private float startCursorY;

    private float downX;
    private float downY;
    private float initialX;
    private float initialY;
    private long downTime;
    private int pointerID;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (menuHelper.gameMenuSetting.enableTouch) {
                if (Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_BLOCK)) {
                    InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true);
                }
                else if (Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_ENTITY) || Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_MISS)) {
                    InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,true);
                }
                else {
                    if (menuHelper.gameMenuSetting.touchMode == 0) {
                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true);
                    }
                    if (menuHelper.gameMenuSetting.touchMode == 1) {
                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,true);
                    }
                }
            }
        }
    };

    private final Handler throwHandler = new Handler();
    private final Runnable throwRunnable = new Runnable() {
        @Override
        public void run() {
            InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_Q,true);
            InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_Q,false);
        }
    };

    public TouchPad(Context context,int launcher, int screenWidth, int screenHeight, MenuHelper menuHelper) {
        super(context);
        this.launcher = launcher;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.menuHelper = menuHelper;

        SocketServer server = new SocketServer("127.0.0.1", 2332, (server1, msg) -> {
            handleRayTraceResult(msg);
            Log.i("ReceiveRaytraceResultType", Long.toString(System.currentTimeMillis()));
        });
        server.start();

        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_cursor);
    }

    private void handleRayTraceResult(String msg) {
        switch (msg) {
            case RAYTRACE_RESULT_TYPE_MISS:
            case RAYTRACE_RESULT_TYPE_BLOCK:
            case RAYTRACE_RESULT_TYPE_ENTITY:
                rayTraceResultType = msg;
                break;
            default:
                rayTraceResultType = RAYTRACE_RESULT_TYPE_UNKNOWN;
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(screenWidth, screenHeight);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (menuHelper.gameCursorMode == 0) {
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect((int) menuHelper.cursorX, (int) menuHelper.cursorY, (int) menuHelper.cursorX + ConvertUtils.dip2px(getContext(),menuHelper.gameMenuSetting.mouseSize), (int) menuHelper.cursorY + ConvertUtils.dip2px(getContext(),menuHelper.gameMenuSetting.mouseSize));
            canvas.drawBitmap(bitmap, src, dst, new Paint(Paint.ANTI_ALIAS_FLAG));
        }
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (menuHelper.gameCursorMode == 1 && event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Log.i("StartGettingRaytraceResultType", Long.toString(System.currentTimeMillis()));
            new Thread(() -> {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    socket.connect(new InetSocketAddress("127.0.0.1", 2333));
                    byte[] data = ("refresh").getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.send(packet);
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
        }
        int guiScale = -1;
        if (menuHelper.gameDir != null) {
            MCOptionUtils.load(menuHelper.gameDir);
            String str = MCOptionUtils.get("guiScale");
            guiScale = (str == null ? 0 :Integer.parseInt(str));
            int scale = (int) Math.max(Math.min((screenWidth * menuHelper.scaleFactor) / 320, (screenHeight * menuHelper.scaleFactor) / 240), 1);
            if(scale < guiScale || guiScale == 0){
                guiScale = scale;
            }
        }
        int inventoryWidth = 0;
        int inventoryHeight = 0;
        if (guiScale != -1) {
            inventoryWidth = 20 * guiScale * 9;
            inventoryHeight = 20 * guiScale;
        }
        if (menuHelper.gameCursorMode == 1 && downX >= ((getWidth() / 2) - (inventoryWidth / 2)) && downX <= ((getWidth() / 2) + (inventoryWidth / 2)) && downY >= getHeight() - inventoryHeight) {
            int start = ((getWidth() / 2) - (inventoryWidth / 2));
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    initialY = event.getY();
                    downTime = System.currentTimeMillis();
                    if (event.getX() <= start + inventoryHeight) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_1,true);
                    }
                    if (event.getX() > start + inventoryHeight && event.getX() <= start + (2 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_2,true);
                    }
                    if (event.getX() > start + (2 * inventoryHeight) && event.getX() <= start + (3 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_3,true);
                    }
                    if (event.getX() > start + (3 * inventoryHeight) && event.getX() <= start + (4 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_4,true);
                    }
                    if (event.getX() > start + (4 * inventoryHeight) && event.getX() <= start + (5 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_5,true);
                    }
                    if (event.getX() > start + (5 * inventoryHeight) && event.getX() <= start + (6 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_6,true);
                    }
                    if (event.getX() > start + (6 * inventoryHeight) && event.getX() <= start + (7 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_7,true);
                    }
                    if (event.getX() > start + (7 * inventoryHeight) && event.getX() <= start + (8 * inventoryHeight)) {
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_8,true);
                    }
                    if (event.getX() > start + (8 * inventoryHeight)){
                        InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_9,true);
                    }
                    throwHandler.postDelayed(throwRunnable,800);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((Math.abs(event.getX() - initialX) > 1 || Math.abs(event.getY() - initialY) > 1) && System.currentTimeMillis() - downTime < 800) {
                        throwHandler.removeCallbacks(throwRunnable);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    throwHandler.removeCallbacks(throwRunnable);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_1,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_2,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_3,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_4,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_5,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_6,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_7,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_8,false);
                    InputBridge.sendEvent(launcher, LwjglGlfwKeycode.GLFW_KEY_9,false);
                    break;
            }
        }
        else {
            if (menuHelper.gameMenuSetting.mouseMode == 0 && menuHelper.gameCursorMode == 0) {
                menuHelper.cursorX = event.getX();
                menuHelper.cursorY = event.getY();
                menuHelper.pointerX = event.getX();
                menuHelper.pointerY = event.getY();
                InputBridge.setPointer(launcher,(int) (event.getX() * menuHelper.scaleFactor),(int) (event.getY() * menuHelper.scaleFactor));
            }
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    initialY = event.getY();
                    downTime = System.currentTimeMillis();
                    pointerID = event.getPointerId(event.getActionIndex());
                    if (menuHelper.gameMenuSetting.mouseMode == 1 && menuHelper.gameCursorMode == 0) {
                        startCursorX = menuHelper.cursorX;
                        startCursorY = menuHelper.cursorY;
                    }
                    if (menuHelper.gameCursorMode == 1 && (!menuHelper.gameMenuSetting.disableHalfScreen || initialX > (screenWidth >> 1))) {
                        handler.postDelayed(runnable,400);
                    }
                    if (menuHelper.gameMenuSetting.mouseMode == 0 && menuHelper.gameCursorMode == 0) {
                        if (launcher == 1) {
                            InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true);
                        }
                        if (launcher == 2) {
                            new Handler().postDelayed(() -> InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true),20);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (menuHelper.gameMenuSetting.mouseMode == 1 && menuHelper.gameCursorMode == 0) {
                        float targetX;
                        float targetY;
                        if (startCursorX + ((event.getX() - initialX) * menuHelper.gameMenuSetting.mouseSpeed) < 0) {
                            targetX = 0;
                        }
                        else if (startCursorX + ((event.getX() - initialX) * menuHelper.gameMenuSetting.mouseSpeed) > screenWidth) {
                            targetX = screenWidth;
                        }
                        else {
                            targetX = startCursorX + ((event.getX() - initialX) * menuHelper.gameMenuSetting.mouseSpeed);
                        }
                        if (startCursorY + ((event.getY() - initialY) * menuHelper.gameMenuSetting.mouseSpeed) < 0) {
                            targetY = 0;
                        }
                        else if (startCursorY + ((event.getY() - initialY) * menuHelper.gameMenuSetting.mouseSpeed) > screenHeight) {
                            targetY = screenHeight;
                        }
                        else {
                            targetY = startCursorY + ((event.getY() - initialY) * menuHelper.gameMenuSetting.mouseSpeed);
                        }
                        menuHelper.cursorX = targetX;
                        menuHelper.cursorY = targetY;
                        menuHelper.pointerX = targetX;
                        menuHelper.pointerY = targetY;
                        InputBridge.setPointer(launcher,(int) (targetX * menuHelper.scaleFactor),(int) (targetY * menuHelper.scaleFactor));
                    }
                    if (menuHelper.gameCursorMode == 1 && (!menuHelper.gameMenuSetting.disableHalfScreen || initialX > (screenWidth >> 1)) && event.getPointerId(event.getActionIndex()) == pointerID) {
                        menuHelper.viewManager.setGamePointer("1",true,event.getX() - initialX,event.getY() - initialY);
                        if ((Math.abs(event.getX() - initialX) > 1 || Math.abs(event.getY() - initialY) > 1) && System.currentTimeMillis() - downTime < 400) {
                            handler.removeCallbacks(runnable);
                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (menuHelper.gameMenuSetting.mouseMode == 0 && menuHelper.gameCursorMode == 0) {
                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,false);
                    }
                    if (event.getPointerId(event.getActionIndex()) == pointerID) {
                        if (menuHelper.gameCursorMode == 1 && event.getPointerId(event.getActionIndex()) == pointerID && (!menuHelper.gameMenuSetting.disableHalfScreen || initialX > (screenWidth >> 1))){
                            menuHelper.viewManager.setGamePointer("1",false,event.getX() - initialX,event.getY() - initialY);
                            handler.removeCallbacks(runnable);
                            if (Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_BLOCK)) {
                                InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,false);
                            }
                            else if (Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_ENTITY) || Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_MISS)) {
                                InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,false);
                            }
                            else {
                                if (menuHelper.gameMenuSetting.touchMode == 0) {
                                    InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,false);
                                }
                                if (menuHelper.gameMenuSetting.touchMode == 1) {
                                    InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,false);
                                }
                            }
                        }
                        if (System.currentTimeMillis() - downTime <= 200 && Math.abs(event.getX() - initialX) <= 10 && Math.abs(event.getY() - initialY) <= 10) {
                            if (menuHelper.gameMenuSetting.mouseMode == 1 && menuHelper.gameCursorMode == 0) {
                                InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true);
                                InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,false);
                            }
                            if (menuHelper.gameCursorMode == 1 && event.getPointerId(event.getActionIndex()) == pointerID && (!menuHelper.gameMenuSetting.disableHalfScreen || initialX > (screenWidth >> 1))) {
                                if (menuHelper.gameMenuSetting.enableTouch) {
                                    if (Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_BLOCK)) {
                                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,true);
                                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,false);
                                    }
                                    else if (Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_ENTITY) || Objects.equals(rayTraceResultType, RAYTRACE_RESULT_TYPE_MISS)) {
                                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true);
                                        InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,false);
                                    }
                                    else {
                                        if (menuHelper.gameMenuSetting.touchMode == 0) {
                                            InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,true);
                                            InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_RIGHT,false);
                                        }
                                        if (menuHelper.gameMenuSetting.touchMode == 1) {
                                            InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,true);
                                            InputBridge.sendMouseEvent(launcher,InputBridge.MOUSE_LEFT,false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }
}
