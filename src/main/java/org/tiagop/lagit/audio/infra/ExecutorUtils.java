package org.tiagop.lagit.audio.infra;

import jakarta.enterprise.context.ApplicationScoped;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ApplicationScoped
public class ExecutorUtils {
    private final ExecutorService executorService;

    public ExecutorUtils() {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public <T> T execute(@NotNull final Callable<Future<T>> task) {
        try {
            return executorService.submit(() -> task.call().get()).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Collection<T> executeMultiple(@NotNull final Collection<Callable<T>> tasks) {
        try {
            List<Future<T>> futures = executorService.invokeAll(tasks);
            return futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (final InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
