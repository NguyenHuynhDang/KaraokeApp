package com.example.karaokeapp.data;

import com.google.gson.annotations.SerializedName;

public class VideoItem {
    @SerializedName("id")
    private VideoId id;

    @SerializedName("snippet")
    private SnippetData snippet;

    @SerializedName("contentDetails")
    private ContentDetails contentDetails;

    public VideoItem(VideoId id, SnippetData snippet, ContentDetails contentDetails)
    {
        this.id = id;
        this.snippet = snippet;
        this.contentDetails = contentDetails;
    }

    public VideoId getId() {
        return id;
    }

    public SnippetData getSnippet() {
        return snippet;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    public static class VideoId
    {
        @SerializedName("videoId")
        private String id;

        public VideoId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class ContentDetails
    {
        @SerializedName("duration")
        private String duration;

        public ContentDetails(String duration) {
            this.duration = duration;
        }

        public String getDuration() {
            return duration;
        }
    }
}
