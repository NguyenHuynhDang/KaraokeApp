package com.example.karaokeapp.network;

import com.example.karaokeapp.data.VideoData;
import com.example.karaokeapp.data.VideoItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServices
{
    @GET("search")
    public Call<VideoData> getVideo(
            @Query("part") String part,
            @Query("type") String type,
            @Query("maxResults") int maxResult,
            @Query("q") String query
    );

    @GET("videos")
    public Call<VideoItem> getVideoDetails(
            @Query("part") String part,
            @Query("id") String id
    );
}
