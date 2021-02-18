package com.yj.tomatoclock;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yj.tomatoclock.databinding.ItemSongBinding;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Song> songs;
    private IClickListener mClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.tvSongName.setText(songs.get(position).song);

        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClick(songs.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSongBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemSongBinding.bind(itemView);
        }
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public interface IClickListener {
        void onClick(Song song);
    }

    public void setClickListener(IClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }
}

