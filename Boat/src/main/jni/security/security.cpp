//
// Created by mio on 2022/6/9.
//
#include <jni.h>
#include <cstdlib>
#include <android/log.h>
#include <string>
#include <unistd.h>
#include <iostream>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FUNCTION__, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, __FUNCTION__,__VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, __FUNCTION__, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, __FUNCTION__,__VA_ARGS__))
#define RELEASE(...) (env->ReleaseStringUTFChars(__VA_ARGS__))

extern "C" {
using namespace std;
string dir;
string getSecretKey(const char *oaid);


bool isPublic = true;
bool isCracked = false;

bool isInit = false;


JNIEXPORT void JNICALL
Java_com_tungsten_hmclpe_launcher_MainActivity_securityInit(JNIEnv *env, jobject obj_context) {
    // TODO: implement securityInit()
    isInit = true;

    jclass mainClass = env->FindClass("com/tungsten/hmclpe/launcher/MainActivity");
    jmethodID initMethod = env->GetMethodID(mainClass, "init", "()V");
    env->CallVoidMethod(obj_context, initMethod);

    jclass cls = env->GetObjectClass(obj_context);
    jmethodID method_a = env->GetMethodID(cls, "startVerify", "()V");
    jmethodID method_getDataDir = env->GetMethodID(cls, "getDataDir", "()Ljava/io/File;");
    jclass File = env->FindClass("java/io/File");
    jmethodID method_getAbsolutePath = env->GetMethodID(File, "getAbsolutePath",
                                                        "()Ljava/lang/String;");
    jobject obj_datadir = env->CallObjectMethod(obj_context, method_getDataDir);
    jstring datadir = (jstring) env->CallObjectMethod(obj_datadir, method_getAbsolutePath);
    jmethodID method_file_init = env->GetMethodID(File, "<init>", "(Ljava/lang/String;)V");
    const char *tmp = env->GetStringUTFChars(datadir, 0);
    char result[128];
    strcpy(result, tmp);
    strcat(result, "/security");
    dir = result;
    jobject obj_newFile = env->NewObject(File, method_file_init, env->NewStringUTF(result));
    jmethodID method_mkdirs = env->GetMethodID(File, "mkdirs", "()Z");
    env->CallBooleanMethod(obj_newFile, method_mkdirs);
    if (!isPublic && !isCracked) {
        env->CallVoidMethod(obj_context, method_a);
    }
    env->ReleaseStringUTFChars(datadir, tmp);
}


JNIEXPORT jboolean JNICALL
Java_com_tungsten_hmclpe_launcher_MainActivity_isValid(JNIEnv *env, jobject obj_context,
                                                       jstring str) {
    // TODO: implement isValid()
    const char *pw = env->GetStringUTFChars(str, 0);

    jclass DigestUtils = env->FindClass("com/tungsten/hmclpe/utils/DigestUtils");
    jclass DeviceIdentifier = env->FindClass("com/github/gzuliyujiang/oaid/DeviceIdentifier");
    jmethodID getOAID = env->GetStaticMethodID(DeviceIdentifier, "getOAID",
                                               "(Landroid/content/Context;)Ljava/lang/String;");
    jmethodID getAndroidID = env->GetStaticMethodID(DeviceIdentifier, "getAndroidID",
                                                    "(Landroid/content/Context;)Ljava/lang/String;");
    jmethodID getWidevineID = env->GetStaticMethodID(DeviceIdentifier, "getWidevineID",
                                                     "()Ljava/lang/String;");
    jmethodID getPseudoID = env->GetStaticMethodID(DeviceIdentifier, "getPseudoID",
                                                   "()Ljava/lang/String;");
    jstring joaid = (jstring) env->CallStaticObjectMethod(DeviceIdentifier, getOAID, obj_context);
    jstring jAndroidID = (jstring) env->CallStaticObjectMethod(DeviceIdentifier, getAndroidID,
                                                               obj_context);
    jstring jWidevineID = (jstring) env->CallStaticObjectMethod(DeviceIdentifier, getWidevineID);
    jstring jPseudoID = (jstring) env->CallStaticObjectMethod(DeviceIdentifier, getPseudoID);
    const char *OAID = env->GetStringUTFChars(joaid, 0);
    const char *AndroidID = env->GetStringUTFChars(jAndroidID, 0);
    const char *WidevineID = env->GetStringUTFChars(jWidevineID, 0);
    const char *PseudoID = env->GetStringUTFChars(jPseudoID, 0);
    string result;
    result = OAID;
    result += AndroidID;
    result += WidevineID;
    result += PseudoID;
//    char *aaa= const_cast<char *>(result.c_str());
//    LOGE("OAID:%s,AndroidID:%s,WidevineID:%s,PseudoID:%s,result:%s",OAID,AndroidID,WidevineID,PseudoID,aaa);
    jmethodID encryptToMD5 = env->GetStaticMethodID(DigestUtils, "encryptToMD5",
                                                    "(Ljava/lang/String;)Ljava/lang/String;");
    jstring CODE = env->NewStringUTF(result.c_str());
    jstring OAIDMd5 = (jstring) env->CallStaticObjectMethod(DigestUtils, encryptToMD5, CODE);
    const char *b = env->GetStringUTFChars(OAIDMd5, 0);
    jstring SecretKeyMd5 = (jstring) env->CallStaticObjectMethod(DigestUtils, encryptToMD5,
                                                                 env->NewStringUTF(
                                                                         getSecretKey(b).c_str()));
    const char *c = env->GetStringUTFChars(SecretKeyMd5, 0);
    env->ReleaseStringUTFChars(str, pw);
    env->ReleaseStringUTFChars(OAIDMd5, b);
    env->ReleaseStringUTFChars(SecretKeyMd5, c);
    RELEASE(joaid, OAID);
    RELEASE(jPseudoID, PseudoID);
    RELEASE(jWidevineID, WidevineID);
    RELEASE(jAndroidID, AndroidID);
    LOGE("OAIDMd5:%s", b);
    LOGE("md5:%s", c);
    if (!isInit) {
        abort();
    }
    if (isCracked || strcmp(pw, c) == 0) {
        rmdir(dir.c_str());
        return true;
    }
    return false;
}


JNIEXPORT void JNICALL
Java_com_tungsten_hmclpe_launcher_MainActivity_verify(JNIEnv *env, jclass clazz) {
    // TODO: implement verify()
    if (!isPublic && !isCracked && access(dir.c_str(), 0) == F_OK || !isInit) {
        abort();
    }
}


JNIEXPORT void JNICALL
Java_com_tungsten_hmclpe_launcher_MainActivity_verifyFunc(JNIEnv *env, jclass clazz) {
    // TODO: implement verify()
    if (!isCracked && access(dir.c_str(), 0) == F_OK || !isInit) {
        abort();
    }
}


JNIEXPORT void JNICALL
Java_com_tungsten_hmclpe_launcher_MainActivity_launch(JNIEnv *env, jobject obj_context,
                                                      jobject intent) {
    // TODO: implement verify()
    if (!isPublic && !isCracked && access(dir.c_str(), 0) == F_OK || !isInit) {
        abort();
    } else {
        jclass activity = env->FindClass("com/tungsten/hmclpe/launcher/MainActivity");
        jmethodID method_startGame = env->GetMethodID(activity, "startActivity",
                                                      "(Landroid/content/Intent;)V");
        env->CallVoidMethod(obj_context, method_startGame, intent);
    }
}


string getSecretKey(const char *oaid) {
    char result[128];
    strcpy(result, oaid);
    int i = 0;
    while (result[i] != '\0') {
        result[i] = result[i] + 1;
        result[i] = result[i] | 1;
        i++;
    }
    return {result};
}


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    vm->AttachCurrentThread(&env, NULL);
    if (env == NULL) {
        abort();
    }
    char *app_packageName = "com.tungsten.hmclpe";
    jint app_signature_hash_code_debug = 369753433;
    jint app_signature_hash_code_debug_ = 3665141352;
    jint app_signature_hash_code_release = 606150242;
    jclass class_HMCLPEApplication = env->FindClass(
            "com/tungsten/hmclpe/launcher/HMCLPEApplication");
    jmethodID method_getContext = env->GetStaticMethodID(class_HMCLPEApplication, "getContext",
                                                         "()Landroid/content/Context;");
    jobject obj_context = env->CallStaticObjectMethod(class_HMCLPEApplication, method_getContext);
    jclass class_Context = env->GetObjectClass(obj_context);
    jmethodID methodID_getPackageManager = env->GetMethodID(
            class_Context, "getPackageManager",
            "()Landroid/content/pm/PackageManager;");
    jobject packageManager = env->CallObjectMethod(obj_context, methodID_getPackageManager);
    jclass packageManager_clazz = env->GetObjectClass(packageManager);
    jmethodID methodID_getPackageInfo = env->GetMethodID(packageManager_clazz, "getPackageInfo",
                                                         "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jmethodID methodID_getPackageName = env->GetMethodID(class_Context, "getPackageName",
                                                         "()Ljava/lang/String;");
    jstring application_package = (jstring) env->CallObjectMethod(obj_context,
                                                                  methodID_getPackageName);
    const char *package_name = env->GetStringUTFChars(application_package, 0);
    jobject packageInfo = env->CallObjectMethod(packageManager, methodID_getPackageInfo,
                                                application_package, 64);
    jclass packageinfo_clazz = env->GetObjectClass(packageInfo);
    jfieldID fieldID_signatures = env->GetFieldID(packageinfo_clazz, "signatures",
                                                  "[Landroid/content/pm/Signature;");
    jobjectArray signature_arr = (jobjectArray) env->GetObjectField(packageInfo,
                                                                    fieldID_signatures);
    jobject signature = env->GetObjectArrayElement(signature_arr, 0);
    jclass signature_clazz = env->GetObjectClass(signature);
    jmethodID methodID_hashcode = env->GetMethodID(signature_clazz, "hashCode", "()I");
    jint hashCode = env->CallIntMethod(signature, methodID_hashcode);
    if (strcmp(package_name, app_packageName) != 0) {
        abort();
    }
    LOGE("hashcode:%ld", hashCode);
    if (hashCode != app_signature_hash_code_release && hashCode != app_signature_hash_code_debug &&
        hashCode != app_signature_hash_code_debug_) {
        //abort();
    }
    jmethodID method_releaseContext = env->GetStaticMethodID(class_HMCLPEApplication,
                                                             "releaseContext",
                                                             "()V");
    env->CallStaticVoidMethod(class_HMCLPEApplication, method_releaseContext);
    jmethodID method_toString = env->GetMethodID(class_Context, "toString", "()Ljava/lang/String;");
    jstring application = (jstring) env->CallObjectMethod(obj_context, method_toString);
    const char *app = env->GetStringUTFChars(application, 0);
    if (strstr(app, "HMCLPEApplication") == NULL) {
        abort();
    }
    env->ReleaseStringUTFChars(application, app);
    return JNI_VERSION_1_6;
}
JNIEXPORT void JNICALL
Java_com_tungsten_hmclpe_launcher_MainActivity_onCreate(JNIEnv *env, jobject thiz,
                                                        jobject saved_instance_state) {
    // TODO: implement onCreate()
    //super.onCreate(savedInstanceState);
    jclass MainActivity = env->GetObjectClass(thiz);
    jclass AppCompatActivity = env->GetSuperclass(MainActivity);
    jmethodID onCreate = env->GetMethodID(AppCompatActivity, "onCreate", "(Landroid/os/Bundle;)V");
    env->CallNonvirtualVoidMethod(thiz, AppCompatActivity, onCreate, saved_instance_state);

    jmethodID setContentView = env->GetMethodID(MainActivity, "setContentView", "(I)V");
    jclass Layout = env->FindClass("com/tungsten/hmclpe/R$layout");
    jfieldID activity_main = env->GetStaticFieldID(Layout, "activity_main", "I");
    jint id_activity_main = env->GetStaticIntField(Layout, activity_main);
    env->CallVoidMethod(thiz, setContentView, id_activity_main);

    jmethodID findViewById = env->GetMethodID(MainActivity, "findViewById",
                                              "(I)Landroid/view/View;");
    jclass id = env->FindClass("com/tungsten/hmclpe/R$id");
    jfieldID launcher_layout = env->GetStaticFieldID(id, "launcher_layout", "I");
    jint id_launcher_layout = env->GetStaticIntField(id, launcher_layout);
    jobject launcherLayout = env->CallObjectMethod(thiz, findViewById, id_launcher_layout);
    jfieldID id_launcherLayout = env->GetFieldID(MainActivity, "launcherLayout",
                                                 "Landroid/widget/LinearLayout;");
    env->SetObjectField(thiz, id_launcherLayout, launcherLayout);
    Java_com_tungsten_hmclpe_launcher_MainActivity_securityInit(env, thiz);
}
}