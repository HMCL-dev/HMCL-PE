//
// Created by switchwang(https://github.com/switch-st) on 2018-04-21.
//

#include "slog.h"
#include <string.h>
#include <sys/stat.h>
#include <errno.h>
#include <unistd.h>
#include <stdarg.h>
#include <stdlib.h>

#define SLOG_MAX_CHECK_TIMES        100

static FILE* create_and_open_file(char* path)
{
    int ret;
    FILE* fp = NULL;
    char* c = NULL;
    const char* p = path;

    if (!path || path[0] == '\0') {
        return NULL;
    }
    if (path[0] == '/') path++;
    while (path[0] != '\0') {
        c = strchr(path, '/');
        if (c == NULL) {
            fp = fopen(p, "a");
            return fp;
        }
        *c = '\0';
        ret = mkdir(p, 0755);
        *c = '/';
        if (ret != 0) {
            if (errno != EEXIST) {
                return NULL;
            }
        }
        path = c + 1;
    }

    return NULL;
}

static slog_t* check_log_path(slog_t* slog)
{
    if (!slog)
        return NULL;
    if (!slog->path) {
        fclose(slog->fp);
        free(slog);
        return NULL;
    }
	slog->times = 0;
    if (!access(slog->path, W_OK))
        return slog;
    if (slog->fp) {
        fclose(slog->fp);
        slog->fp = NULL;
    }
    slog->fp = create_and_open_file(slog->path);
    return slog;
}

slog_t* initslog(int base, const char* path)
{
    if (base < 0 || path == NULL || path[0] == '\0')
        return NULL;
    char* p = strdup(path);
    FILE* fp = create_and_open_file(p);
    if (fp == NULL) {
        free(p);
        return NULL;
    }
    slog_t* slog = malloc(sizeof(slog_t));
    slog->fp = fp;
    slog->base = base;
    slog->path = p;
    slog->times = 0;
    return slog;
}

slog_t* writeslog(slog_t* p, int level, const char* tag, const char* text)
{
    return printslog(p, level, tag, "%s\n", text);
}

slog_t* printslog(slog_t* p, int level, const char* tag, const char* fmt, ...)
{
    va_list ap;
    va_start(ap, fmt);
    p = vprintslog(p, level, tag, fmt, ap);
    va_end(ap);
    return p;
}

slog_t* vprintslog(slog_t* p, int level, const char* tag, const char* fmt, va_list ap)
{
    if (!p) {
        return NULL;
    }
    if (level < p->base) {
        return p;
    }
#ifndef NDEBUG
    __android_log_vprint(level, tag, fmt, ap);
#endif /* #ifndef NDEBUG */
    if (p->fp) {
        fprintf(p->fp, "%s: ", tag);
        vfprintf(p->fp, fmt, ap);
        fflush(p->fp);
    }
    p->times++;
    if (p->times > SLOG_MAX_CHECK_TIMES) {
        fflush(p->fp);
        p = check_log_path(p);
    }
    return p;
}

void closeslog(slog_t* p)
{
    if (p) {
        if (p->fp) {
            fclose(p->fp);
        }
        if (p->path) {
            free(p->path);
        }
        free(p);
    }
}
