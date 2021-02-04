package com.skteam.diyodardayari.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.databinding.FragmentProfileBinding;
import com.skteam.diyodardayari.models.Category;
import com.skteam.diyodardayari.simpleclasses.FileUtils;
import com.skteam.diyodardayari.simpleclasses.Functions;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.Variables;

import java.io.File;
import java.util.Objects;


public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;
    private Category category;
    private String my_user_id;
    private static final int IMAGE_FILE_PICKER = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private String profile_pic;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        initData();
        my_user_id = userData.user_id;
        setValues();
        return binding.getRoot();



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String cat_id = userData.category_id;
        singleton.getAllCategories(binding, context, cat_id);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        binding.cvSaveDetails.setOnClickListener(view12 -> {
            if (binding.spinnerCategory.getSelectedItemPosition() == 0) {
                Functions.ShowToast(context, "Please select category");
            } else {
                save();
            }
        });

        binding.imageView.setOnClickListener(view1 -> {
            openImageChooser();
        });

        if (Variables.category_id.equals("")){
            binding.topLyt.setVisibility(View.GONE);
        }
        binding.ivBack.setOnClickListener(view13 -> requireActivity().onBackPressed());
    }

    private void save() {
        singleton.updateUserProfile(userData.user_id,
                binding.etUserName.getText().toString().trim(),
                binding.etWhatsApp.getText().toString().trim(),
                binding.etShopName.getText().toString().trim(),
                binding.etShopAddress.getText().toString().trim(),
                binding.etBusinessDesc.getText().toString().trim(),
                binding.etShopTime.getText().toString().trim(),
                binding.etServices.getText().toString().trim(),
                category.getId(),
                binding.etPhone.getText().toString().trim(),
                binding.etEmail.getText().toString().trim(), context, binding);
    }


    private void setValues() {
        Glide.with(context).load(userData.image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                binding.tvAddImage.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                binding.tvAddImage.setVisibility(View.GONE);
                return false;
            }
        }).into(binding.imageView);


        binding.etUserName.setText(userData.name);
        binding.etPhone.setText(userData.phone);
        binding.etWhatsApp.setText(userData.whatsapp);
        binding.etEmail.setText(userData.email);
        binding.etShopName.setText(userData.shop_name);
        binding.etShopAddress.setText(userData.shop_address);
        binding.etShopTime.setText(userData.shop_time);
        binding.etServices.setText(userData.services);
        binding.etBusinessDesc.setText(userData.business_desc);



    }


    /**
     * Common functions
     */
    private void openImageChooser() {
        if (FileUtils.doesUserHavePermission(context)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            Objects.requireNonNull(getActivity()).startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), IMAGE_FILE_PICKER);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }
    /**
     * get file path
     */
    //On permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openImageChooser();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_FILE_PICKER && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            // MEDIA GALLERY
            profile_pic = FileUtils.getPath(context, selectedImageUri);

            try {
                assert profile_pic != null;
                Uri uri = Uri.fromFile(new File(profile_pic));
                Glide.with(context).load(uri).into(binding.imageView);
              singleton.updateUserImage(context,profile_pic,my_user_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userData = Helper.getLoggedInUser(sharedPreferenceUtil);
        Helper.saveProfileDetails(sharedPreferenceUtil, null);
    }
}