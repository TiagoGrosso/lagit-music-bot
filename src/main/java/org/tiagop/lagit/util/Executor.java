package org.tiagop.lagit.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public final class Executor {
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private Executor() {
    }

    public static CompletableFuture<Void> executeInVirtualThread(final Runnable runnable) {
        return CompletableFuture.runAsync(runnable, EXECUTOR);
    }

    public static <T> CompletableFuture<T> executeInVirtualThread(final Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR);
    }
}
