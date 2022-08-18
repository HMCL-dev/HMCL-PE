package com.tungsten.hmclpe.utils;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * When {@link #accept(ARG)} is called, this class invokes the handler on another thread.
 * If {@link #accept(ARG)} is called more than one time before the handler starts processing,
 * the handler will only be invoked once, taking the latest argument as its input.
 *
 * @author yushijinhun
 */
public class InvocationDispatcher<ARG> implements Consumer<ARG> {

    public static <ARG> InvocationDispatcher<ARG> runOn(Executor executor, Consumer<ARG> action) {
        return new InvocationDispatcher<>(arg -> executor.execute(() -> {
            synchronized (action) {
                action.accept(arg.get());
            }
        }));
    }

    private Consumer<Supplier<ARG>> handler;

    private AtomicReference<Optional<ARG>> pendingArg = new AtomicReference<>();

    public InvocationDispatcher(Consumer<Supplier<ARG>> handler) {
        this.handler = handler;
    }

    @Override
    public void accept(ARG arg) {
        if (pendingArg.getAndSet(Optional.ofNullable(arg)) == null) {
            handler.accept(() -> pendingArg.getAndSet(null).orElse(null));
        }
    }
}