package org.tiagop.lagit;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {
    private RandomUtils() {
    }

    public static long nextLong(final long origin, final long bound) {
        return ThreadLocalRandom.current().nextLong(origin, bound);
    }
}
