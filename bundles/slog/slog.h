//
// Created by switchwang(https://github.com/switch-st) on 2018-04-21.
//

#ifndef _SIMPLELOG_H_
#define _SIMPLELOG_H_

#include <android/log.h>
#include <stdio.h>

typedef struct slog_st {
    FILE* fp;
    int base;
    char* path;
    int times;
} slog_t;

slog_t* initslog(int base, const char* path);
slog_t* writeslog(slog_t* p, int level, const char* tag, const char* text);
slog_t* printslog(slog_t* p, int level, const char* tag, const char* fmt, ...);
slog_t* vprintslog(slog_t* p, int level, const char* tag, const char* fmt, va_list ap);
void closeslog(slog_t* p);

#endif //_SIMPLELOG_H_
