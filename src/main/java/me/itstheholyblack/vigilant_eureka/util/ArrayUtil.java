package me.itstheholyblack.vigilant_eureka.util;

public class ArrayUtil {
    public static boolean contains(Object[] arr, Object item) {
        for (Object n : arr) {
            if (item.equals(n)) {
                return true;
            }
        }
        return false;
    }
}
