package wang.switchy.hin2n.model;

import java.util.Random;
import java.util.Vector;

public class EdgeCmd {
    public int edgeType;    // 0: v1, 1: v2, 2: v2s 3: v3
    public int ipMode;
    public String ipAddr;
    public String ipNetmask;
    public String[] supernodes;
    public String community;
    public String devDesc;
    public String encKey;
    public String encKeyFile;
    public String macAddr;
    public int mtu;
    public String localIP;
    public int holePunchInterval;
    public boolean reResoveSupernodeIP;
    public int localPort;
    public boolean allowRouting;
    public boolean dropMuticast;
    public boolean httpTunnel;
    public int traceLevel;
    public int vpnFd;
    public String gatewayIp;
    public String dnsServer;
    public String logPath;
    public String encryptionMode;
    public boolean headerEnc;

    public EdgeCmd(int edgeType, int ipMode, String ipAddr, String ipNetmask, String[] supernodes, String community,
                   String encKey, String devDesc, String encKeyFile, String macAddr, int mtu, String localIP, int holePunchInterval,
                   boolean reResoveSupernodeIP, int localPort, boolean allowRouting, boolean dropMuticast,
                   boolean httpTunnel, int traceLevel, int vpnFd, String logPath, String gatewayIp, String dnsServer,
                   String encryptionMode, boolean headerEnc) {
        this.edgeType = edgeType;
        this.ipMode = ipMode;
        this.ipAddr = ipAddr;
        this.ipNetmask = ipNetmask;
        this.supernodes = supernodes;
        this.community = community;
        this.encKey = encKey;
        this.devDesc = devDesc;
        this.encKeyFile = encKeyFile;
        this.macAddr = macAddr;
        this.mtu = mtu;
        this.localIP = localIP;
        this.holePunchInterval = holePunchInterval;
        this.reResoveSupernodeIP = reResoveSupernodeIP;
        this.localPort = localPort;
        this.allowRouting = allowRouting;
        this.dropMuticast = dropMuticast;
        this.httpTunnel = httpTunnel;
        this.traceLevel = traceLevel;
        this.vpnFd = vpnFd;
        this.logPath = logPath;
        this.gatewayIp = gatewayIp;
        this.dnsServer = dnsServer;
        this.encryptionMode = encryptionMode;
        this.headerEnc = headerEnc;
    }

    public EdgeCmd(N2NSettingInfo n2NSettingInfo, int vpnFd, String logPath){
        this.edgeType = n2NSettingInfo.getVersion();
        this.ipMode = n2NSettingInfo.getIpMode();
        this.ipAddr = n2NSettingInfo.getIp();
        this.ipNetmask = n2NSettingInfo.getNetmask();
        this.supernodes = new String[2];
        this.supernodes[0] = n2NSettingInfo.getSuperNode();
        this.supernodes[1] = n2NSettingInfo.getSuperNodeBackup();
        this.community = n2NSettingInfo.getCommunity();
        this.encKey = n2NSettingInfo.getPassword();
        this.devDesc = n2NSettingInfo.getDevDesc();
        this.encKeyFile = "";
        this.macAddr = n2NSettingInfo.getMacAddr();
        this.mtu = n2NSettingInfo.getMtu();
        this.localIP = n2NSettingInfo.getLocalIP();
        this.holePunchInterval = n2NSettingInfo.getHolePunchInterval();
        this.reResoveSupernodeIP = n2NSettingInfo.isResoveSupernodeIP();
        this.localPort = n2NSettingInfo.getLocalPort();
        this.allowRouting = n2NSettingInfo.isAllowRouting();
        this.dropMuticast = n2NSettingInfo.isDropMuticast();
        this.httpTunnel = n2NSettingInfo.isUseHttpTunnel();
        this.traceLevel = n2NSettingInfo.getTraceLevel();
        this.vpnFd = vpnFd;
        this.logPath = logPath;
        this.gatewayIp = n2NSettingInfo.getGatewayIp();
        this.dnsServer = n2NSettingInfo.getDnsServer();
        this.encryptionMode = n2NSettingInfo.getEncryptionMode();
        this.headerEnc = n2NSettingInfo.isHeaderEnc();
    }

    public boolean checkValues(Vector<String> invalids) {
        if (invalids == null) {
            invalids = new Vector<String>();
        }
        invalids.clear();
        if (!checkIPV4(ipAddr)) {
            invalids.add("ipAddr");
        }
        if (!checkIPV4Mask(ipNetmask)) {
            invalids.add("ipNetmask");
        }
        if (supernodes == null || !checkSupernode(supernodes[0]) ||
                (supernodes[1] != null && !supernodes[1].isEmpty() && !checkSupernode(supernodes[1]))) {
            invalids.add("(backup)supernode");
        }
        if (!checkCommunity(community)) {
            invalids.add("community");
        }
        if (!checkEncKey(encKey)) {
            invalids.add("encKey");
        }
        if (!checkEncKeyFile(encKeyFile)) {
            invalids.add("encKeyFile");
        }
        if (!checkMacAddr(macAddr)) {
            invalids.add("macAddr");
        }
        if (!checkInt(mtu, 46, 1500)) {
            invalids.add("mut");
        }
        if (localIP != null && !localIP.isEmpty() && !checkIPV4(localIP)) {
            invalids.add("localIP");
        }
        if (!checkInt(holePunchInterval, 10, 120)) {
            invalids.add("holePunchInterval");
        }
        if (!checkInt(localPort, 0, 65535)) {
            invalids.add("localPort");
        }
        if (!checkInt(traceLevel, 0, 4)) {
            invalids.add("traceLevel");
        }
        if (!checkInt(vpnFd, 0, 65535)) {
            invalids.add("traceLevel");
        }
        if (!gatewayIp.isEmpty() && !checkIPV4(gatewayIp)) {
            invalids.add("gatewayIp");
        }
        if (!dnsServer.isEmpty() && !checkIPV4(dnsServer)) {
            invalids.add("dnsServer");
        }

        return invalids.size() == 0;
    }

    public static boolean checkIPV4(String ip) {
        if (ip == null || ip.length() < 7 || ip.length() > 15) {
            return false;
        }
        String[] split = ip.split("\\.");
        if (split == null || split.length != 4) {
            return false;
        }
        try {
            for (int i = 0; i < split.length; ++i) {
                int n = Integer.parseInt(split[i]);
                if (n < 0 || n > 255 || !String.valueOf(n).equals(split[i])) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean checkIPV4Mask(String netmask) {
        int[] arr = {0x00, 0x80, 0xC0, 0xE0, 0xF0, 0xF8, 0xFC, 0xFE, 0xFF};
        if (netmask == null || netmask.length() < 7 || netmask.length() > 15) {
            return false;
        }
        String[] split = netmask.split("\\.");
        if (split == null || split.length != 4) {
            return false;
        }
        try {
            for (int i = 0; i < split.length; ++i) {
                int n = Integer.parseInt(split[i]);
                boolean flag = false;
                for (int j = 0; j < arr.length; ++j) {
                    if (n == arr[j]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag || !String.valueOf(n).equals(split[i])) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean checkSupernode(String supernode) {
        if (supernode == null || supernode.isEmpty() || supernode.length() > 47) {
            return false;
        }
        String[] split = supernode.split(":");
        if (split == null || split.length != 2 || split[0].isEmpty()) {
            return false;
        }
        int n = Integer.parseInt(split[1]);
        if (n < 0 || n > 65535 || !String.valueOf(n).equals(split[1])) {
            return false;
        }

        return true;
    }

    public static boolean checkCommunity(String community) {
        if (community == null || community.isEmpty() || community.length() > 15) {
            return false;
        }

        return true;
    }

    public static boolean checkEncKey(String encKey) {
        return true;
    }

    public static boolean checkEncKeyFile(String encKeyFile) {
        return true;
    }

    public static boolean checkMacAddr(String mac) {
        String hex = "0123456789abcdef";
        if (mac == null || mac.length() != 17) {
            return false;
        }
        for (int i = 0; i < mac.length(); ++i) {
            char c = mac.charAt(i);
            if ((i + 1) % 3 == 0) {
                if (c != ':') {
                    return false;
                }
                continue;
            }
            if (!hex.contains(String.valueOf(c))) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkMtu(int mtu) {
        return checkInt(mtu, 46, 1500);
    }

    public static boolean checkInt(int n, int min, int max) {
        if (n < min || n > max) {
            return false;
        }

        return true;
    }


    public static String getRandomMac() {
        String mac = "", hex = "0123456789abcdef";
        Random rand = new Random();
        for (int i = 0; i < 17; ++i) {
            if ((i + 1) % 3 == 0) {
                mac += ':';
                continue;
            }
            mac += hex.charAt(rand.nextInt(16));
        }
        return mac;
    }
}
