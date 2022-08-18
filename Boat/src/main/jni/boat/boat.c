#include "boat_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>

BoatInternal mBoat;

ANativeWindow* boatGetNativeWindow() {
    return mBoat.window;
}

JNIEXPORT void JNICALL Java_cosine_boat_BoatActivity_setBoatNativeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    mBoat.window = ANativeWindow_fromSurface(env, surface);
    BOAT_INTERNAL_LOG("setBoatNativeWindow : %p", mBoat.window);
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
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

