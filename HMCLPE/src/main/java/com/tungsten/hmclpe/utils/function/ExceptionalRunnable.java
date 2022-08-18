package com.tungsten.hmclpe.utils.function;

import java.util.concurrent.Callable;

/**
 *
 * @author huangyuhui
 */
public interface ExceptionalRunnable<E extends Exception> {

    void run() throws E;

    default Callable<Void> toCallable() {
        return () -> {
            run();
            return null;
        };
    }

}
