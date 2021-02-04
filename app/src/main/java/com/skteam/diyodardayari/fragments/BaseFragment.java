package com.skteam.diyodardayari.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.api.RetrofitApi;
import com.skteam.diyodardayari.api.RetrofitClient;
import com.skteam.diyodardayari.models.User;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.SharedPreferenceUtil;


public class BaseFragment extends Fragment {

    public static final String TAG = "DebugTest";
    public Context context;
    public User userData, profileData;
    public SharedPreferenceUtil sharedPreferenceUtil;
    public FirebaseAuth mAuth;
    public ApiCallsSingleton singleton;

    public void initData() {
        context = getContext();
        sharedPreferenceUtil = new SharedPreferenceUtil(context);

        profileData = Helper.getProfileDetails(sharedPreferenceUtil);
        userData = Helper.getLoggedInUser(sharedPreferenceUtil);

        singleton = ApiCallsSingleton.getInstance(context);
        FirebaseApp.initializeApp(context);
        mAuth = FirebaseAuth.getInstance();
    }

}