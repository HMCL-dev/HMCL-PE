//
// Created by switchwang(https://github.com/switch-st) on 2018-04-15.
//

#include <android/log.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include "edge_jni.h"

static n2n_edge_status_t status;

static int GetEdgeCmd(JNIEnv *env, jobject jcmd, n2n_edge_cmd_t *cmd);

static void *EdgeRoutine(void *);

static void ResetEdgeStatus(JNIEnv *env, uint8_t cleanup);

static void InitEdgeStatus(void);

JNIEXPORT jboolean JNICALL Java_wang_switchy_hin2n_service_N2NService_startEdge(
        JNIEnv *env,
        jobject this,
        jobject jcmd) {

#ifndef NDEBUG
    __android_log_write(ANDROID_LOG_DEBUG, "edge_jni", "in start");
#endif /* #ifndef NDEBUG */
    ResetEdgeStatus(env, 0 /* not cleanup*/);
    if (GetEdgeCmd(env, jcmd, &status.cmd) != 0) {
        ResetEdgeStatus(env, 1 /* cleanup*/);
        return JNI_FALSE;
    }

    /* only when java already created the vpn service and pass to C should we reset NON_BLOCK */
    if(status.cmd.vpn_fd > 0) {
        int val = fcntl(status.cmd.vpn_fd, F_GETFL);
        if (val == -1) {
            ResetEdgeStatus(env, 1 /* cleanup*/);
            return JNI_FALSE;
        }
        if ((val & O_NONBLOCK) == O_NONBLOCK) {
            val &= ~O_NONBLOCK;
            val = fcntl(status.cmd.vpn_fd, F_SETFL, val);
            if (val == -1) {
                ResetEdgeStatus(env, 1 /* cleanup*/);
                return JNI_FALSE;
            }
        }
    }

    if ((*env)->GetJavaVM(env, &status.jvm) != JNI_OK) {
        ResetEdgeStatus(env, 1 /* cleanup*/);
        return JNI_FALSE;
    }
    status.jobj_service = (*env)->NewGlobalRef(env, this);
    jclass cls = (*env)->FindClass(env, "wang/switchy/hin2n/model/EdgeStatus");
    if (!cls) {
        ResetEdgeStatus(env, 1 /* cleanup*/);
        return JNI_FALSE;
    }
    status.jcls_status = (*env)->NewGlobalRef(env, cls);
    jclass cls_rs = (*env)->FindClass(env, "wang/switchy/hin2n/model/EdgeStatus$RunningStatus");
    if (!cls_rs) {
        ResetEdgeStatus(env, 1 /* cleanup*/);
        return JNI_FALSE;
    }
    status.jcls_rs = (*env)->NewGlobalRef(env, cls_rs);

    switch (status.edge_type) {
        case EDGE_TYPE_V1:
            status.start_edge = start_edge_v1;
            status.stop_edge = stop_edge_v1;
            break;
        case EDGE_TYPE_V2:
            status.start_edge = start_edge_v2;
            status.stop_edge = stop_edge_v2;
            break;
        case EDGE_TYPE_V2S:
            status.start_edge = start_edge_v2s;
            status.stop_edge = stop_edge_v2s;
            break;
        case EDGE_TYPE_V3:
            status.start_edge = start_edge_v3;
            status.stop_edge = stop_edge_v3;
            break;
        default:
            ResetEdgeStatus(env, 1 /* cleanup*/);
            return JNI_FALSE;
    }
    status.report_edge_status = report_edge_status;
    pthread_mutex_init(&status.mutex, NULL);
    int ret = pthread_create(&status.tid, NULL, EdgeRoutine, NULL);
    if (ret != 0) {
        ResetEdgeStatus(env, 1 /* cleanup*/);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_wang_switchy_hin2n_service_N2NService_stopEdge(
        JNIEnv *env,
        jobject this) {

#ifndef NDEBUG
    __android_log_write(ANDROID_LOG_DEBUG, "edge_jni", "in stop");
#endif /* #ifndef NDEBUG */
    ResetEdgeStatus(env, 0 /* not cleanup*/);
}

JNIEXPORT jobject JNICALL Java_wang_switchy_hin2n_service_N2NService_getEdgeStatus(
        JNIEnv *env,
        jobject this) {
    const char *running_status = "DISCONNECT";
    if (status.tid != -1) {
        if (!pthread_kill(status.tid, 0)) {
            pthread_mutex_lock(&status.mutex);
            switch (status.running_status) {
                case EDGE_STAT_CONNECTING:
                    running_status = "CONNECTING";
                    break;
                case EDGE_STAT_CONNECTED:
                    running_status = "CONNECTED";
                    break;
                case EDGE_STAT_SUPERNODE_DISCONNECT:
                    running_status = "SUPERNODE_DISCONNECT";
                    break;
                case EDGE_STAT_DISCONNECT:
                    running_status = "DISCONNECT";
                    break;
                case EDGE_STAT_FAILED:
                    running_status = "FAILED";
                    break;
                default:
                    running_status = "DISCONNECT";
            }
            pthread_mutex_unlock(&status.mutex);
        }
    }

    jclass cls = (*env)->FindClass(env, "wang/switchy/hin2n/model/EdgeStatus");
    jobject jStatus = (*env)->NewObject(env, cls, (*env)->GetMethodID(env, cls, "<init>", "()V"));
    if (!jStatus) {
        return NULL;
    }
    jclass cls_rs = (*env)->FindClass(env, "wang/switchy/hin2n/model/EdgeStatus$RunningStatus");
    jobject jRunningStatus = (*env)->GetStaticObjectField(env, cls_rs,
                                                          (*env)->GetStaticFieldID(env, cls_rs,
                                                                                   running_status,
                                                                                   "Lwang/switchy/hin2n/model/EdgeStatus$RunningStatus;"));
    (*env)->SetObjectField(env, jStatus, (*env)->GetFieldID(env, cls, "runningStatus",
                                                            "Lwang/switchy/hin2n/model/EdgeStatus$RunningStatus;"),
                           jRunningStatus);

    return jStatus;
}

/////////////////////////////////////////////////////////////////////////
#ifndef JNI_CHECKNULL
#define JNI_CHECKNULL(p)            do { if (!(p)) return 1;}while(0)
#endif /* JNI_CHECKNULL */

int GetEdgeCmd(JNIEnv *env, jobject jcmd, n2n_edge_cmd_t *cmd) {
    jclass cls;
    int i, j;

    cls = (*env)->GetObjectClass(env, jcmd);
    JNI_CHECKNULL(cls);

    // edgeType
    {
        jint jiEdgeType = (*env)->GetIntField(env, jcmd,
                                              (*env)->GetFieldID(env, cls, "edgeType", "I"));
        if (jiEdgeType < EDGE_TYPE_V1 || jiEdgeType > EDGE_TYPE_V3) {
            return 1;
        }
        status.edge_type = jiEdgeType;

#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "edgeType = %d", status.edge_type);
#endif /* #ifndef NDEBUG */
    }

    {
        jint jiEdgeType = (*env)->GetIntField(env, jcmd,
                                              (*env)->GetFieldID(env, cls, "ipMode", "I"));
        if (jiEdgeType < 0 || jiEdgeType > 1) {
            return 1;
        }
        status.cmd.ip_mode = jiEdgeType;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "ipMode = %d", status.cmd.ip_mode);
#endif /* #ifndef NDEBUG */
    }
 
    // ipAddr
    {
        jstring jsIpAddr = (*env)->GetObjectField(env, jcmd, (*env)->GetFieldID(env, cls, "ipAddr",
                                                                                "Ljava/lang/String;"));
        JNI_CHECKNULL(jsIpAddr);
        const char *ipAddr = (*env)->GetStringUTFChars(env, jsIpAddr, NULL);
        if (!ipAddr || strlen(ipAddr) == 0) {
            (*env)->ReleaseStringUTFChars(env, jsIpAddr, ipAddr);
            return 1;
        }
        strncpy(cmd->ip_addr, ipAddr, EDGE_CMD_IPSTR_SIZE);
        (*env)->ReleaseStringUTFChars(env, jsIpAddr, ipAddr);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "ipAddr = %s", cmd->ip_addr);
#endif /* #ifndef NDEBUG */
    }
    // ipNetmask
    {
        jstring jsIpNetmask = (*env)->GetObjectField(env, jcmd,
                                                     (*env)->GetFieldID(env, cls, "ipNetmask",
                                                                        "Ljava/lang/String;"));
        JNI_CHECKNULL(jsIpNetmask);
        const char *ipNetmask = (*env)->GetStringUTFChars(env, jsIpNetmask, NULL);
        if (!ipNetmask || strlen(ipNetmask) == 0) {
            (*env)->ReleaseStringUTFChars(env, jsIpNetmask, ipNetmask);
            return 1;
        }
        strncpy(cmd->ip_netmask, ipNetmask, EDGE_CMD_IPSTR_SIZE);
        (*env)->ReleaseStringUTFChars(env, jsIpNetmask, ipNetmask);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "ipNetmask = %s", cmd->ip_netmask);
#endif /* #ifndef NDEBUG */
    }
    // supernodes
    {
        jarray jaSupernodes = (*env)->GetObjectField(env, jcmd,
                                                     (*env)->GetFieldID(env, cls, "supernodes",
                                                                        "[Ljava/lang/String;"));
        JNI_CHECKNULL(jaSupernodes);
        int len = (*env)->GetArrayLength(env, jaSupernodes);
        if (len <= 0) {
            return 1;
        }
        for (i = 0, j = 0; i < len && i < EDGE_CMD_SUPERNODES_NUM; ++i) {
            const jobject jsNode = (*env)->GetObjectArrayElement(env, jaSupernodes, i);
            if (!jsNode) {
                continue;
            }
            const char *node = (*env)->GetStringUTFChars(env, jsNode, NULL);
            if (!node || strlen(node) == 0) {
                (*env)->ReleaseStringUTFChars(env, jsNode, node);
                continue;
            }
            strncpy(cmd->supernodes[j], node, EDGE_CMD_SN_HOST_SIZE);
            (*env)->ReleaseStringUTFChars(env, jsNode, node);
#ifndef NDEBUG
            __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "supernodes = %s",
                                cmd->supernodes[j]);
#endif /* #ifndef NDEBUG */
            j++;
        }
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "j = %d", j);
#endif /* #ifndef NDEBUG */
        if (j == 0) {
            return 1;
        }
    }
    // community
    {
        jstring jsCommunity = (*env)->GetObjectField(env, jcmd,
                                                     (*env)->GetFieldID(env, cls, "community",
                                                                        "Ljava/lang/String;"));
        JNI_CHECKNULL(jsCommunity);
        const char *community = (*env)->GetStringUTFChars(env, jsCommunity, NULL);
        if (!community || strlen(community) == 0) {
            (*env)->ReleaseStringUTFChars(env, jsCommunity, community);
            return 1;
        }
        strncpy(cmd->community, community, EDGE_CMD_COMMUNITY_SIZE);
        (*env)->ReleaseStringUTFChars(env, jsCommunity, community);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "community = %s", cmd->community);
#endif /* #ifndef NDEBUG */
    }
    // encKey
    {
        jstring jsEncKey = (*env)->GetObjectField(env, jcmd, (*env)->GetFieldID(env, cls, "encKey",
                                                                                "Ljava/lang/String;"));
        if (jsEncKey) {
            const char *encKey = (*env)->GetStringUTFChars(env, jsEncKey, NULL);
            if (encKey && strlen(encKey) != 0) {
                cmd->enc_key = strdup(encKey);
            }
            (*env)->ReleaseStringUTFChars(env, jsEncKey, encKey);
#ifndef NDEBUG
            __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "encKey = %s", cmd->enc_key);
#endif /* #ifndef NDEBUG */
        }
    }
    // encKeyFile
    if (EDGE_TYPE_V2 <= status.edge_type && status.edge_type <= EDGE_TYPE_V3) {
        jstring jsEncKeyFile = (*env)->GetObjectField(env, jcmd,
                                                      (*env)->GetFieldID(env, cls, "encKeyFile",
                                                                         "Ljava/lang/String;"));
        if (jsEncKeyFile) {
            const char *encKeyFile = (*env)->GetStringUTFChars(env, jsEncKeyFile, NULL);
            if (encKeyFile && strlen(encKeyFile) != 0) {
                cmd->enc_key_file = strdup(encKeyFile);
            }
            (*env)->ReleaseStringUTFChars(env, jsEncKeyFile, encKeyFile);
#ifndef NDEBUG
            __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "encKeyFile = %s",
                                cmd->enc_key_file);
#endif /* #ifndef NDEBUG */
        }
    }
    // macAddr
    {
        jstring jsMacAddr = (*env)->GetObjectField(env, jcmd,
                                                   (*env)->GetFieldID(env, cls, "macAddr",
                                                                      "Ljava/lang/String;"));
        JNI_CHECKNULL(jsMacAddr);
        const char *macAddr = (*env)->GetStringUTFChars(env, jsMacAddr, NULL);
        if (macAddr && strlen(macAddr) != 0) {
            strncpy(cmd->mac_addr, macAddr, EDGE_CMD_MACNAMSIZ);
        }
        (*env)->ReleaseStringUTFChars(env, jsMacAddr, macAddr);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "macAddr = %s", cmd->mac_addr);
#endif /* #ifndef NDEBUG */
    }
    // mtu
    {
        jint jiMtu = (*env)->GetIntField(env, jcmd, (*env)->GetFieldID(env, cls, "mtu", "I"));
        if (jiMtu <= 0) {
            return 1;
        }
        cmd->mtu = jiMtu;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "mtu = %d", cmd->mtu);
#endif /* #ifndef NDEBUG */
    }
    // localIP
    if (status.edge_type == EDGE_TYPE_V2S) {
        jstring jsLocalIP = (*env)->GetObjectField(env, jcmd,
                                                   (*env)->GetFieldID(env, cls, "localIP",
                                                                      "Ljava/lang/String;"));
        JNI_CHECKNULL(jsLocalIP);
        const char *localIP = (*env)->GetStringUTFChars(env, jsLocalIP, NULL);
        if (localIP && strlen(localIP) != 0) {
            strncpy(cmd->local_ip, localIP, EDGE_CMD_IPSTR_SIZE);
        }
        (*env)->ReleaseStringUTFChars(env, jsLocalIP, localIP);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "localIP = %s", cmd->local_ip);
#endif /* #ifndef NDEBUG */
    }
    // holePunchInterval
    if (status.edge_type == EDGE_TYPE_V2S) {
        jint jiHolePunchInterval = (*env)->GetIntField(env, jcmd, (*env)->GetFieldID(env, cls,
                                                                                     "holePunchInterval",
                                                                                     "I"));
        if (jiHolePunchInterval <= 0) {
            return 1;
        }
        cmd->holepunch_interval = jiHolePunchInterval;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "holePunchInterval = %d",
                            cmd->holepunch_interval);
#endif /* #ifndef NDEBUG */
    }
    // reResoveSupernodeIP
    {
        jboolean jbReResoveSupernodeIP = (*env)->GetBooleanField(env, jcmd,
                                                                 (*env)->GetFieldID(env, cls,
                                                                                    "reResoveSupernodeIP",
                                                                                    "Z"));
        cmd->re_resolve_supernode_ip = jbReResoveSupernodeIP ? 1 : 0;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "reResoveSupernodeIP = %d",
                            cmd->re_resolve_supernode_ip);
#endif /* #ifndef NDEBUG */
    }
    // localPort
    {
        jint jiLocalPort = (*env)->GetIntField(env, jcmd,
                                               (*env)->GetFieldID(env, cls, "localPort", "I"));
        if (jiLocalPort < 0) {
            return 1;
        }
        cmd->local_port = jiLocalPort;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "localPort = %d", cmd->local_port);
#endif /* #ifndef NDEBUG */
    }
    // allowRouting
    {
        jboolean jbAllowRouting = (*env)->GetBooleanField(env, jcmd, (*env)->GetFieldID(env, cls,
                                                                                        "allowRouting",
                                                                                        "Z"));
        cmd->allow_routing = jbAllowRouting ? 1 : 0;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "allowRouting = %d", cmd->allow_routing);
#endif /* #ifndef NDEBUG */
    }
    // dropMuticast
    if (status.edge_type == EDGE_TYPE_V2 || status.edge_type == EDGE_TYPE_V2S) {
        jboolean jbDropMuticast = (*env)->GetBooleanField(env, jcmd, (*env)->GetFieldID(env, cls,
                                                                                        "dropMuticast",
                                                                                        "Z"));
        cmd->drop_multicast = jbDropMuticast ? 1 : 0;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "dropMuticast = %d",
                            cmd->drop_multicast);
#endif /* #ifndef NDEBUG */
    }
    // gatewayIp
    {
        jstring jbGatewayIp = (*env)->GetObjectField(env, jcmd, (*env)->GetFieldID(env, cls, "gatewayIp",
                                                                                "Ljava/lang/String;"));
        JNI_CHECKNULL(jbGatewayIp);
        const char *ipAddr = (*env)->GetStringUTFChars(env, jbGatewayIp, NULL);
        if (!ipAddr) {
            (*env)->ReleaseStringUTFChars(env, jbGatewayIp, ipAddr);
            return 1;
        }
        strncpy(cmd->gateway_ip, ipAddr, EDGE_CMD_IPSTR_SIZE);
        (*env)->ReleaseStringUTFChars(env, jbGatewayIp, ipAddr);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "gatewayIp = %s", cmd->gateway_ip);
#endif /* #ifndef NDEBUG */
    }
    // encryptionMode
    {
        jstring jEncryptionMode = (*env)->GetObjectField(env, jcmd, (*env)->GetFieldID(env, cls, "encryptionMode",
                                                                                      "Ljava/lang/String;"));
        JNI_CHECKNULL(jEncryptionMode);
        const char *encMode = (*env)->GetStringUTFChars(env, jEncryptionMode, NULL);
        if (!encMode) {
            (*env)->ReleaseStringUTFChars(env, jEncryptionMode, encMode);
            return 1;
        }
        strncpy(cmd->encryption_mode, encMode, EDGE_CMD_ENCRYPTION_MODE_SIZE);
        (*env)->ReleaseStringUTFChars(env, jEncryptionMode, encMode);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "encryptionMode = %s", cmd->encryption_mode);
#endif /* #ifndef NDEBUG */
    }
    // httpTunnel
    if (status.edge_type == EDGE_TYPE_V1) {
        jboolean jbHttpTunnel = (*env)->GetBooleanField(env, jcmd,
                                                        (*env)->GetFieldID(env, cls, "httpTunnel",
                                                                           "Z"));
        cmd->http_tunnel = jbHttpTunnel ? 1 : 0;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "httpTunnel = %d", cmd->http_tunnel);
#endif /* #ifndef NDEBUG */
    }
    // traceLevel
    {
        jint jiTraceLevel = (*env)->GetIntField(env, jcmd,
                                                (*env)->GetFieldID(env, cls, "traceLevel", "I"));
        cmd->trace_vlevel = jiTraceLevel;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "traceLevel = %d", cmd->trace_vlevel);
#endif /* #ifndef NDEBUG */
    }
    // vpnFd
    {
        jint jiVpnFd = (*env)->GetIntField(env, jcmd, (*env)->GetFieldID(env, cls, "vpnFd", "I"));
#if 0
        if (jiVpnFd < 0) {
            return 1;
        }
#endif
        cmd->vpn_fd = jiVpnFd;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "vpnFd = %d", cmd->vpn_fd);
#endif /* #ifndef NDEBUG */
    }
    // logPath
    jstring jsLogPath = (*env)->GetObjectField(env, jcmd, (*env)->GetFieldID(env, cls, "logPath",
                                                                             "Ljava/lang/String;"));
    JNI_CHECKNULL(jsLogPath);
    const char *logPath = (*env)->GetStringUTFChars(env, jsLogPath, NULL);
    if (logPath && strlen(logPath) != 0) {
        cmd->logpath = strdup(logPath);
    }
    (*env)->ReleaseStringUTFChars(env, jsLogPath, logPath);
#ifndef NDEBUG
    __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "logPath = %s", cmd->logpath);
#endif /* #ifndef NDEBUG */
    // devDesc
    {
        jstring jsDevDesc = (*env)->GetObjectField(env, jcmd, (*env)->GetFieldID(env, cls, "devDesc",
                                                                                 "Ljava/lang/String;"));
        JNI_CHECKNULL(jsDevDesc);
        const char *devDesc = (*env)->GetStringUTFChars(env, jsDevDesc, NULL);
        if (devDesc && strlen(devDesc) != 0) {
            cmd->devDesc = strdup(devDesc);
        }
        (*env)->ReleaseStringUTFChars(env, jsDevDesc, devDesc);
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "devDesc = %s", cmd->devDesc);
#endif /* #ifndef NDEBUG */
    }
    // headerEnc
    {
        jboolean jbHeaderEnc = (*env)->GetBooleanField(env, jcmd, (*env)->GetFieldID(env, cls,
                                                                                        "headerEnc",
                                                                                        "Z"));
        cmd->header_encryption = jbHeaderEnc ? 2 : 0;
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni", "headerEnc = %d", cmd->header_encryption);
#endif /* #ifndef NDEBUG */
    }

    return 0;
}

void InitEdgeStatus(void) {
    memset(&status.cmd, 0, sizeof(status.cmd));
    status.cmd.enc_key = NULL;
    status.cmd.enc_key_file = NULL;
    status.cmd.mtu = 1400;
    status.cmd.holepunch_interval = EDGE_CMD_HOLEPUNCH_INTERVAL;
    status.cmd.re_resolve_supernode_ip = 0;
    status.cmd.local_port = 0;
    status.cmd.allow_routing = 0;
    status.cmd.drop_multicast = 1;
    status.cmd.http_tunnel = 0;
    status.cmd.trace_vlevel = 1;
    status.cmd.vpn_fd = -1;
    status.cmd.logpath = NULL;
    status.cmd.header_encryption = 0;

    status.tid = -1;
    status.jvm = NULL;
    status.jobj_service = NULL;
    status.jcls_status = NULL;
    status.jcls_rs = NULL;
    status.start_edge = NULL;
    status.stop_edge = NULL;
    status.report_edge_status = NULL;

    status.edge_type = EDGE_TYPE_NONE;
    status.running_status = EDGE_STAT_DISCONNECT;
}

void ResetEdgeStatus(JNIEnv *env, uint8_t cleanup) {
    static u_int8_t once = 0;
    static pthread_mutex_t mut = PTHREAD_MUTEX_INITIALIZER;

    if (!once) {
        InitEdgeStatus();
        once = 1;
        return;
    }

    pthread_mutex_lock(&mut);
    if (status.tid != -1 || cleanup) {
#ifndef NDEBUG
        __android_log_print(ANDROID_LOG_DEBUG, "edge_jni",
                            "ResetEdgeStatus tid = %ld, cleanup = %d", status.tid, cleanup);
#endif /* #ifndef NDEBUG */
        if (status.stop_edge) {
            status.stop_edge();
        }
        if (status.tid != -1) {
            pthread_join(status.tid, NULL);
        }
        pthread_mutex_lock(&status.mutex);
        if (env) {
            if (status.jcls_rs) {
                (*env)->DeleteGlobalRef(env, status.jcls_rs);
            }
            if (status.jcls_status) {
                (*env)->DeleteGlobalRef(env, status.jcls_status);
            }
            if (status.jobj_service) {
                (*env)->DeleteGlobalRef(env, status.jobj_service);
            }
        }
        if (status.cmd.enc_key_file) {
            free(status.cmd.enc_key_file);
        }
        if (status.cmd.enc_key) {
            free(status.cmd.enc_key);
        }
        if (status.cmd.logpath) {
            free(status.cmd.logpath);
        }
        InitEdgeStatus();
        pthread_mutex_unlock(&status.mutex);
        pthread_mutex_destroy(&status.mutex);
    }
    pthread_mutex_unlock(&mut);
}

void *EdgeRoutine(void *ignore) {
    int flag = 0;
    JNIEnv *env = NULL;

    if (status.jvm) {
        if ((*status.jvm)->AttachCurrentThread(status.jvm, &env, NULL) == JNI_OK) {
            flag = 1;
        }
    }

    if (!status.start_edge) {
        return NULL;
    }

    int ret = status.start_edge(&status);
    if (ret) {
        pthread_mutex_lock(&status.mutex);
        status.running_status = EDGE_STAT_FAILED;
        pthread_mutex_unlock(&status.mutex);
        report_edge_status();
    }

    if (flag && status.jvm) {
        (*status.jvm)->DetachCurrentThread(status.jvm);
    }

    return NULL;
}

void report_edge_status(void) {
    if (!status.jvm || !status.jobj_service || !status.jcls_status || !status.jcls_rs ||
        status.tid == -1) {
        return;
    }

    const char *running_status = "DISCONNECT";
    pthread_mutex_lock(&status.mutex);
    switch (status.running_status) {
        case EDGE_STAT_CONNECTING:
            running_status = "CONNECTING";
            break;
        case EDGE_STAT_CONNECTED:
            running_status = "CONNECTED";
            break;
        case EDGE_STAT_SUPERNODE_DISCONNECT:
            running_status = "SUPERNODE_DISCONNECT";
            break;
        case EDGE_STAT_DISCONNECT:
            running_status = "DISCONNECT";
            break;
        case EDGE_STAT_FAILED:
            running_status = "FAILED";
            break;
        default:
            running_status = "DISCONNECT";
    }
    pthread_mutex_unlock(&status.mutex);

    JNIEnv *env = NULL;
    if ((*status.jvm)->GetEnv(status.jvm, (void **)&env, JNI_VERSION_1_1) != JNI_OK || !env) {
        return;
    }

    jmethodID mid = (*env)->GetMethodID(env, status.jcls_status, "<init>", "()V");
    if (!mid) {
        return;
    }
    jobject jStatus = (*env)->NewObject(env, status.jcls_status, mid);
    if (!jStatus) {
        return;
    }

    jfieldID fid = (*env)->GetStaticFieldID(env, status.jcls_rs, running_status,
                                            "Lwang/switchy/hin2n/model/EdgeStatus$RunningStatus;");
    if (!fid) {
        (*env)->DeleteLocalRef(env, jStatus);
        return;
    }
    jobject jRunningStatus = (*env)->GetStaticObjectField(env, status.jcls_rs, fid);
    if (!jRunningStatus) {
        (*env)->DeleteLocalRef(env, jRunningStatus);
        (*env)->DeleteLocalRef(env, jStatus);
        return;
    }
    fid = (*env)->GetFieldID(env, status.jcls_status, "runningStatus",
                             "Lwang/switchy/hin2n/model/EdgeStatus$RunningStatus;");
    if (!fid) {
        (*env)->DeleteLocalRef(env, jRunningStatus);
        (*env)->DeleteLocalRef(env, jStatus);
        return;
    }
    (*env)->SetObjectField(env, jStatus, fid, jRunningStatus);


    jclass cls = (*env)->GetObjectClass(env, status.jobj_service);
    if (!cls) {
        (*env)->DeleteLocalRef(env, jRunningStatus);
        (*env)->DeleteLocalRef(env, jStatus);
        return;
    }
    mid = (*env)->GetMethodID(env, cls, "reportEdgeStatus",
                              "(Lwang/switchy/hin2n/model/EdgeStatus;)V");
    if (!mid) {
        (*env)->DeleteLocalRef(env, jRunningStatus);
        (*env)->DeleteLocalRef(env, jStatus);
        return;
    }
    (*env)->CallVoidMethod(env, status.jobj_service, mid, jStatus);

    (*env)->DeleteLocalRef(env, jRunningStatus);
    (*env)->DeleteLocalRef(env, jStatus);
}
