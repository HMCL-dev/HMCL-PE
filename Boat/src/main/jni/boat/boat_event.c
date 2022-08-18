#include <android/log.h>
#include "boat_internal.h"

void EventQueue_init(EventQueue* queue) {
    queue->count = 0;
    queue->head = NULL;
    queue->tail = NULL;
}

BoatEvent* EventQueue_add(EventQueue* queue) {
    BoatEvent* ret = NULL;
    QueueElement* e = malloc(sizeof(QueueElement));
    if (e != NULL) {
        e->next = NULL;
        if (queue->count > 0) {
            queue->tail->next = e;
            queue->tail = e;
        }
        else { // count == 0
            queue->head = e;
            queue->tail = e;
        }
        queue->count++;
        ret = &queue->tail->event;
    }
    return ret;
}

int EventQueue_take(EventQueue* queue, BoatEvent* event) {
    int ret = 0;
    if (queue->count > 0) {
        QueueElement* e = queue->head;
        if (queue->count == 1) {
            queue->head = NULL;
            queue->tail = NULL;
        }
        else {
            queue->head = e->next;
        }
        queue->count--;
        ret = 1;
        if (event != NULL) {
            memcpy(event, &e->event, sizeof(BoatEvent));
        }
        free(e);
    }
    return ret;
}

void EventQueue_clear(EventQueue* queue) {
    while (queue->count > 0) {
        EventQueue_take(queue, NULL);
    }
}

void boatSetCursorMode(int mode) {
    if (mBoat.android_jvm == 0){
        return;
    }
    JNIEnv* env = 0;

    jint result = (*mBoat.android_jvm)->AttachCurrentThread(mBoat.android_jvm, &env, 0);

    if (result != JNI_OK || env == 0){
        //__android_log_print(ANDROID_LOG_ERROR, "Boat", "Failed to attach thread to JavaVM.");
        abort();
    }

    jclass class_BoatActivity = mBoat.class_BoatActivity;

    if (class_BoatActivity == 0){
        //__android_log_print(ANDROID_LOG_ERROR, "Boat", "Failed to find class: cosine/boat/BoatActivity.");
        abort();
    }

    jmethodID BoatActivity_setCursorMode = mBoat.setCursorMode;

    if (BoatActivity_setCursorMode == 0){
        //__android_log_print(ANDROID_LOG_ERROR, "Boat", "Failed to find method BoatActivity::setCursorMode");
        abort();
    }
    (*env)->CallVoidMethod(env, mBoat.boatActivity, BoatActivity_setCursorMode, mode);


    (*mBoat.android_jvm)->DetachCurrentThread(mBoat.android_jvm);
}

int boatGetEventFd() {
    if (!mBoat.has_event_pipe) {
        return -1;
    }
    return mBoat.event_pipe_fd[0];
}

int boatWaitForEvent(int timeout) {
    if (!mBoat.has_event_pipe) {
        return 0;
    }
    struct epoll_event ev;
    int ret = epoll_wait(mBoat.epoll_fd, &ev, 1, timeout);
    if (ret > 0 && (ev.events & EPOLLIN)) {
        return 1;
    }
    return 0;
}

int boatPollEvent(BoatEvent* event) {
    if (!mBoat.has_event_pipe) {
        return 0;
    }
    if (pthread_mutex_lock(&mBoat.event_queue_mutex)) {
        BOAT_INTERNAL_LOG("Failed to acquire mutex");
        return 0;
    }
    char c;
    int ret = 0;
    if (read(mBoat.event_pipe_fd[0], &c, 1) > 0) {
        ret = EventQueue_take(&mBoat.event_queue, event);
    }
    if (pthread_mutex_unlock(&mBoat.event_queue_mutex)) {
        BOAT_INTERNAL_LOG("Failed to release mutex");
        return 0;
    }
    return ret;
}

JNIEXPORT void JNICALL Java_cosine_boat_BoatInput_pushEvent(JNIEnv* env, jclass clazz, jlong time, jint type, jint p1, jint p2) {
    if (!mBoat.has_event_pipe) {
        return;
    }
    if (pthread_mutex_lock(&mBoat.event_queue_mutex)) {
        BOAT_INTERNAL_LOG("Failed to acquire mutex");
        return;
    }
    BoatEvent* event = EventQueue_add(&mBoat.event_queue);
    if (event == NULL) {
        BOAT_INTERNAL_LOG("Failed to add event to event queue");
        return;
    }
    event->time = time;
    event->type = type;
    event->state = 0;
    switch (type) {
        case MotionNotify:
            event->x = p1;
            event->y = p2;
            break;
        case ButtonPress:
        case ButtonRelease:
            event->button = p1;
            break;
        case KeyPress:
        case KeyRelease:
            event->keycode = p1;
            event->keychar = p2;
            break;
        case ConfigureNotify:
            event->width = p1;
            event->height = p2;
            break;
        case BoatMessage:
            event->message = p1;
            break;
    }
    write(mBoat.event_pipe_fd[1], "E", 1);
    if (pthread_mutex_unlock(&mBoat.event_queue_mutex)) {
        BOAT_INTERNAL_LOG("Failed to release mutex");
    }
}

JNIEXPORT void JNICALL Java_cosine_boat_BoatInput_setEventPipe(JNIEnv* env, jclass clazz) {
    if (pipe(mBoat.event_pipe_fd) == -1) {
        BOAT_INTERNAL_LOG("Failed to create event pipe : %s", strerror(errno));
        return;
    }
    mBoat.epoll_fd = epoll_create(3);
    if (mBoat.epoll_fd == -1) {
        BOAT_INTERNAL_LOG("Failed to get epoll fd : %s", strerror(errno));
        return;
    }
    struct epoll_event ev;
    ev.events = EPOLLIN;
    ev.data.fd = mBoat.event_pipe_fd[0];
    if (epoll_ctl(mBoat.epoll_fd, EPOLL_CTL_ADD, mBoat.event_pipe_fd[0], &ev) == -1) {
        BOAT_INTERNAL_LOG("Failed to add epoll event : %s", strerror(errno));
        return;
    }
    EventQueue_init(&mBoat.event_queue);
    pthread_mutex_init(&mBoat.event_queue_mutex, NULL);
    mBoat.has_event_pipe = 1;
    BOAT_INTERNAL_LOG("Succeeded to set event pipe");
}
