package com.example.karaokeapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.karaokeapp.adapter.VideoAdapter;
import com.example.karaokeapp.databinding.FragmentVideoBinding;
import com.example.karaokeapp.viewModel.VideoViewModel;

public class VideoFragment extends Fragment {
    private FragmentVideoBinding binding;
    private VideoViewModel videoViewModel;
    private final VideoAdapter adapter = new VideoAdapter();

    private FragmentVideoBinding getBinding() {
        return binding;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getBinding().rvVideo.setAdapter(adapter);
        getBinding().rvVideo.setLayoutManager(new LinearLayoutManager(requireContext()));

        videoViewModel.getVideo().observe(getViewLifecycleOwner(), videoData ->
        {
            if (videoData != null && !videoData.getItems().isEmpty())
            {
                adapter.setData(videoData.getItems());
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        binding = FragmentVideoBinding.inflate(inflater, container, false);

        return getBinding().getRoot();
    }
}