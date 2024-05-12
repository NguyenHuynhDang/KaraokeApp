package com.example.karaokeapp.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.karaokeapp.data.VideoData;
import com.example.karaokeapp.data.VideoItem;
import com.example.karaokeapp.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoViewModel extends ViewModel {
    private final MutableLiveData<VideoData> _video = new MutableLiveData<>();
    public MutableLiveData<VideoData> getVideo() { return _video; }
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> IsLoading() { return _isLoading; }

    private final MutableLiveData<String> _message = new MutableLiveData<>();
    public MutableLiveData<String> getMessage() { return _message; }
    private final String typeSearch = "video";
    private final String querySearch = "Karaoke";
    private final int maxResult = 8;


    public VideoViewModel() {
        getVideoList();
    }

    private void getVideoList()
    {
        _isLoading.setValue(true);
        Call<VideoData> client = ApiConfig.getService().getVideo("snippet", typeSearch, maxResult, querySearch);
        client.enqueue(new Callback<VideoData>() {
            @Override
            public void onResponse(@NonNull Call<VideoData> call, @NonNull Response<VideoData> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    VideoData data = response.body();
                    if (data != null && !data.getItems().isEmpty()) {
                        //setVideoDetails(data);
                        _video.setValue(data);
                    } else {
                        _message.setValue("No video");
                    }
                } else {
                    _message.setValue(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoData> call, @NonNull Throwable throwable) {
                _isLoading.setValue(false);
                _message.setValue(throwable.getMessage());
            }
        });
    }

    private void setVideoDetails(VideoData data)
    {
        if (_video == null) return;

        for (int i = 0; i < data.getItems().size(); i++)
        {
            Call<VideoItem> client = ApiConfig.getService().getVideoDetails("contentDetails", data.getItems().get(i).getId().getId());
            int index = i;
            client.enqueue(new Callback<VideoItem>() {
                @Override
                public void onResponse(@NonNull Call<VideoItem> call, @NonNull Response<VideoItem> response) {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        data.setItemAt(index, response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<VideoItem> call, @NonNull Throwable throwable) {
                    return;
                }
            });
        }
    }
}
