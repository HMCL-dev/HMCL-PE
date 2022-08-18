#ifndef BOAT_INTERNAL_H
#define BOAT_INTERNAL_H

#include <stdlib.h>
#include <pthread.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <errno.h>

#include <boat.h>
#include <boat_keycodes.h>

typedef struct _QueueElement {
    struct _QueueElement* next;
    BoatEvent event;
} QueueElement;

typedef struct {
    int count;
    int capacity;
    QueueElement* head;
    QueueElement* tail;
} EventQueue;

typedef struct {
    JavaVM* android_jvm;
    jclass class_BoatLib;
    jclass class_BoatActivity;
    jobject boatActivity;
    jmethodID setCursorMode;
    ANativeWindow* window;
    char* clipboard_string;
    EventQueue event_queue;
    pthread_mutex_t event_queue_mutex;
    int has_event_pipe;
    int event_pipe_fd[2];
    int epoll_fd;
} BoatInternal;

extern BoatInternal mBoat;

#define BOAT_INTERNAL_LOG(x...) do { \
    fprintf(stderr, "[Boat Internal] %s:%d\n", __FILE__, __LINE__); \
    fprintf(stderr, x); \
    fprintf(stderr, "\n"); \
    fflush(stderr); \
    } while (0)

#define PrepareBoatLibJNI() \
    JavaVM* vm = mBoat.android_jvm; \
    JNIEnv* env = NULL; \
    jint attached = (*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_2); \
    if (attached == JNI_EDETACHED) { \
        attached = (*vm)->AttachCurrentThread(vm, &env, NULL); \
        if (attached != JNI_OK || env == NULL) { \
            BOAT_INTERNAL_LOG("Failed to attach thread to Android JavaVM."); \
        } \
    } \
    do {} while(0)

#define CallBoatLibJNIFunc(return_exp, func_type, func_name, signature, args...) \
    jmethodID BoatLib_##func_name = (*env)->GetStaticMethodID(env, mBoat.class_BoatLib, #func_name, signature); \
    if (BoatLib_##func_name == NULL) { \
        BOAT_INTERNAL_LOG("Failed to find static method BoatLib_"#func_name ); \
    } \
    return_exp (*env)->CallStatic##func_type##Method(env, mBoat.class_BoatLib, BoatLib_##func_name, ##args); \
    do {} while(0)

#endif // BOAT_INTERNAL_H
