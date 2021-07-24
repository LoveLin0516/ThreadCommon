////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package sg.bigo.core.task;
//
//import android.content.Context;
//import android.util.Log;
//import androidx.lifecycle.GenericLifecycleObserver;
//import androidx.lifecycle.LifecycleObserver;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.Lifecycle.Event;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import rx.Scheduler;
//import rx.Single;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.exceptions.Exceptions;
//import rx.functions.Action1;
//import rx.functions.Func1;
//import rx.plugins.RxJavaHooks;
//import rx.plugins.RxJavaPlugins;
//import rx.plugins.RxJavaSchedulersHook;
//import rx.schedulers.Schedulers;
//import sg.bigo.common.AppUtils;
//import sg.bigo.common.DeviceUtils;
//import sg.bigo.common.ThreadUtils;
//import sg.bigo.common.base.NamedThreadFactory;
//import sg.bigo.common.function.Consumer;
//
//public class AppExecutors {
//    private static final String TAG = "AppExecutors";
//    private static final int THREAD_BACKGROUND_PRIORITY = 3;
//    private int mBgCoreCount;
//    private static volatile AppExecutors mAppExecutors;
//    private ThreadPoolExecutor mBackgroundService;
//    private ExecutorService mIOService;
//    private ExecutorService mNetworkService;
//    private final ConcurrentHashMap<LifecycleOwner, HashSet<Subscription>> mLifecycleSubscriptionMap = new ConcurrentHashMap();
//    private final LifecycleObserver OBSERVER = new GenericLifecycleObserver() {
//        public void onStateChanged(LifecycleOwner source, Event event) {
//            if (event == Event.ON_DESTROY) {
//                HashSet<Subscription> data = (HashSet)AppExecutors.this.mLifecycleSubscriptionMap.get(source);
//                Iterator var4 = data.iterator();
//
//                while(var4.hasNext()) {
//                    Subscription subscription = (Subscription)var4.next();
//                    if (subscription != null && !subscription.isUnsubscribed()) {
//                        subscription.unsubscribe();
//                        Log.d("AppExecutors", "remove subscription in" + source.getClass().getCanonicalName());
//                    }
//                }
//
//                data.clear();
//                source.getLifecycle().removeObserver(this);
//                AppExecutors.this.mLifecycleSubscriptionMap.remove(source);
//                Log.d("AppExecutors", "clear lifecycle owner" + source.getClass().getCanonicalName());
//            }
//
//        }
//    };
//
//    public AppExecutors() {
//    }
//
//    public static AppExecutors get() {
//        if (mAppExecutors == null) {
//            Class var0 = AppExecutors.class;
//            synchronized(AppExecutors.class) {
//                if (mAppExecutors == null) {
//                    mAppExecutors = new AppExecutors();
//                }
//            }
//        }
//
//        return mAppExecutors;
//    }
//
//    public int getBgCoreCount() {
//        if (this.mBgCoreCount != 0) {
//            return this.mBgCoreCount;
//        } else {
//            int cpuCount = DeviceUtils.getNumberOfCPUCores();
//            if (cpuCount < 2) {
//                cpuCount = 2;
//            }
//
//            this.mBgCoreCount = Math.min(cpuCount, 4);
//            return this.mBgCoreCount;
//        }
//    }
//
//    private synchronized void ensureBackgroundExecutorCreated() {
//        if (this.mBackgroundService == null) {
//            int cpuCount = DeviceUtils.getNumberOfCPUCores();
//            if (cpuCount < 2) {
//                cpuCount = 2;
//            }
//
//            this.mBackgroundService = new ThreadPoolExecutor(cpuCount + 2, cpuCount + 2, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new NamedThreadFactory("global-background-thread", 3));
//            this.mBackgroundService.allowCoreThreadTimeOut(true);
//        }
//
//    }
//
//    private synchronized void ensureIOExecutorCreated() {
//        if (this.mIOService == null) {
//            this.mIOService = Executors.newFixedThreadPool(2, new NamedThreadFactory("global-io-thread", 3));
//        }
//
//    }
//
//    private synchronized void ensureNetworkExecutorCreated() {
//        if (this.mNetworkService == null) {
//            this.mNetworkService = Executors.newFixedThreadPool(3, new NamedThreadFactory("global-network-thread", 3));
//        }
//
//    }
//
//    public Subscription execute(TaskType taskType, final Runnable task) {
//        return this.execute(taskType, new Callable<Void>() {
//            public Void call() throws Exception {
//                task.run();
//                return null;
//            }
//        }, (Consumer)null, (Consumer)null);
//    }
//
//    public Subscription execute(TaskType taskType, final Runnable task, Consumer<Throwable> errorHandler) {
//        return this.execute(taskType, new Callable<Void>() {
//            public Void call() throws Exception {
//                task.run();
//                return null;
//            }
//        }, (Consumer)null, errorHandler);
//    }
//
//    public <T> Subscription execute(TaskType taskType, Callable<T> task, Consumer<T> UICallback) {
//        return this.execute(taskType, task, UICallback, (Consumer)null);
//    }
//
//    public <T> Subscription execute(TaskType taskType, Callable<T> task, final Consumer<T> UICallback, final Consumer<Throwable> errorHandler) {
//        Scheduler scheduler;
//        switch(taskType) {
//            case IO:
//                if (this.mIOService == null) {
//                    this.ensureIOExecutorCreated();
//                }
//
//                scheduler = Schedulers.from(this.mIOService);
//                break;
//            case BACKGROUND:
//                if (this.mBackgroundService == null) {
//                    this.ensureBackgroundExecutorCreated();
//                }
//
//                scheduler = Schedulers.from(this.mBackgroundService);
//                break;
//            case WORK:
//                scheduler = Schedulers.newThread();
//                break;
//            case NETWORK:
//                if (this.mNetworkService == null) {
//                    this.ensureNetworkExecutorCreated();
//                }
//
//                scheduler = Schedulers.from(this.mNetworkService);
//                break;
//            default:
//                throw new IllegalArgumentException("task type is not supported!!!");
//        }
//
//        Single<T> single = Single.fromCallable(task).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//        return errorHandler == null ? single.subscribe(new Action1<T>() {
//            public void call(T t) {
//                if (UICallback != null) {
//                    UICallback.accept(t);
//                }
//
//            }
//        }) : single.subscribe(new Action1<T>() {
//            public void call(T t) {
//                if (UICallback != null) {
//                    UICallback.accept(t);
//                }
//
//            }
//        }, new Action1<Throwable>() {
//            public void call(Throwable throwable) {
//                if (errorHandler != null) {
//                    errorHandler.accept(throwable);
//                }
//
//            }
//        });
//    }
//
//    public Subscription executeDelay(TaskType taskType, long delay, final Runnable task) {
//        return this.executeDelay(taskType, delay, new Callable<Void>() {
//            public Void call() throws Exception {
//                task.run();
//                return null;
//            }
//        }, (Consumer)null, (Consumer)null);
//    }
//
//    public <T> Subscription executeDelay(TaskType taskType, long delay, Callable<T> task, Consumer<T> UICallback) {
//        return this.executeDelay(taskType, delay, task, UICallback, (Consumer)null);
//    }
//
//    public <T> Subscription executeDelay(TaskType taskType, long delay, final Callable<T> task, final Consumer<T> UICallback, final Consumer<Throwable> errorHandler) {
//        Scheduler scheduler;
//        switch(taskType) {
//            case IO:
//                if (this.mIOService == null) {
//                    this.ensureIOExecutorCreated();
//                }
//
//                scheduler = Schedulers.from(this.mIOService);
//                break;
//            case BACKGROUND:
//                if (this.mBackgroundService == null) {
//                    this.ensureBackgroundExecutorCreated();
//                }
//
//                scheduler = Schedulers.from(this.mBackgroundService);
//                break;
//            case WORK:
//                scheduler = Schedulers.newThread();
//                break;
//            case NETWORK:
//                if (this.mNetworkService == null) {
//                    this.ensureNetworkExecutorCreated();
//                }
//
//                scheduler = Schedulers.from(this.mNetworkService);
//                break;
//            default:
//                throw new IllegalArgumentException("task type is not supported!!!");
//        }
//
//        Single<T> single = Single.just(0).delay(delay, TimeUnit.MILLISECONDS).map(new Func1<Integer, T>() {
//            public T call(Integer integer) {
//                try {
//                    return task.call();
//                } catch (Exception var3) {
//                    throw Exceptions.propagate(var3);
//                }
//            }
//        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//        return errorHandler == null ? single.subscribe(new Action1<T>() {
//            public void call(T t) {
//                if (UICallback != null) {
//                    UICallback.accept(t);
//                }
//
//            }
//        }) : single.subscribe(new Action1<T>() {
//            public void call(T t) {
//                if (UICallback != null) {
//                    UICallback.accept(t);
//                }
//
//            }
//        }, new Action1<Throwable>() {
//            public void call(Throwable throwable) {
//                if (errorHandler != null) {
//                    errorHandler.accept(throwable);
//                }
//
//            }
//        });
//    }
//
//    public void executeWith(Context context, TaskType taskType, final Runnable task) {
//        this.executeWith(context, taskType, new Callable<Void>() {
//            public Void call() throws Exception {
//                task.run();
//                return null;
//            }
//        }, (Consumer)null, (Consumer)null);
//    }
//
//    public <T> void executeWith(Context context, TaskType taskType, Callable<T> task, Consumer<T> UICallback) {
//        this.executeWith(context, taskType, task, UICallback, (Consumer)null);
//    }
//
//    public <T> void executeWith(Context context, TaskType taskType, Callable<T> task, Consumer<T> UICallback, Consumer<Throwable> errorHandler) {
//        final LifecycleOwner lifecycleOwner = context instanceof LifecycleOwner ? (LifecycleOwner)context : null;
//        Subscription subscription = this.execute(taskType, task, UICallback, errorHandler);
//        if (lifecycleOwner != null) {
//            if (!this.mLifecycleSubscriptionMap.containsKey(lifecycleOwner)) {
//                ThreadUtils.runOnUiThread(new Runnable() {
//                    public void run() {
//                        lifecycleOwner.getLifecycle().addObserver(AppExecutors.this.OBSERVER);
//                    }
//                });
//                this.mLifecycleSubscriptionMap.putIfAbsent(lifecycleOwner, new HashSet(16));
//            }
//
//            ((HashSet)this.mLifecycleSubscriptionMap.get(lifecycleOwner)).add(subscription);
//            Log.d("AppExecutors", "add subscription to" + lifecycleOwner.getClass().getCanonicalName());
//        }
//
//    }
//
//    public static final void cancel(Subscription task) {
//        if (task != null && !task.isUnsubscribed()) {
//            task.unsubscribe();
//        }
//
//    }
//
//    public static final void init() {
//        try {
//            RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
//                public Scheduler getComputationScheduler() {
//                    return Schedulers.from(AppExecutors.get().backgroundExecutor());
//                }
//
//                public Scheduler getIOScheduler() {
//                    return Schedulers.from(AppExecutors.get().backgroundExecutor());
//                }
//            });
//        } catch (IllegalStateException var1) {
//            Log.e("AppExecutors", "registerSchedulersHook called more than once");
//        }
//
//        if (AppUtils.isDebug()) {
//            RxJavaHooks.enableAssemblyTracking();
//        }
//
//    }
//
//    public ExecutorService backgroundExecutor() {
//        if (this.mBackgroundService == null) {
//            this.ensureBackgroundExecutorCreated();
//        }
//
//        return this.mBackgroundService;
//    }
//
//    public ExecutorService ioExecutor() {
//        if (this.mIOService == null) {
//            this.ensureIOExecutorCreated();
//        }
//
//        return this.mIOService;
//    }
//
//    public ExecutorService networkExecutor() {
//        if (this.mNetworkService == null) {
//            this.ensureNetworkExecutorCreated();
//        }
//
//        return this.mNetworkService;
//    }
//}
