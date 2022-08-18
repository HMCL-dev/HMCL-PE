package wang.switchy.hin2n.storage.db.base.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by janiszhang on 2018/5/4.
 */

@Entity(
        nameInDb = "N2NSettingList"//表名
)
public class N2NSettingModel /*implements Parcelable*/ {

    @Id(autoincrement = true)
    Long id;
    int version;
    String name;
    int ipMode;  /* 0 - fix , 1 - from supernode */
    String ip;
    String netmask;
    String community;
    /**
     * 后续需要加密存储
     */
    String password;
    String devDesc;
    String superNode;
    boolean moreSettings;
    String superNodeBackup;
    String macAddr;
    int mtu;
    String localIP;
    int holePunchInterval;
    boolean resoveSupernodeIP;
    int localPort;
    boolean allowRouting;
    boolean dropMuticast;
    boolean useHttpTunnel;
    int traceLevel;
    boolean isSelcected;
    String gatewayIp;
    String dnsServer;
    String encryptionMode;
    boolean headerEnc;


    public String getSuperNode() {
        return this.superNode;
    }

    public void setSuperNode(String superNode) {
        this.superNode = superNode;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommunity() {
        return this.community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public int getIpMode() { return this.ipMode; }

    public void setIpMode(int ipMode) { this.ipMode = ipMode; }

    public String getNetmask() {
        return this.netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDevDesc() { return this.devDesc; }

    public void setDevDesc(String devDesc) { this.devDesc = devDesc; }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsSelcected() {
        return this.isSelcected;
    }

    public void setIsSelcected(boolean isSelcected) {
        this.isSelcected = isSelcected;
    }

    public int getTraceLevel() {
        return this.traceLevel;
    }

    public void setTraceLevel(int traceLevel) {
        this.traceLevel = traceLevel;
    }

    public boolean getDropMuticast() {
        return this.dropMuticast;
    }

    public void setDropMuticast(boolean dropMuticast) {
        this.dropMuticast = dropMuticast;
    }

    public boolean getAllowRouting() {
        return this.allowRouting;
    }

    public void setAllowRouting(boolean allowRouting) {
        this.allowRouting = allowRouting;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public boolean getResoveSupernodeIP() {
        return this.resoveSupernodeIP;
    }

    public void setResoveSupernodeIP(boolean resoveSupernodeIP) {
        this.resoveSupernodeIP = resoveSupernodeIP;
    }

    public int getHolePunchInterval() {
        return this.holePunchInterval;
    }

    public void setHolePunchInterval(int holePunchInterval) {
        this.holePunchInterval = holePunchInterval;
    }

    public String getLocalIP() {
        return this.localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public int getMtu() {
        return this.mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public String getMacAddr() {
        return this.macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getSuperNodeBackup() {
        return this.superNodeBackup;
    }

    public void setSuperNodeBackup(String superNodeBackup) {
        this.superNodeBackup = superNodeBackup;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isUseHttpTunnel() {
        return useHttpTunnel;
    }

    public void setUseHttpTunnel(boolean useHttpTunnel) {
        this.useHttpTunnel = useHttpTunnel;
    }

    public String getGatewayIp() {
        return this.gatewayIp;
    }

    public String getDnsServer() {
        return this.dnsServer;
    }

    public String getEncryptionMode() {
        return this.encryptionMode;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public void setDnsServer(String dnsServer) {
        this.dnsServer = dnsServer;
    }

    public void setEncryptionMode(String mode) { this.encryptionMode = mode; }

    public boolean getHeaderEnc() {
        return this.headerEnc;
    }

    public void setHeaderEnc(boolean headerEnc) {
        this.headerEnc = headerEnc;
    }

    @Generated
    public N2NSettingModel(Long id, int version, String name, int ipMode, String ip, String netmask, String community, String password, String devDesc,
            String superNode, boolean moreSettings, String superNodeBackup, String macAddr, int mtu, String localIP, int holePunchInterval,
            boolean resoveSupernodeIP, int localPort, boolean allowRouting, boolean dropMuticast, boolean useHttpTunnel, int traceLevel,
            boolean isSelcected, String gatewayIp, String dnsServer, String encryptionMode, boolean headerEnc) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.ipMode = ipMode;
        this.ip = ip;
        this.netmask = netmask;
        this.community = community;
        this.password = password;
        this.devDesc = devDesc;
        this.superNode = superNode;
        this.moreSettings = moreSettings;
        this.superNodeBackup = superNodeBackup;
        this.macAddr = macAddr;
        this.mtu = mtu;
        this.localIP = localIP;
        this.holePunchInterval = holePunchInterval;
        this.resoveSupernodeIP = resoveSupernodeIP;
        this.localPort = localPort;
        this.allowRouting = allowRouting;
        this.dropMuticast = dropMuticast;
        this.useHttpTunnel = useHttpTunnel;
        this.traceLevel = traceLevel;
        this.isSelcected = isSelcected;
        this.gatewayIp = gatewayIp;
        this.dnsServer = dnsServer;
        this.encryptionMode = encryptionMode;
        this.headerEnc = headerEnc;
    }

    @Generated
    public N2NSettingModel() {
    }

    public boolean getMoreSettings() {
        return this.moreSettings;
    }

    public void setMoreSettings(boolean moreSettings) {
        this.moreSettings = moreSettings;
    }

    @Override
    public String toString() {
        return "N2NSettingModel{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", ipMode='" + ipMode + '\'' +
                ", ip='" + ip + '\'' +
                ", netmask='" + netmask + '\'' +
                ", community='" + community + '\'' +
                ", password='" + password + '\'' +
                ", devDesc='" + devDesc + '\'' +
                ", superNode='" + superNode + '\'' +
                ", moreSettings=" + moreSettings +
                ", superNodeBackup='" + superNodeBackup + '\'' +
                ", macAddr='" + macAddr + '\'' +
                ", mtu=" + mtu +
                ", localIP='" + localIP + '\'' +
                ", holePunchInterval=" + holePunchInterval +
                ", resoveSupernodeIP=" + resoveSupernodeIP +
                ", localPort=" + localPort +
                ", allowRouting=" + allowRouting +
                ", dropMuticast=" + dropMuticast +
                ", useHttpTunnel=" + useHttpTunnel +
                ", traceLevel=" + traceLevel +
                ", isSelcected=" + isSelcected +
                ", gatewayIp=" + gatewayIp +
                ", dnsServer=" + dnsServer +
                ", encryptionMode =" + encryptionMode +
                ", headerEnc =" + headerEnc +
                '}';
    }

    public boolean getUseHttpTunnel() {
        return this.useHttpTunnel;
    }
}
