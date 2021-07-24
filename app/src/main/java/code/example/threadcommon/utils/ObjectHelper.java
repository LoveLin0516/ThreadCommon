package code.example.threadcommon.utils;

/**
 * Created by muyao on 2021/7/23
 * Description: 帮助类
 */
public class ObjectHelper {

    /**
     * 单个对象判空逻辑
     *
     * @param object  object
     * @param message message
     * @param <T>     <T>
     */
    public static <T> void requireNonNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    public static <T> void requireNonNull(T object) {
        requireNonNull(object, "params can not be null");
    }

    /**
     * 数组对象判空逻辑
     *
     * @param message     message
     * @param objectArray objectArray
     * @param <T>         T
     */
    public static <T> void requireNonNullList(String message, T... objectArray) {
        requireNonNull(objectArray, message);
        for (T t : objectArray) {
            requireNonNull(t, message);
        }
    }

    /**
     * @param objectArray objectArray
     * @param <T>         T
     */
    public static <T> void requireNonNullList(T... objectArray) {
        requireNonNull(objectArray, "any input params can not be null");
        for (T t : objectArray) {
            requireNonNull(t, "any input params can not be null");
        }
    }

}
