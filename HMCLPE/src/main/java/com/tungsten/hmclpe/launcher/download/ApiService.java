package com.tungsten.hmclpe.launcher.download;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.tungsten.hmclpe.manifest.AppManifest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import cosine.boat.BoatApiService;

public class ApiService extends BoatApiService {

    public static final int API_SERVICE_PORT = 6868;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppManifest.initializeManifest(getApplicationContext());
        String javaPath = AppManifest.JAVA_DIR + "/default";
        ArrayList<String> commands = intent.getExtras().getStringArrayList("commands");
        String debugDir = AppManifest.DEBUG_DIR;
        startApiInstaller(javaPath, commands, debugDir, code -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.connect(new InetSocketAddress("127.0.0.1", API_SERVICE_PORT));
                byte[] data = (code + "").getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.send(packet);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

}
