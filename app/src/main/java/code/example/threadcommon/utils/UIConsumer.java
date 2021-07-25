package code.example.threadcommon.utils;

/**
 * Created by muyao on 2021/7/25
 * Description: UI线程使用的Consumer回调
 */
public interface UIConsumer<T> {
    void accept(T t);
}
