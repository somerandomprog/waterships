package by.bsu.waterships.shared.utils;

public class NullUtils {
    public static <T> T coalesce(T left, T right) {
        return left != null ? left : right;
    }
}
