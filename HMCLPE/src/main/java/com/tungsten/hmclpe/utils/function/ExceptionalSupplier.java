package com.tungsten.hmclpe.utils.function;

import java.util.concurrent.Callable;

/**
 *
 * @author huangyuhui
 */
public interface ExceptionalSupplier<R, E extends Exception> {
    R get() throws E;

    default Callable<R> toCallable() {
        return this::get;
    }
}
