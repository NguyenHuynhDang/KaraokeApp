package com.example.karaokeapp.data;

import com.google.gson.annotations.SerializedName;

public class ThumbnailData {
    @SerializedName("high")
    private High high;

    private ThumbnailData(High high) {
        this.high = high;
    }

    public High getHigh() {
        return high;
    }


    public static class High
    {
        @SerializedName("url")
        private String url;

        public High(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
