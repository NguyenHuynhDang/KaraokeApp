package com.example.karaokeapp.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.karaokeapp.R;
import com.example.karaokeapp.adapter.VideoAdapter;
import com.example.karaokeapp.databinding.FragmentVideoBinding;
import com.example.karaokeapp.viewModel.VideoViewModel;

public class VideoFragment extends Fragment {
    private FragmentVideoBinding binding;
    private VideoViewModel videoViewModel;
    private final VideoAdapter adapter = new VideoAdapter();
    private boolean isLoading;

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

        videoViewModel.IsLoading().observe(getViewLifecycleOwner(), it ->
        {
            isLoading = it;
            if (isLoading)
                binding.progressBar.setVisibility(View.VISIBLE);
            else
                binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        binding = FragmentVideoBinding.inflate(inflater, container, false);

        return getBinding().getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_video));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()){
                    if (!query.contains("karaoke")) query += " karaoke";
                    videoViewModel.setQuerySearch(query);
                    adapter.clearAll();
                    videoViewModel.getVideoList();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    videoViewModel.setQuerySearch("karaoke");
                    adapter.clearAll();
                    videoViewModel.getVideoList();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}