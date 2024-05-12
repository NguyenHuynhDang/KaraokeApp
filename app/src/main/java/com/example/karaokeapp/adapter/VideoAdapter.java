package com.example.karaokeapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.karaokeapp.GlideApp;
import com.example.karaokeapp.PlayerActivity;
import com.example.karaokeapp.data.VideoItem;
import com.example.karaokeapp.databinding.VideoItemBinding;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>
{
    private List<VideoItem> videoItems = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideoItemBinding view = VideoItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(videoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public void setData(List<VideoItem> newItems)
    {
        VideoDiffUtil videoDiffUtil = new VideoDiffUtil(videoItems, newItems);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(videoDiffUtil);
        videoItems = newItems;
        result.dispatchUpdatesTo(this);
        notifyItemChanged(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final VideoItemBinding binding;

        public ViewHolder(@NonNull VideoItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void setData(VideoItem data)
        {
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PlayerActivity.class);
                intent.putExtra("videoId", data.getId().getId());
                intent.putExtra("videoTitle", data.getSnippet().getTitle());
                v.getContext().startActivity(intent);
            });

            GlideApp.with(binding.getRoot())
                    .load(data.getSnippet().getThumbnail().getHigh().getUrl())
                    .into(binding.ivThumbnail);
            binding.tvVideoTitle.setText(data.getSnippet().getTitle());
        }
    }
}
