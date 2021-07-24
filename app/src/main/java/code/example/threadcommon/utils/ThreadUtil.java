package code.example.threadcommon.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by muyao on 2021/7/23
 * Description: 主线程切换类
 */
public class ThreadUtil {

    /**
     * 未明确当前是哪个线程的情况下，切换到主线程
     *
     * @param runnable runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        ObjectHelper.requireNonNull(runnable, "runnable is null");
        if (isMainThread()) {
            runnable.run();
        } else {
            LazyHolder.sUIHandler.post(runnable);
        }

    }

    /**
     * 未明确当前是哪个线程的情况下，使用postAtFrontOfQueue切换到主线程
     *
     * @param runnable runnable
     */
    public static void runOnUIThreadAtFront(Runnable runnable) {
        ObjectHelper.requireNonNull(runnable, "runnable is null");
        if (isMainThread()) {
            runnable.run();
        } else {
            LazyHolder.sUIHandler.postAtFrontOfQueue(runnable);
        }
    }

    /**
     * 使用该方法，务必记得runnable在页面销毁时需要释放,调用{@link ThreadUtil#removeCallbacks(java.lang.Runnable)}
     * <p>
     * <p>
     * 明确当前在非UI线程中，想要切换到UI线程，推荐使用该方法，
     * 如果不明确当前在哪个线程，想要切换到UI线程，
     * 可调用 {@link ThreadUtil#runOnUIThread(java.lang.Runnable)}
     *
     * @param runnable runnable
     * @param delay    delay
     */
    public static void postOnUIThreadDelayed(Runnable runnable, long delay) {
        ObjectHelper.requireNonNull(runnable, "runnable is null");
        LazyHolder.sUIHandler.postDelayed(runnable, delay);
    }

    /**
     * 使用该方法，务必记得runnable在页面销毁时需要释放
     *
     * <p>
     * 明确当前在非UI线程中，想要切换到UI线程，推荐使用该方法，
     * 如果不明确当前在哪个线程，想要切换到UI线程，
     * <p>
     * 可调用 {@link ThreadUtil#runOnUIThread(java.lang.Runnable)}
     *
     * @param runnable runnable
     */
    public static void postOnUIThread(Runnable runnable) {
        ObjectHelper.requireNonNull(runnable, "runnable is null");
        LazyHolder.sUIHandler.post(runnable);
    }

    /**
     * 移除runnable回调，因为sUIHandler为全局handler，为避免全部消息被移除
     * 这里不支持runnable==null
     *
     * @param runnable runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        ObjectHelper.requireNonNull(runnable, "runnable is null");
        LazyHolder.sUIHandler.removeCallbacks(runnable);
    }

    /**
     * @return 当前线程是否是主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static class LazyHolder {
        private static Handler sUIHandler = new Handler(Looper.getMainLooper());
    }


}
