package org.tiagop.lagit.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Executor {
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private Executor() {
    }

    public static CompletableFuture<Void> executeInVirtualThread(final Runnable runnable) {
        return CompletableFuture.runAsync(runnable, EXECUTOR);
    }
}
