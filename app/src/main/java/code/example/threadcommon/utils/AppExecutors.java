package code.example.threadcommon.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by muyao on 2021/7/23
 * Description: 线程任务管理类
 */
public class AppExecutors {
    private volatile static AppExecutors sInstance;
    private static Executor sCurrentExecutor;

    private AppExecutors() {
    }

    private static AppExecutors get() {
        if (sInstance == null) {
            synchronized (AppExecutors.class) {
                if (sInstance == null) {
                    sInstance = new AppExecutors();
                }
            }
        }
        return sInstance;
    }

    /**
     * 在选择的线程模式下直接执行
     *
     * @param runnable runnable
     */
    public void execute(@NonNull Runnable runnable) {
        ObjectHelper.requireNonNull(runnable, "runnable is null");
        ObjectHelper.requireNonNull(sCurrentExecutor, "sCurrentExecutor is null");
        if (sCurrentExecutor != null) {
            sCurrentExecutor.execute(runnable);
        }
    }

    /**
     * 在选择的线程模式下运行，先在onResult中得到结果，然后在callBack中进行回调
     * 此过程不会涉及到线程的切换
     *
     * @param onResult        onResult
     * @param currentCallback currentCallback
     * @param <T>             T
     */
    public <T> void execute(@NonNull final Callable<T> onResult,
                            @NonNull final Consumer<T> currentCallback) {
        execute(onResult, currentCallback, null);
    }

    /**
     * 同{@link AppExecutors#execute(code.example.threadcommon.utils.Callable,
     * code.example.threadcommon.utils.Consumer)}
     *
     * <p>
     * <p>
     * 会辅助开发者捕获异常，并在当前线程中的errorAction中进行回调
     *
     * @param onResult        onResult
     * @param currentCallback currentCallback
     * @param errorAction     errorAction
     * @param <T>             <T>
     */
    public <T> void execute(@NonNull final Callable<T> onResult,
                            @NonNull final Consumer<T> currentCallback,
                            final Consumer<Throwable> errorAction) {
        ObjectHelper.requireNonNullList(sCurrentExecutor, onResult, currentCallback);

        if (sCurrentExecutor != null) {
            sCurrentExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (errorAction == null) {
                        currentCallback.accept(onResult.call());
                    } else {
                        try {
                            currentCallback.accept(onResult.call());
                        } catch (Exception e) {
                            errorAction.accept(e);
                        }
                    }

                }
            });
        }
    }

    /**
     * 注意，当该LifeCycleOwner为空，或者处于不活跃状态时，OnUICallBack不会进行回调
     * <p>
     * 在选择的线程模式下运行，先在OnResult中得到结果，然后回调到UI线程中，
     * 使用该返回的结果
     *
     * @param lifecycleOwner lifecycleOwner
     * @param onResult       onResult
     * @param callback       callback
     * @param <T>            T
     */
    public <T> void execute(@NonNull final LifecycleOwner lifecycleOwner,
                            @NonNull final Callable<T> onResult,
                            @NonNull final UIConsumer<T> callback) {

        execute(lifecycleOwner, onResult, callback, null);
    }

    /**
     * 同 {@link AppExecutors#execute(androidx.lifecycle.LifecycleOwner,
     * code.example.threadcommon.utils.Callable,
     * code.example.threadcommon.utils.UIConsumer)}
     *
     * <p>
     * <p>
     * 会辅助开发者捕获异常，并在UI线程中的errorAction中进行回调
     *
     * @param lifecycleOwner lifecycleOwner
     * @param onResult       onResult
     * @param callback       callback
     * @param <T>            <T>
     */
    public <T> void execute(@NonNull final LifecycleOwner lifecycleOwner,
                            @NonNull final Callable<T> onResult,
                            @NonNull final UIConsumer<T> callback,
                            final UIConsumer<Throwable> errorAction) {

        ObjectHelper.requireNonNullList(sCurrentExecutor, lifecycleOwner, onResult, callback);

        final WeakReference<LifecycleOwner> lifecycleOwnerRef = new WeakReference<>(lifecycleOwner);

        if (sCurrentExecutor != null) {
            sCurrentExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (errorAction == null) {
                        final T t = onResult.call();
                        handleInUIThread(lifecycleOwnerRef, callback, t);
                    } else {
                        try {
                            final T t = onResult.call();
                            //try catch 无法在线程执行的外部捕获异常，所以要捕获此时onCallback.invoke的异常
                            //需要将errorAction传入，在线程内部执行时进行捕获
                            handleInUIThreadWithCatch(lifecycleOwnerRef, callback, errorAction, t);
                        } catch (Exception e) {
                            handleInUIThread(lifecycleOwnerRef, errorAction, e);
                        }
                    }

                }
            });
        }
    }

    private <T> void handleInUIThreadWithCatch(WeakReference<LifecycleOwner> lifecycleOwnerRef,
                                               final UIConsumer<T> callback,
                                               final UIConsumer<Throwable> errorAction,
                                               final T t) {
        if (lifecycleOwnerRef.get() != null) {
            Log.d("ansen", "state--->" +
                    lifecycleOwnerRef.get().getLifecycle().getCurrentState());

            //todo @muyao 待确认
            if (lifecycleOwnerRef.get().getLifecycle()
                    .getCurrentState() != Lifecycle.State.DESTROYED) {
                ThreadUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.accept(t);
                        } catch (Exception e) {
                            errorAction.accept(e);
                        }
                    }
                });
            }
        }
    }

    private <T> void handleInUIThread(WeakReference<LifecycleOwner> lifecycleOwnerRef,
                                      final UIConsumer<T> callback,
                                      final T t) {
        if (lifecycleOwnerRef.get() != null) {
            Log.d("ansen", "state--->" +
                    lifecycleOwnerRef.get().getLifecycle().getCurrentState());

            //todo @muyao 待确认
            if (lifecycleOwnerRef.get().getLifecycle()
                    .getCurrentState() != Lifecycle.State.DESTROYED) {
                ThreadUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.accept(t);
                    }
                });
            }
        }
    }

    /**
     * App 销毁时调用
     */
    public void onDestroy() {
        sCurrentExecutor = null;
        sInstance = null;
    }

    public static synchronized AppExecutors io() {
        sCurrentExecutor = IOHolder.io;
        return get();
    }

    public static synchronized AppExecutors network() {
        sCurrentExecutor = NetworkHolder.network;
        return get();
    }

    public static synchronized AppExecutors mainThread() {
        sCurrentExecutor = MainHolder.main;
        return get();
    }

    public static final class IOHolder {
        public static Executor io = Executors.newFixedThreadPool(3);
    }

    public static final class NetworkHolder {
        public static Executor network = Executors.newFixedThreadPool(3);
    }

    public static final class MainHolder {
        public static Executor main = new MainExecutor();
    }

}
