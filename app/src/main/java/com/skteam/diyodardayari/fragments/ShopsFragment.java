package com.skteam.diyodardayari.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.databinding.FragmentProfileBinding;
import com.skteam.diyodardayari.databinding.FragmentShopsBinding;
import com.skteam.diyodardayari.models.Category;
import com.skteam.diyodardayari.simpleclasses.Variables;


public class ShopsFragment extends BaseFragment {
    private FragmentShopsBinding binding;


    public ShopsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShopsBinding.inflate(getLayoutInflater());
        initData();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        singleton.getShopsByCategory(context,binding, Variables.category_id);
        binding.ivBack.setOnClickListener(view13 -> requireActivity().onBackPressed());
    }


}