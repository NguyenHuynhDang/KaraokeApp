package com.example.karaokeapp.data;

import com.google.gson.annotations.SerializedName;

public class SnippetData {
    @SerializedName("title")
    private String title;

    @SerializedName("thumbnails")
    private ThumbnailData thumbnail;

    public SnippetData(String title, ThumbnailData thumbnail) {
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public ThumbnailData getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }
}
