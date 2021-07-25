package code.example.threadcommon;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import code.example.threadcommon.utils.AppExecutors;
import code.example.threadcommon.utils.ThreadUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //测试非UI线程下的回调捕获等相关
//        AppExecutors.io().execute(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("ansen", "thread-name->" + Thread.currentThread().getName());
//                Log.d("ansen", "thread-id->" + Thread.currentThread().getId());
//
//                AppExecutors.io().execute(new AppExecutors.OnResult<String>() {
//                    @Override
//                    public String execute() {
//                        Log.d("ansen", "thread-name--1-->" + Thread.currentThread().getName());
//                        Log.d("ansen", "thread-id--1-->" + Thread.currentThread().getId());
//
////                        String test = null;
////                        test.toString();
//                        return null;
//                    }
//                }, new AppExecutors.OnCallBack<String>() {
//                    @Override
//                    public void invoke(String s) {
//
//                        Log.d("ansen", "thread-name--2-->" + Thread.currentThread().getName());
//                        Log.d("ansen", "thread-id--2-->" + Thread.currentThread().getId());
//
//
//                    }
//                }, new AppExecutors.OnCallBack<Throwable>() {
//                    @Override
//                    public void invoke(Throwable throwable) {
//                        Log.d("ansen", "thread-name--3-->" + Thread.currentThread().getName());
//                        Log.d("ansen", "thread-id--3-->" + Thread.currentThread().getId());
//                    }
//                });
//            }
//        });

        //测试延时状态下的崩溃捕获相关，切换几个线程的状态下
//        AppExecutors.io().execute(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("ansen", "thread-name->" + Thread.currentThread().getName());
//                Log.d("ansen", "thread-id->" + Thread.currentThread().getId());
//
//                ThreadUtil.postOnUIThreadDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        AppExecutors.io().execute(MainActivity.this,
//                                new AppExecutors.OnResult<String>() {
//                                    @Override
//                                    public String execute() {
//                                        Log.d("ansen", "thread-name--1-->" + Thread.currentThread().getName());
//                                        Log.d("ansen", "thread-id--1-->" + Thread.currentThread().getId());
//
//                                        return null;
//                                    }
//                                }, new AppExecutors.OnUICallBack<String>() {
//                                    @Override
//                                    public void invoke(String s) {
//                                        Log.d("ansen", "thread-name--2-->" + Thread.currentThread().getName());
//                                        Log.d("ansen", "thread-id--2-->" + Thread.currentThread().getId());
//
//                                        String test = null;
//                                        test.toString();
//                                    }
//                                }, new AppExecutors.OnUICallBack<Throwable>() {
//                                    @Override
//                                    public void invoke(Throwable throwable) {
//                                        Log.d("ansen", "thread-name--3-->" + Thread.currentThread().getName());
//                                        Log.d("ansen", "thread-id--3-->" + Thread.currentThread().getId());
//                                    }
//                                });
//                    }
//                },2000L);
//
//
//            }
//        });


        //测试延时状态下的崩溃捕获相关
//        ThreadUtil.postOnUIThreadDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AppExecutors.io().execute(MainActivity.this,
//                        new AppExecutors.OnResult<String>() {
//                            @Override
//                            public String execute() {
//                                Log.d("ansen", "thread-name--1-->" + Thread.currentThread().getName());
//                                Log.d("ansen", "thread-id--1-->" + Thread.currentThread().getId());
//
////                                String test = null;
////                                test.toString();
//                                return null;
//                            }
//                        }, new AppExecutors.OnUICallBack<String>() {
//                            @Override
//                            public void invoke(String s) {
//                                Log.d("ansen", "thread-name--2-->" + Thread.currentThread().getName());
//                                Log.d("ansen", "thread-id--2-->" + Thread.currentThread().getId());
//
//                            }
//                        }, new AppExecutors.OnUICallBack<Throwable>() {
//                            @Override
//                            public void invoke(Throwable throwable) {
//                                Log.d("ansen", "thread-name--3-->" + Thread.currentThread().getName());
//                                Log.d("ansen", "thread-id--3-->" + Thread.currentThread().getId());
//                            }
//                        });
//            }
//        }, 2000L);


        //测试延时下，不设置errorAction的崩溃相关，onResult一定是会执行的，不管页面是否销毁
//        AppExecutors.mainThread().execute(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("ansen", "thread-name->" + Thread.currentThread().getName());
//                Log.d("ansen", "thread-id->" + Thread.currentThread().getId());
//
//                AppExecutors.io().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("ansen", "thread-name->" + Thread.currentThread().getName());
//                        Log.d("ansen", "thread-id->" + Thread.currentThread().getId());
//
//                        ThreadUtil.postOnUIThreadDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                AppExecutors.io().execute(MainActivity.this,
//                                        new AppExecutors.OnResult<String>() {
//                                            @Override
//                                            public String execute() {
//                                                Log.d("ansen", "thread-name--1-->" + Thread.currentThread().getName());
//                                                Log.d("ansen", "thread-id--1-->" + Thread.currentThread().getId());
//
//                                                String test = null;
//                                                test.toString();
//                                                return null;
//                                            }
//                                        }, new AppExecutors.OnUICallBack<String>() {
//                                            @Override
//                                            public void invoke(String s) {
//                                                Log.d("ansen", "thread-name--2-->" + Thread.currentThread().getName());
//                                                Log.d("ansen", "thread-id--2-->" + Thread.currentThread().getId());
//                                            }
//                                        });
//                            }
//                        }, 2000L);
//
//                    }
//                });
//            }
//        });

        //测试主线程切换相关
        AppExecutors.io().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("ansen", "thread-name->" + Thread.currentThread().getName());
                Log.d("ansen", "thread-id->" + Thread.currentThread().getId());

                AppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        Log.d("ansen", "thread-name--1-->" + Thread.currentThread().getName());
                        Log.d("ansen", "thread-id--1-->" + Thread.currentThread().getId());
                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}