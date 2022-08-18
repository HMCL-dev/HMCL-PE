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

#endif

