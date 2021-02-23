package com.yj.tomatoclock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.yj.tomatoclock.databinding.DialogTomatoTaskBinding;
import com.yj.tomatoclock.databinding.DialogTomatoTaskCountDownBinding;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import dev.utils.app.SizeUtils;
import dev.utils.app.share.SPUtils;
import dev.utils.app.toast.ToastUtils;


public class TomatoTaskCountDownDialog extends Dialog {
    private DialogTomatoTaskCountDownBinding mBinding;
    private CountDownTimer mCountDownTimer;
    private TomatoTask tomatoTask;
    private Vibrator vibrator;
    private MediaPlayer mMediaPlayer;

    public TomatoTaskCountDownDialog(@NonNull Context context, TomatoTask tomatoTask) {
        super(context);
        this.tomatoTask = tomatoTask;
        mBinding = DialogTomatoTaskCountDownBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = SizeUtils.dipConvertPx(400);
        window.setAttributes(lp);

        mBinding.tvTomatoTaskName.setText(tomatoTask.tomatoTaskName);
        mBinding.mcbVibrate.setChecked(tomatoTask.vibrateEnable);
        mBinding.mcbVoice.setChecked(tomatoTask.voiceEnable);
        if (tomatoTask.song != null) {
            mBinding.tvVoice.setText(tomatoTask.song.song);
        }

        String countDown = tomatoTask.countDownTime;
        long countDownTime = timeToStamp(countDown);
        mCountDownTimer = new CountDownTimer(countDownTime, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.tvCountDown.setText("剩余 " + stampToTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                mBinding.tvCountDown.setText("结束");
                mBinding.tvCountDown.setClickable(true);
                vibrate();
                voice();
            }
        };
        mCountDownTimer.start();

        setListener();
        mBinding.tvCountDown.setClickable(false);
        startForegroundService();
    }

    private void startForegroundService() {
        //启动服务
        if (!ForegroundService.serviceIsLive) {
            // Android 8.0使用startForegroundService在前台启动新服务
            Intent mForegroundService = new Intent(getContext(), ForegroundService.class);
            mForegroundService.putExtra("tomatoTaskName", tomatoTask.tomatoTaskName);

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            long awakenTime = System.currentTimeMillis() + timeToStamp(tomatoTask.countDownTime);
            String strAwakenTime = formatter.format(awakenTime);
            mForegroundService.putExtra("awakenTime", strAwakenTime);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(mForegroundService);
            } else {
                getContext().startService(mForegroundService);
            }
        } else {
            Toast.makeText(getContext(), "前台服务正在运行中...", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopForegroundService() {
        //停止服务
        Intent mForegroundService = new Intent(getContext(), ForegroundService.class);
        getContext().stopService(mForegroundService);
    }

    private void voice() {
        boolean voice = tomatoTask.voiceEnable;
        Song song = tomatoTask.song;
        if (voice && song != null) {
            String path = song.path;
            try {
                releaseMediaPlayer();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cancelVoice() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    // 释放多媒体资源
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void vibrate() {
        boolean vibrate = tomatoTask.vibrateEnable;
        if (vibrate) {
            vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
            long[] patter = {1000, 1000, 1000, 1000};
            vibrator.vibrate(patter, 0);
        }
    }

    private void cancelVibrate() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void cancelAll() {
        cancelVibrate();
        cancelVoice();
        stopForegroundService();
    }

    private void setListener() {
        mBinding.tvFinish.setOnClickListener(v -> {
            finish();
        });

        mBinding.tvCountDown.setOnClickListener(v -> {
            finish();
        });

        mBinding.tvBack2Desktop.setOnClickListener(v -> {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            getContext().startActivity(home);
        });

    }

    private void finish() {
        mCountDownTimer.cancel();
        cancelAll();
        dismiss();
    }

    /* //日期转换为时间戳 */
    public long timeToStamp(String timers) {
        String[] strs = timers.split(":");
        long timeStemp = Integer.parseInt(strs[0]) * 60 * 60 * 1000L + Integer.parseInt(strs[1]) * 60 * 1000L;
        return timeStemp;
    }

    public String stampToTime(long stamp) {
        // 总秒数
        int second = (int) Math.floor(stamp / 1000);
// 天数
        int day = (int) Math.floor(second / 3600 / 24);
// 小时
        int hr = (int) Math.floor(second / 3600 % 24);
// 分钟
        int min = (int) Math.floor(second / 60 % 60);
// 秒
        int sec = (int) Math.floor(second % 60);
        return hr + "小时" + min + "分钟" + sec + "秒";
    }


}
