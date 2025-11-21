package by.bsu.waterships.shared.utils;

import java.util.concurrent.Callable;

public class ThrowableUtils {
    public static <T> T nullIfThrows(Callable<T> supplier) {
        try {
            return supplier.call();
        } catch (Exception e) {
            return null;
        }
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }
}
