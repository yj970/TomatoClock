package com.yj.tomatoclock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.yj.tomatoclock.databinding.ActivityMainBinding;

import java.lang.reflect.Type;
import java.util.List;

import dev.utils.app.SizeUtils;
import dev.utils.app.share.SPUtils;
import dev.utils.app.toast.ToastUtils;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private TomatoTaskAdapter mAdapter;
    String a;
    String b;
    String c;
    String ef;
    String cc;

    String dd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.rv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rv.addItemDecoration(new SpacesItemDecoration(0, 0, 0, SizeUtils.dipConvertPx(10)));
        mAdapter = new TomatoTaskAdapter();
        mBinding.rv.setAdapter(mAdapter);

        mBinding.fabAdd.setOnClickListener(v -> showTomatoTaskDialog(null));
        mAdapter.setClickListener(new TomatoTaskAdapter.IClickListener() {
            @Override
            public void onClickStart(TomatoTask tomatoTask) {
                showTomatoTaskCountDownDialog(tomatoTask);
            }

            @Override
            public void onClickDelete(TomatoTask tomatoTask) {
                List<TomatoTask> tomatoTaskList = getLocalTomatoTaskList();
                for (TomatoTask t : tomatoTaskList) {
                    if (t.id.equals(tomatoTask.id)) {
                        showConfirmDelete(t, tomatoTaskList);
                        break;
                    }
                }
            }

            @Override
            public void onClickEdit(TomatoTask tomatoTask) {
                showTomatoTaskDialog(tomatoTask);
            }
        });

        updateView();
    }

    private void fun1(){}

    private void showConfirmDelete(TomatoTask t, List<TomatoTask> tomatoTaskList) {
        new AlertDialog.Builder(this).setMessage("确定要删除这个番茄钟任务吗？")
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    tomatoTaskList.remove(t);
                    String newTomatoTasks = new Gson().toJson(tomatoTaskList);
                    SPUtils.getPreference(MainActivity.this).put(Constant.SP_TOMATO_TASK_LIST, newTomatoTasks);
                    updateView();
                }).create().show();
    }

    private void showTomatoTaskCountDownDialog(TomatoTask tomatoTask) {
        TomatoTaskCountDownDialog dialog = new TomatoTaskCountDownDialog(this, tomatoTask);
        dialog.show();
    }

    private void showTomatoTaskDialog(TomatoTask tomatoTask) {
        TomatoTaskDialog dialog = new TomatoTaskDialog(this, tomatoTask);
        dialog.setClickListener(new TomatoTaskDialog.IClickListener() {
            @Override
            public void onSaveTomatoTask() {
                updateView();
            }

            @Override
            public void onClickVoice() {
                XXPermissions.with(MainActivity.this)
                        // 申请多个权限
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all) {
                                    startActivityForResult(new Intent(MainActivity.this, SelectVoiceActivity.class), 1);
                                } else {
                                    ToastUtils.showShort("获取部分权限成功，但部分权限未正常授予");
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                if (never) {
                                    ToastUtils.showShort("被永久拒绝授权，请手动授予读取文件夹权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                                } else {
                                    ToastUtils.showShort("获取读取文件夹权限失败");
                                }
                            }
                        });
            }
        });
        dialog.show();
    }

    private void updateView() {
        List<TomatoTask> tomatoTaskList = getLocalTomatoTaskList();
        mAdapter.setTomatoTasks(tomatoTaskList);
        mAdapter.notifyDataSetChanged();
    }

    private List<TomatoTask> getLocalTomatoTaskList() {
        String tomatoTasks = SPUtils.getPreference(MainActivity.this).getString(Constant.SP_TOMATO_TASK_LIST);
        Type type = new JsonUtils.ParameterizedTypeImpl(TomatoTask.class);
        List<TomatoTask> tomatoTaskList = new Gson().fromJson(tomatoTasks, type);
        return tomatoTaskList;
    }
String test1;



    private void fun2(){}

}