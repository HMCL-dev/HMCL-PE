#include "boat_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>
#include <dlfcn.h>

BoatInternal mBoat;

ANativeWindow* boatGetNativeWindow() {
    return mBoat.window;
}

void* boat_addr;

void* (*loader_dlopen)(const char* filename, int flag, const void* caller_addr);
void* dlopen_wrapper(const char* filename, int flag) {
    BOAT_INTERNAL_LOG("Using boat to dlopen.");
    void* handle;
    handle = dlopen("libdl.so", RTLD_LAZY);
    loader_dlopen = (void* (*)(const char*, int, const void*))dlsym(handle, "__loader_dlopen");
    return loader_dlopen(filename, flag, boat_addr);
}

int dlclose_wrapper(void* handle) {
    return dlclose(handle);
}

char* dlerror_wrapper() {
    return dlerror();
}

void* dlsym_wrapper(void* handle, const char* symbol) {
    return dlsym(handle, symbol);
}

void* setup_dl_hook() {
    return __builtin_return_address(0);
}

JNIEXPORT void JNICALL Java_cosine_boat_BoatActivity_setBoatNativeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    mBoat.window = ANativeWindow_fromSurface(env, surface);
    BOAT_INTERNAL_LOG("setBoatNativeWindow : %p", mBoat.window);
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    boat_addr = setup_dl_hook();
    memset(&mBoat, 0, sizeof(mBoat));
    mBoat.android_jvm = vm;
    JNIEnv* env = 0;
    jint result = (*mBoat.android_jvm)->AttachCurrentThread(mBoat.android_jvm, &env, 0);
    if (result != JNI_OK || env == 0) {
        BOAT_INTERNAL_LOG("Failed to attach thread to JavaVM.");
        abort();
    }
    jclass class_BoatLib = (*env)->FindClass(env, "cosine/boat/BoatInput");
    if (class_BoatLib == 0) {
        BOAT_INTERNAL_LOG("Failed to find class: cosine/boat/BoatInput.");
        abort();
    }
    mBoat.class_BoatLib = (jclass)(*env)->NewGlobalRef(env, class_BoatLib);
    jclass class_BoatActivity = (*env)->FindClass(env, "cosine/boat/BoatActivity");
    if (class_BoatActivity == 0) {
        BOAT_INTERNAL_LOG("Failed to find class: cosine/boat/BoatActivity.");
        abort();
    }
    mBoat.class_BoatActivity = (jclass)(*env)->NewGlobalRef(env, class_BoatActivity);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL Java_cosine_boat_BoatActivity_nOnCreate(JNIEnv *env, jobject thiz) {

    // Get the BoatActivity class
    jclass class_BoatActivity = (*env)->GetObjectClass(env, thiz);
    mBoat.class_BoatActivity = (*env)->NewGlobalRef(env, class_BoatActivity);

    // Get the setCursorMode function from the BoatActivity class
    mBoat.setCursorMode = (*env)->GetMethodID(env,mBoat.class_BoatActivity, "setCursorMode","(I)V");

    mBoat.boatActivity = (*env)->NewGlobalRef(env, thiz);
}

