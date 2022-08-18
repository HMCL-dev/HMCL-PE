package com.tungsten.hmclpe.task;

import static com.tungsten.hmclpe.utils.Lang.threadPool;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.concurrent.*;

import com.tungsten.hmclpe.utils.Logging;

/**
 *
 * @author huangyuhui
 */
public final class Schedulers {

    private Schedulers() {
    }

    private static volatile ExecutorService IO_EXECUTOR;

    /**
     * Get singleton instance of the thread pool for I/O operations,
     * usually for reading files from disk, or Internet connections.
     *
     * This thread pool has no more than 4 threads, and number of threads will get
     * reduced if concurrency is less than thread number.
     *
     * @return Thread pool for I/O operations.
     */
    public static ExecutorService io() {
        if (IO_EXECUTOR == null) {
            synchronized (Schedulers.class) {
                if (IO_EXECUTOR == null) {
                    IO_EXECUTOR = threadPool("IO", true, 4, 10, TimeUnit.SECONDS);
                }
            }
        }

        return IO_EXECUTOR;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Executor defaultScheduler() {
        return ForkJoinPool.commonPool();
    }

    public static synchronized void shutdown() {
        Logging.LOG.info("Shutting down executor services.");

        // shutdownNow will interrupt all threads.
        // So when we want to close the HMCLPE, no threads need to be waited for finish.
        // Sometimes it resolves the problem that the HMCLPE does not exit.

        if (IO_EXECUTOR != null)
            IO_EXECUTOR.shutdownNow();
    }

}
