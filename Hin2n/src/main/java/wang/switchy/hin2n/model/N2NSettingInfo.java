package wang.switchy.hin2n.model;

import android.os.Parcel;
import android.os.Parcelable;

import wang.switchy.hin2n.storage.db.base.model.N2NSettingModel;

/**
 * Created by janiszhang on 2018/5/11.
 */

public class N2NSettingInfo implements Parcelable {

    Long id;
    String name;
    int version;
    int ipMode;
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
    int traceLevel;
    boolean useHttpTunnel;
    String gatewayIp;
    String dnsServer;
    String encryptionMode;
    boolean headerEnc;

    public N2NSettingInfo(N2NSettingModel n2NSettingModel) {
        this.id = n2NSettingModel.getId();
        this.name = n2NSettingModel.getName();
        this.version = n2NSettingModel.getVersion();
        this.ipMode = n2NSettingModel.getIpMode();
        this.ip = n2NSettingModel.getIp();
        this.netmask = n2NSettingModel.getNetmask();
        this.community = n2NSettingModel.getCommunity();
        this.password = n2NSettingModel.getPassword();
        this.devDesc = n2NSettingModel.getDevDesc();
        this.superNode = n2NSettingModel.getSuperNode();
        this.moreSettings = n2NSettingModel.getMoreSettings();
        this.superNodeBackup = n2NSettingModel.getSuperNodeBackup();
        this.macAddr = n2NSettingModel.getMacAddr();
        this.mtu = n2NSettingModel.getMtu();
        this.localIP = n2NSettingModel.getLocalIP();
        this.holePunchInterval = n2NSettingModel.getHolePunchInterval();
        this.resoveSupernodeIP = n2NSettingModel.getResoveSupernodeIP();
        this.localPort = n2NSettingModel.getLocalPort();
        this.allowRouting = n2NSettingModel.getAllowRouting();
        this.dropMuticast = n2NSettingModel.getDropMuticast();
        this.traceLevel = n2NSettingModel.getTraceLevel();
        this.useHttpTunnel = n2NSettingModel.isUseHttpTunnel();
        this.gatewayIp = n2NSettingModel.getGatewayIp();
        this.dnsServer = n2NSettingModel.getDnsServer();
        this.encryptionMode = n2NSettingModel.getEncryptionMode();
        this.headerEnc = n2NSettingModel.getHeaderEnc();
    }

    protected N2NSettingInfo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        version = in.readInt();
        ipMode = in.readInt();
        ip = in.readString();
        netmask = in.readString();
        community = in.readString();
        password = in.readString();
        devDesc = in.readString();
        superNode = in.readString();
        moreSettings = in.readByte() != 0;
        superNodeBackup = in.readString();
        macAddr = in.readString();
        mtu = in.readInt();
        localIP = in.readString();
        holePunchInterval = in.readInt();
        resoveSupernodeIP = in.readByte() != 0;
        localPort = in.readInt();
        allowRouting = in.readByte() != 0;
        dropMuticast = in.readByte() != 0;
        traceLevel = in.readInt();
        useHttpTunnel = in.readByte() != 0;
        gatewayIp = in.readString();
        dnsServer = in.readString();
        encryptionMode = in.readString();
        headerEnc = in.readByte() != 0;
    }

    public static final Creator<N2NSettingInfo> CREATOR = new Creator<N2NSettingInfo>() {
        @Override
        public N2NSettingInfo createFromParcel(Parcel in) {
            return new N2NSettingInfo(in);
        }

        @Override
        public N2NSettingInfo[] newArray(int size) {
            return new N2NSettingInfo[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIpMode() {return ipMode;}

    public void setIpMode(int ipMode) { this.ipMode = ipMode; }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSuperNode() {
        return superNode;
    }

    public void setSuperNode(String superNode) {
        this.superNode = superNode;
    }

    public String getDevDesc() { return devDesc; }

    public void setDevDesc(String devDesc) { this.devDesc = devDesc; }

    public boolean isMoreSettings() {
        return moreSettings;
    }

    public void setMoreSettings(boolean moreSettings) {
        this.moreSettings = moreSettings;
    }

    public String getSuperNodeBackup() {
        return superNodeBackup;
    }

    public void setSuperNodeBackup(String superNodeBackup) {
        this.superNodeBackup = superNodeBackup;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public int getHolePunchInterval() {
        return holePunchInterval;
    }

    public void setHolePunchInterval(int holePunchInterval) {
        this.holePunchInterval = holePunchInterval;
    }

    public boolean isResoveSupernodeIP() {
        return resoveSupernodeIP;
    }

    public void setResoveSupernodeIP(boolean resoveSupernodeIP) {
        this.resoveSupernodeIP = resoveSupernodeIP;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public boolean isAllowRouting() {
        return allowRouting;
    }

    public void setAllowRouting(boolean allowRouting) {
        this.allowRouting = allowRouting;
    }

    public boolean isDropMuticast() {
        return dropMuticast;
    }

    public void setDropMuticast(boolean dropMuticast) {
        this.dropMuticast = dropMuticast;
    }

    public int getTraceLevel() {
        return traceLevel;
    }

    public void setTraceLevel(int traceLevel) {
        this.traceLevel = traceLevel;
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
        return gatewayIp;
    }

    public String getDnsServer() {
        return dnsServer;
    }

    public String getEncryptionMode() { return encryptionMode; }

    public boolean isHeaderEnc() {
        return headerEnc;
    }

    public void setHeaderEnc(boolean headerEnc) {
        this.headerEnc = headerEnc;
    }

    @Override
    public String toString() {
        return "N2NSettingInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", ipMode=" + ipMode +
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
                ", traceLevel=" + traceLevel +
                ", useHttpTunnel=" + useHttpTunnel +
                ", gatewayIp=" + gatewayIp +
                ", dnsServer=" + dnsServer +
                ", encryptionMode=" + encryptionMode +
                ", headerEnc=" + headerEnc +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeLong(new Long(-1));
        } else {
            parcel.writeLong(id);
        }
        parcel.writeString(name);
        parcel.writeInt(version);
        parcel.writeInt(ipMode);
        parcel.writeString(ip);
        parcel.writeString(netmask);
        parcel.writeString(community);
        parcel.writeString(password);
        parcel.writeString(devDesc);
        parcel.writeString(superNode);
        parcel.writeByte((byte) (moreSettings ? 1 : 0));
        parcel.writeString(superNodeBackup);
        parcel.writeString(macAddr);
        parcel.writeInt(mtu);
        parcel.writeString(localIP);
        parcel.writeInt(holePunchInterval);
        parcel.writeByte((byte) (resoveSupernodeIP ? 1 : 0));
        parcel.writeInt(localPort);
        parcel.writeByte((byte) (allowRouting ? 1 : 0));
        parcel.writeByte((byte) (dropMuticast ? 1 : 0));
        parcel.writeInt(traceLevel);
        parcel.writeByte((byte) (useHttpTunnel ? 1 : 0));
        parcel.writeString(gatewayIp);
        parcel.writeString(dnsServer);
        parcel.writeString(encryptionMode);
        parcel.writeByte((byte) (headerEnc ? 2 : 0));
    }
}
