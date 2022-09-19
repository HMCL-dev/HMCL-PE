#ifndef BOAT_H
#define BOAT_H

#include <android/native_window.h>
#include <boat_event.h>

ANativeWindow* boatGetNativeWindow(void);
int boatWaitForEvent(int timeout);
int boatPollEvent(BoatEvent* event);
int boatGetEventFd(void);
void boatSetCursorMode(int mode);
void boatSetPrimaryClipString(const char* string);
const char* boatGetPrimaryClipString(void);

void* dlopen_wrapper(const char* __filename, int __flag);
int dlclose_wrapper(void* __handle);
char* dlerror_wrapper(void);
void* dlsym_wrapper(void* __handle, const char* __symbol);

#endif

