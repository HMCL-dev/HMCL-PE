package com.tungsten.hmclpe.multiplayer;

import static com.tungsten.hmclpe.utils.Logging.LOG;

import com.tungsten.hmclpe.event.Event;
import com.tungsten.hmclpe.event.EventManager;
import com.tungsten.hmclpe.utils.Lang;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class LocalServerDetector extends Thread {

    private final EventManager<DetectedLanServerEvent> onDetectedLanServer = new EventManager<>();
    private final int retry;

    public LocalServerDetector(int retry) {
        this.retry = retry;

        setName("LocalServerDetector");
        setDaemon(true);
    }

    public EventManager<DetectedLanServerEvent> onDetectedLanServer() {
        return onDetectedLanServer;
    }

    @Override
    public void run() {
        MulticastSocket socket;
        InetAddress broadcastAddress;
        try {
            socket = new MulticastSocket(4445);
            socket.setSoTimeout(5000);
            socket.joinGroup(broadcastAddress = InetAddress.getByName("224.0.2.60"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        byte[] buf = new byte[1024];

        int tried = 0;
        while (!isInterrupted()) {
            DatagramPacket packet = new DatagramPacket(buf, 1024);

            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                if (tried++ > retry) {
                    onDetectedLanServer.fireEvent(new DetectedLanServerEvent(this, null));
                    break;
                }

                continue;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            String response = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
            LOG.fine("Local server " + packet.getAddress() + ":" + packet.getPort() + " broadcast message: " + response);
            onDetectedLanServer.fireEvent(new DetectedLanServerEvent(this, PingResponse.parsePingResponse(response)));
            break;
        }

        try {
            socket.leaveGroup(broadcastAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket.close();
    }

    public static class DetectedLanServerEvent extends Event {
        private final PingResponse lanServer;

        public DetectedLanServerEvent(Object source, PingResponse lanServer) {
            super(source);
            this.lanServer = lanServer;
        }

        public PingResponse getLanServer() {
            return lanServer;
        }
    }

    public static class PingResponse {
        private final String motd;
        private final Integer ad;

        public PingResponse(String motd, Integer ad) {
            this.motd = motd;
            this.ad = ad;
        }

        public String getMotd() {
            return motd;
        }

        public Integer getAd() {
            return ad;
        }

        public boolean isValid() {
            return ad != null;
        }

        public static PingResponse parsePingResponse(String message) {
            return new PingResponse(
                    StringUtils.substringBefore(
                            StringUtils.substringAfter(message, "[MOTD]"),
                            "[/MOTD]"),
                    Lang.toIntOrNull(StringUtils.substringBefore(
                            StringUtils.substringAfter(message, "[AD]"),
                            "[/AD]"))
            );
        }
    }
}
