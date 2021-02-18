package com.yj.tomatoclock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yj.tomatoclock.databinding.ItemTomatoTaskBinding;

import java.util.List;

public class TomatoTaskAdapter extends RecyclerView.Adapter<TomatoTaskAdapter.ViewHolder> {
    private List<TomatoTask> tomatoTasks;
    private IClickListener mClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tomato_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.tvTomatoTaskName.setText(tomatoTasks.get(position).tomatoTaskName);
        holder.mBinding.tvTomatoTaskCountDownTime.setText(tomatoTasks.get(position).countDownTime);
        holder.mBinding.mcbVibrate.setChecked(tomatoTasks.get(position).vibrateEnable);
        holder.mBinding.mcbVoice.setChecked(tomatoTasks.get(position).voiceEnable);

        holder.mBinding.tvStart.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickStart(tomatoTasks.get(position));
            }
        });

        holder.mBinding.tvDelete.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickDelete(tomatoTasks.get(position));
            }
        });

        holder.mBinding.tvEdit.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickEdit(tomatoTasks.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return tomatoTasks == null ? 0 : tomatoTasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTomatoTaskBinding mBinding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemTomatoTaskBinding.bind(itemView);
        }
    }

    public void setTomatoTasks(List<TomatoTask> tomatoTasks) {
        this.tomatoTasks = tomatoTasks;
    }

    public interface IClickListener{
        void onClickStart(TomatoTask tomatoTask);
        void onClickDelete(TomatoTask tomatoTask);
        void onClickEdit(TomatoTask tomatoTask);
    }

    public void setClickListener(IClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }
}
