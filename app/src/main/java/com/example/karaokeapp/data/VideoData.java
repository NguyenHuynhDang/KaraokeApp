package com.example.karaokeapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoData
{
    @SerializedName("nextPageToken")
    private String nextPageToken;

    @SerializedName("items")
    private List<VideoItem> items;

    public VideoData(String nextPageToken, List<VideoItem> items) {
        this.nextPageToken = nextPageToken;
        this.items = items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public List<VideoItem> getItems() {
        return items;
    }

    public void setItemAt(int position, VideoItem videoItem)
    {
        items.set(position, videoItem);
    }
}

