#include "boat_internal.h"

void boatSetPrimaryClipString(const char* string) {
    PrepareBoatLibJNI();
    CallBoatLibJNIFunc( , Void, setPrimaryClipString, "(Ljava/lang/String;)V", (*env)->NewStringUTF(env, string));
}

const char* boatGetPrimaryClipString() {
    PrepareBoatLibJNI();
    if (mBoat.clipboard_string != NULL) {
        free(mBoat.clipboard_string);
        mBoat.clipboard_string = NULL;
    }
    CallBoatLibJNIFunc(jstring clipstr = , Object, getPrimaryClipString, "()Ljava/lang/String;");
    const char* string = NULL;
    if (clipstr != NULL) {
        string = (*env)->GetStringUTFChars(env, clipstr, NULL);
        if (string != NULL) {
            mBoat.clipboard_string = strdup(string);
        }
    }
    return mBoat.clipboard_string;
}
