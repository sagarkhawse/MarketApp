package com.skteam.diyodardayari.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.databinding.FragmentProfileBinding;
import com.skteam.diyodardayari.databinding.FragmentUserProfileBinding;
import com.skteam.diyodardayari.models.Category;


public class UserProfileFragment extends BaseFragment {
    private FragmentUserProfileBinding binding;
    private String my_user_id;


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(getLayoutInflater());

        initData();
        my_user_id = userData.user_id;
        if (profileData != null) {
            userData = profileData;
        }
        setValues();
        return binding.getRoot();


    }



    private void setValues() {
        Glide.with(context).load(userData.image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(binding.ivUser);


        binding.tvUserName.setText(userData.name.trim());
        binding.tvCategoryName.setText(userData.category_title.trim());
        binding.tvUserEmail.setText(userData.email.trim());
        binding.tvShopName.setText(userData.shop_name.trim());
        binding.tvAddress.setText(userData.shop_address.trim());
        binding.tvBusinessDescription.setText(userData.business_desc.trim());


        binding.tvCall.setOnClickListener(view -> {
            String phoneNumber = String.format("tel: %s",
                    userData.phone);
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse(phoneNumber));
            if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(dialIntent);
            } else {
                Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
            }
        });

       binding.tvWhatsapp.setOnClickListener(view -> {
            String contact = "+91 "+userData.whatsapp; // use country code with your phone number
            String url = "https://api.whatsapp.com/send?phone=" + contact;
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(context, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });



//        if (userData.user_id.equals(my_user_id)) {
//            binding.cvSaveDetails.setVisibility(View.VISIBLE);
//        } else {
//            binding.cvSaveDetails.setVisibility(View.GONE);
//        }


    }
}