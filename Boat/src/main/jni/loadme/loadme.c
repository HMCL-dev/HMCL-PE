
#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <android/log.h>
#include <sys/mman.h>
#include "xhook.h"
#include "include/loadme.h"

static volatile jobject exitTrap_ctx;
static volatile jclass exitTrap_exitClass;
static volatile jmethodID exitTrap_staticMethod;
static JavaVM *exitTrap_jvm;

void (*old_exit)(int code);
void custom_exit(int code) {
    JNIEnv *env;
    (*exitTrap_jvm)->AttachCurrentThread(exitTrap_jvm, &env, NULL);
    (*env)->CallStaticVoidMethod(env, exitTrap_exitClass, exitTrap_staticMethod, exitTrap_ctx, code);
    (*env)->DeleteGlobalRef(env, exitTrap_ctx);
    (*env)->DeleteGlobalRef(env, exitTrap_exitClass);
    (*exitTrap_jvm)->DetachCurrentThread(exitTrap_jvm);
    old_exit(code);
}

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_setupExitTrap(JNIEnv *env, jclass clazz, jobject context) {
    exitTrap_ctx = (*env)->NewGlobalRef(env,context);
    (*env)->GetJavaVM(env,&exitTrap_jvm);
    exitTrap_exitClass = (*env)->NewGlobalRef(env,(*env)->FindClass(env,"cosine/boat/BoatActivity"));
    exitTrap_staticMethod = (*env)->GetStaticMethodID(env,exitTrap_exitClass,"onExit","(Landroid/content/Context;I)V");
    xhook_enable_debug(1);
    xhook_register(".*\\.so$", "exit", custom_exit, (void **) &old_exit);
    xhook_refresh(1);
}

void* caller_addr;

void (*__loader_dlopen)(const char* __filename, int __flag, const void* caller_addr);
void (*old_dlopen)(const char* __filename, int __flag);
void new_dlopen(const char* __filename, int __flag) {
    return __loader_dlopen(__filename, __flag, caller_addr);
}
void (*__loader_dlsym)(void * __handle, const char* __symbol, const void* caller_addr);
void (*old_dlsym)(void * __handle, const char* __symbol);
void new_dlsym(void * __handle, const char* __symbol) {
    return __loader_dlsym(__handle, __symbol, caller_addr);
}

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_setupDlHook(JNIEnv* env, jclass clazz){
    void* handle;
    handle = dlopen("libdl.so", RTLD_LAZY);
    __loader_dlopen = (void (*)(const char*, int, const void*))dlsym(handle, "__loader_dlopen");
    __loader_dlsym = (void (*)(void *, const char*, const void*))dlsym(handle, "__loader_dlsym");

    caller_addr = setup_dl_hook();
}

void* setup_dl_hook() {
    return __builtin_return_address(0);
}

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_hookDlopen(JNIEnv *env, jclass clazz) {
    xhook_register(".*\\.so$", "dlopen", new_dlopen, (void **) &old_dlopen);
    xhook_register(".*\\.so$", "dlsym", new_dlsym, (void **) &old_dlsym);
    xhook_refresh(1);
}

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_redirectStdio(JNIEnv* env, jclass clazz, jstring path) {
    char const* file = (*env)->GetStringUTFChars(env, path, 0);

    int fd = open(file, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    dup2(fd, 1);
    dup2(fd, 2);

    (*env)->ReleaseStringUTFChars(env, path, file);
}

JNIEXPORT jint JNICALL Java_cosine_boat_LoadMe_chdir(JNIEnv* env, jclass clazz, jstring path) {
    char const* dir = (*env)->GetStringUTFChars(env, path, 0);

    int b = chdir(dir);

    (*env)->ReleaseStringUTFChars(env, path, dir);
    return b;
}

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_setenv(JNIEnv* env, jclass clazz, jstring str1, jstring str2) {
    char const* name = (*env)->GetStringUTFChars(env, str1, 0);
    char const* value = (*env)->GetStringUTFChars(env, str2, 0);

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, str1, name);
    (*env)->ReleaseStringUTFChars(env, str2, value);
}

JNIEXPORT jint JNICALL Java_cosine_boat_LoadMe_dlopen(JNIEnv* env, jclass clazz, jstring str1) {
    dlerror();

    int ret = 0;
    char const* lib_name = (*env)->GetStringUTFChars(env, str1, 0);

    void* handle;
    dlerror();
    handle = dlopen(lib_name, RTLD_GLOBAL);
    __android_log_print(ANDROID_LOG_ERROR, "Boat", "loading %s (error = %s)", lib_name, dlerror());

    if (handle == NULL) {
        ret = -1;
    }

    (*env)->ReleaseStringUTFChars(env, str1, lib_name);
    return ret;
}

unsigned gen_ldr_pc(unsigned rt, signed long off) {
    // 33 222 2 22 2222111111111100000 00000
    // 10 987 6 54 3210987654321098765 43210
    // 01 011 0 00 1111111111111111011 00010
    //             imm                 rt
    unsigned result = 0x0;

    result |= 0x58; // 01 011 0 00;
    result <<= 19;

    signed long imm = off / 4;
    imm &= 0x7ffff;
    result |= imm;
    result <<= 5;

    rt &= 0x1f;
    result |= rt;

    return result;
}
unsigned gen_ret(unsigned lr) {
    // 3322222 2 2 22 21111 1111 1 1 00000 00000
    // 1098765 4 3 21 09876 5432 1 0 98765 43210
    // 1101011 0 0 10 11111 0000 0 0 11110 00000
    //                               lr
    unsigned result = 0x0;

    result |= 0x3597c0;  // 1101011 0 0 10 11111 0000 0 0
    result <<= 5;
    lr &= 0x1f;
    result |= lr;
    result <<= 5;
    result |= 0x0;
    return result;
}
unsigned gen_mov_reg(unsigned tr, unsigned sr) {
    // 3 32 22222 22 2 21111 111111 00000 00000
    // 1 09 87654 32 1 09876 543210 98765 43210
    // 1 01 01010 00 0 11110 000000 11111 00010
    //                 sr                 tr
    unsigned result = 0x0;

    result |= 0x550;  // 1 01 01010 00 0
    result <<= 5;

    sr &= 0x1f;
    result |= sr;
    result <<= 11;

    result |= 0x1f;  // 000000 11111
    result <<= 5;

    tr &= 0x1f;
    result |= tr;

    return result;
}

unsigned gen_mov_imm(unsigned tr, signed short imm) {
    // 3 32 222222 22 2111111111100000 00000
    // 1 09 876543 21 0987654321098765 43210
    // 1 10 100101 00 0000000001111111 00000
    //                imm              tr
    unsigned result = 0x0;

    result |= 0x694;  // 1 10 100101 00
    result <<= 16;

    result |= imm;
    result <<= 5;

    tr &= 0x1f;
    result |= tr;

    return result;
}

void stub() {

}
JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_patchLinker(JNIEnv *env, jclass clazz) {

#define PAGE_START(x) ((void*)((unsigned long)(x) & ~((unsigned long)getpagesize() - 1)))

    void* libdl_handle = dlopen("libdl.so", RTLD_GLOBAL);

    unsigned* dlopen_addr = (unsigned*)dlsym(libdl_handle, "dlopen");
    unsigned* dlsym_addr = (unsigned*)dlsym(libdl_handle, "dlsym");
    unsigned* dlvsym_addr = (unsigned*)dlsym(libdl_handle, "dlvsym");
    unsigned* buffer = (unsigned*)dlsym(libdl_handle, "android_get_LD_LIBRARY_PATH");
    mprotect(PAGE_START(buffer), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC );
    mprotect(PAGE_START(dlopen_addr), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC );
    mprotect(PAGE_START(dlsym_addr), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC );
    mprotect(PAGE_START(dlvsym_addr), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC );

    unsigned ins_ret = gen_ret(30);
    unsigned ins_mov_x0_0 = gen_mov_imm(0, 0);

    buffer[0] = ins_mov_x0_0;
    buffer[1] = ins_ret;

    void** tmp_addr = (void**)(buffer + 2);
    *tmp_addr = stub;

    unsigned ins_mov_x2_x30 = gen_mov_reg(2, 30);
    unsigned ins_mov_x3_x30 = gen_mov_reg(3, 30);

    int dlopen_hooked = 0;
    int dlsym_hooked = 0;
    int dlvsym_hooked = 0;
    for (int i = 0; dlopen_addr[i] != ins_ret; i++){
        if (dlopen_addr[i] == ins_mov_x2_x30) {
            dlopen_addr[i] = gen_ldr_pc(2, (unsigned long)tmp_addr - (unsigned long)&dlopen_addr[i]);
            dlopen_hooked = 1;
            break;
        }
    }
    for (int i = 0; dlsym_addr[i] != ins_ret; i++){
        if (dlsym_addr[i] == ins_mov_x2_x30) {
            dlsym_addr[i] = gen_ldr_pc(2, (unsigned long)tmp_addr - (unsigned long)&dlsym_addr[i]);
            dlsym_hooked = 1;
            break;
        }
    }
    for (int i = 0; dlvsym_addr[i] != ins_ret; i++){
        if (dlvsym_addr[i] == ins_mov_x3_x30) {
            dlvsym_addr[i] = gen_ldr_pc(3, (unsigned long)tmp_addr - (unsigned long)&dlvsym_addr[i]);
            dlvsym_hooked = 1;
            break;
        }
    }

    if (dlopen_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, "Boat", "dlopen() not patched");
    }
    if (dlsym_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, "Boat", "dlsym() not patched");
    }
    if (dlvsym_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, "Boat", "dlvsym() not patched");
    }
#undef PAGE_START
}

#define FULL_VERSION "1.8.0-internal"
#define DOT_VERSION "1.8"
#define PROGNAME "java"
#define LAUNCHER_NAME "openjdk"


static char* const_progname = PROGNAME;
static const char* const_launcher = LAUNCHER_NAME;
static const char** const_jargs = NULL;
static const char** const_appclasspath = NULL;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jboolean const_javaw = JNI_FALSE;
static const jint const_ergo_class = 0;    //DEFAULT_POLICY

int
(*JLI_Launch)(int argc, char ** argv,              /* main argc, argc */
              int jargc, const char** jargv,          /* java args */
              int appclassc, const char** appclassv,  /* app classpath */
              const char* fullversion,                /* full version defined */
              const char* dotversion,                 /* dot version defined */
              const char* pname,                      /* program name */
              const char* lname,                      /* launcher name */
              jboolean javaargs,                      /* JAVA_ARGS */
              jboolean cpwildcard,                    /* classpath wildcard */
              jboolean javaw,                         /* windows-only javaw */
              jint     ergo_class                     /* ergnomics policy */
);

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_setupJLI(JNIEnv* env, jclass clazz){

    void* handle;
    handle = dlopen("libjli.so", RTLD_LAZY);
    JLI_Launch = (int (*)(int, char **, int, const char**, int, const char**, const char*, const char*, const char*, const char*, jboolean, jboolean, jboolean, jint))dlsym(handle, "JLI_Launch");

}

JNIEXPORT jint JNICALL Java_cosine_boat_LoadMe_jliLaunch(JNIEnv *env, jclass clazz, jobjectArray argsArray){
    int argc = (*env)->GetArrayLength(env, argsArray);
    char* argv[argc];
    for (int i = 0; i < argc; i++) {
        jstring str = (*env)->GetObjectArrayElement(env, argsArray, i);
        int len = (*env)->GetStringUTFLength(env, str);
        char* buf = malloc(len + 1);
        int characterLen = (*env)->GetStringLength(env, str);
        (*env)->GetStringUTFRegion(env, str, 0, characterLen, buf);
        buf[len] = 0;
        argv[i] = buf;
    }

    return JLI_Launch(argc, argv,
                      sizeof(const_jargs) / sizeof(char *), const_jargs,
                      sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                      FULL_VERSION,
                      DOT_VERSION,
                      (const_progname != NULL) ? const_progname : *argv,
                      (const_launcher != NULL) ? const_launcher : *argv,
                      (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                      const_cpwildcard, const_javaw, const_ergo_class);

}

