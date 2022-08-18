package com.tungsten.hmclpe.multiplayer;

import com.tungsten.hmclpe.utils.DigestUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;

import java.io.IOException;
import java.util.Random;

import wang.switchy.hin2n.service.N2NService;
import wang.switchy.hin2n.storage.db.base.model.N2NSettingModel;

public class Hin2nService extends N2NService {

    public static final int VPN_REQUEST_CODE_CREATE = 10000;
    public static final int VPN_REQUEST_CODE_JOIN = 10001;

    public static final String IP_VERIFICATION_URL = "http://101.43.66.4:8080/IPVerification/ipverification?room=";

    public static ServerType SERVER_TYPE;

    public static String COMMUNITY_CODE;
    public static String IP_PORT;

    public static N2NSettingModel getCreatorModel() {
        SERVER_TYPE = ServerType.SERVER;
        COMMUNITY_CODE = DigestUtils.encryptToMD5(Long.toString(System.currentTimeMillis())).substring(DigestUtils.encryptToMD5(Long.toString(System.currentTimeMillis())).length() - 10);
        return new N2NSettingModel(1L,
                1,
                "HMCL-PE-Local-Server-Setting",
                0,
                "1.1.1.1",
                "255.255.255.0",
                COMMUNITY_CODE,
                "HMCL-PE-Password",
                "",
                "hin2n.wang:10086",
                true,
                "",
                getRandomMacAddress(),
                1386,
                "",
                20,
                false,
                0,
                false,
                true,
                false,
                4,
                true,
                "",
                "",
                "Twofish",
                false);
    }

    public static N2NSettingModel getPlayerModel() {
        SERVER_TYPE = ServerType.CLIENT;
        String response = randomNumber() + "";
        try {
            response = NetworkUtils.doGet(NetworkUtils.toURL(IP_VERIFICATION_URL + COMMUNITY_CODE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new N2NSettingModel(1L,
                1,
                "HMCL-PE-Local-Server-Setting",
                0,
                "1.1.1." + response,
                "255.255.255.0",
                COMMUNITY_CODE,
                "HMCL-PE-Password",
                "",
                "hin2n.wang:10086",
                true,
                "",
                getRandomMacAddress(),
                1386,
                "",
                20,
                false,
                0,
                false,
                true,
                false,
                4,
                true,
                "",
                "",
                "Twofish",
                false);
    }

    private static int randomNumber() {
        Random random = new Random();
        return random.nextInt(253) + 2;
    }

    public static String getRandomMacAddress() {
        Random random = new Random();
        String[] mac = {
                String.format("%02x", 0x52),
                String.format("%02x", 0x54),
                String.format("%02x", 0x00),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff))
        };
        return String.join(":", mac);
    }

}
