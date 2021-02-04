package com.skteam.diyodardayari.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private Context context;
    private FragmentHomeBinding binding;
    private ApiCallsSingleton singleton;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        context = getContext();
        singleton = ApiCallsSingleton.getInstance(context);
        singleton.loadHomeData("category", binding,context);
        singleton.loadHomeData("slider", binding,context);
        singleton.loadUserList(context,binding);

        return view;
    }

}