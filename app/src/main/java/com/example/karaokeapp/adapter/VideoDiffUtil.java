package com.example.karaokeapp.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.karaokeapp.data.VideoItem;

import java.util.List;

public class VideoDiffUtil extends DiffUtil.Callback {
    private final List<VideoItem> oldList;
    private final List<VideoItem> newList;

    public VideoDiffUtil(List<VideoItem> oldList, List<VideoItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId().getId().equals(newList.get(newItemPosition).getId().getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        VideoItem oldVideo = oldList.get(oldItemPosition);
        VideoItem newVideo = newList.get(newItemPosition);
        return oldVideo.getSnippet().getTitle().equals(newVideo.getSnippet().getTitle());
    }
}
