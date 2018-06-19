package me.itstheholyblack.vigilant_eureka.util;

import java.util.Random;

public class ArrayUtil {
    private static Random r = new Random();

    public static boolean contains(Object[] arr, Object item) {
        for (Object n : arr) {
            if (item.equals(n)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T randomFromArr(T[] arr) {
        return arr[r.nextInt(arr.length)];
    }
}
