package com.skteam.diyodardayari.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.databinding.FragmentHomeBinding;
import com.skteam.diyodardayari.databinding.FragmentMyServiceBinding;

public class MyServiceFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private Context context;
    private FragmentMyServiceBinding binding;
    private ApiCallsSingleton singleton;

    public MyServiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyServiceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        context = getContext();
        singleton = ApiCallsSingleton.getInstance(context);
        singleton.myServices(context, binding);
        return view;
    }
}