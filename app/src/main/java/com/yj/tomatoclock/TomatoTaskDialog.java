package com.yj.tomatoclock;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.yj.tomatoclock.databinding.DialogTomatoTaskBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.utils.app.SizeUtils;
import dev.utils.app.share.SPUtils;
import dev.utils.app.toast.ToastUtils;


public class TomatoTaskDialog extends Dialog {
    private DialogTomatoTaskBinding mBinding;
    private TomatoWheelTime tomatoWheelTime;
    private IClickListener mClickListener;
    private Song song;
    private String uuid;

    public TomatoTaskDialog(@NonNull Context context, TomatoTask tomatoTask) {
        super(context);
        mBinding = DialogTomatoTaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = SizeUtils.dipConvertPx(470);
        window.setAttributes(lp);

        tomatoWheelTime = new TomatoWheelTime(mBinding.timepicker, TomatoWheelTime.Type.HOUR_MIN);
        tomatoWheelTime.initWheelTime();

        if (tomatoTask != null) {
            mBinding.etTomatoTaskName.setText(tomatoTask.tomatoTaskName);
            mBinding.mcbVibrate.setChecked(tomatoTask.vibrateEnable);
            mBinding.mcbVoice.setChecked(tomatoTask.voiceEnable);
            song = tomatoTask.song;
            uuid = tomatoTask.id;
            tomatoWheelTime.setTime(tomatoTask.countDownTime);
            if (song != null) {
                mBinding.tvVoice.setText(song.song);
            }
        }

        setListener();
    }


    private void setListener() {
        mBinding.tvSave.setOnClickListener(v -> {
            String tomatoTaskName = mBinding.etTomatoTaskName.getText().toString();
            String countDownTime = tomatoWheelTime.getTimeData();
            boolean vibrateEnable = mBinding.mcbVibrate.isChecked();
            boolean voiceEnable = mBinding.mcbVoice.isChecked();

            if (tomatoTaskName.length() <= 0) {
                ToastUtils.showShort("请输入番茄钟任务名称");
                return;
            }

            if ("00:00".equals(countDownTime)) {
                ToastUtils.showShort("番茄钟倒计时间要大于0");
                return;
            }

            String tomatoTasks = SPUtils.getPreference(getContext()).getString(Constant.SP_TOMATO_TASK_LIST);
            List<TomatoTask> tomatoTaskList;
            Gson gson = new Gson();
            if (tomatoTasks == null || tomatoTasks.length() <= 0) {
                tomatoTaskList = new ArrayList<>();
            } else {
                Type type = new JsonUtils.ParameterizedTypeImpl(TomatoTask.class);
                tomatoTaskList = gson.fromJson(tomatoTasks, type);
            }
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
            }
            TomatoTask tomatoTask = new TomatoTask(uuid, tomatoTaskName, countDownTime, vibrateEnable, voiceEnable, song);
            // 先删除已有的
            for (TomatoTask t : tomatoTaskList) {
                if (t.id.equals(uuid)) {
                    tomatoTaskList.remove(t);
                    break;
                }
            }
            // 添加
            tomatoTaskList.add(tomatoTask);
            String newTomatoTasks = gson.toJson(tomatoTaskList);
            SPUtils.getPreference(getContext()).put(Constant.SP_TOMATO_TASK_LIST, newTomatoTasks);

            if (mClickListener != null) {
                mClickListener.onSaveTomatoTask();
            }

            dismiss();
        });

        mBinding.tvVoice.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickVoice();
            }
        });
    }

    public interface IClickListener {
        void onSaveTomatoTask();

        void onClickVoice();
    }

    public void setClickListener(IClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @Override
    public void show() {
        super.show();
        EventBus.getDefault().register(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void accept(Song selectedSong) {
        song = selectedSong;
        mBinding.tvVoice.setText(song.song);
    }
}
