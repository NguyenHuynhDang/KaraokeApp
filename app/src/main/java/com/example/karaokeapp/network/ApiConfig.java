package com.example.karaokeapp.network;

import androidx.annotation.NonNull;

import com.example.karaokeapp.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfig {
    private static ApiServices apiServices;

    public static void createService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        HttpUrl url = originalRequest.url().newBuilder()
                                .addQueryParameter("key", BuildConfig.YOUTUBE_API_KEY)
                                .build();
                        Request newRequest = originalRequest.newBuilder()
                                .url(url)
                                .build();
                        return chain.proceed(newRequest);
                    }
                });

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiServices = retrofit.create(ApiServices.class);
    }

    public static ApiServices getService()
    {
        if (apiServices == null)
            createService();
        return apiServices;
    }
}


