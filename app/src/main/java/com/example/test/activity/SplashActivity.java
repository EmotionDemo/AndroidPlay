package com.example.test.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.test.R;
import com.example.test.callback.TimeOutListener;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SplashActivity extends BaseActivity implements TimeOutListener {
    private static final String TAG = "SplashActivity";
    private final Handler myHandler = new Handler(Looper.getMainLooper());
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final int downTotal = 5000;
    private int countDown = downTotal / 1000;
    private Button btnSkip;
    private int loadPhoTimes;
    private ImageView ivSplash;
    private long startTime = System.currentTimeMillis();
    private long endTime = startTime;
    private boolean cutDownTimeout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setTimeOutListener(this);
        ivSplash = findViewById(R.id.iv_ad);
        btnSkip = findViewById(R.id.btn_skip);
        btnSkip.setVisibility(View.GONE);
        loadSplashPho(ivSplash);
    }

    @SuppressLint("HandlerLeak")
    private Handler timeOutHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(SplashActivity.this, msg.getData().getString("timeout"), Toast.LENGTH_SHORT).show();
                startActivity();
            }
        }
    };

    /**
     * 加载开屏图片
     *
     * @param ivSplash
     */
    private void loadSplashPho(ImageView ivSplash) {
        Glide.get(SplashActivity.this).clearMemory();
        executor.execute(() -> {
            Glide.get(SplashActivity.this).clearDiskCache();
        });
        Glide.with(SplashActivity.this).load("http://www.dmoe.cc/random.php").centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        timeOutListener.onTimeout(true);
                        return false;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        timeOutListener.onTimeout(false);
                        btnSkip.setVisibility(View.VISIBLE);
                        Runnable runnableStart = () -> {
                            startActivity();
                        };
                        myHandler.postDelayed(runnableStart, downTotal);
                        btnSkip.setText("点击跳过 " + countDown + " 秒");
                        btnSkip.setOnClickListener(v -> {
                            startActivity();
                            myHandler.removeCallbacks(runnableStart);
                        });
                        // 判断是否加载完成
                        myHandler.postDelayed(mTask, 1000);
                        return false;
                    }
                }).into(ivSplash);
    }
    private boolean isStartActivity = false;
    private void startActivity() {
        if(!isStartActivity){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    /**
     * 执行倒计时任务
     */
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            btnSkip.setText("点击跳过 " + --countDown + " 秒");
            myHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onTimeout(boolean isTimeout) {
        // 监听超时状态
        Log.d(TAG, "-----" + isTimeout);
        executor.execute(() -> {
            while ((endTime - startTime) < 20 * 1000) {
                if (isTimeout) {
                    endTime = System.currentTimeMillis();
                    SystemClock.sleep(2000);
                    runOnUiThread(() -> {
                        if (cutDownTimeout) {
                            return;
                        }
                        if (loadPhoTimes == 0) {
                        } else {
                            Toast.makeText(this, "当前第" + loadPhoTimes + "次重试！", Toast.LENGTH_SHORT).show();
                            loadSplashPho(ivSplash);
                        }
                        loadPhoTimes++;
                    });
                } else {
                    return;
                }
            }
            Message message = timeOutHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("timeout", "请求网络超时，请检查网络状态！");
            message.what = 1;
            message.setData(bundle);
            timeOutHandler.sendMessage(message);
        });
    }

    private TimeOutListener timeOutListener;

    public void setTimeOutListener(TimeOutListener timeOutListener) {
        this.timeOutListener = timeOutListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cutDownTimeout = true;
        isStartActivity = true;
    }
}