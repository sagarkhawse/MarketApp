package com.skteam.diyodardayari.activity;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.api.RetrofitApi;
import com.skteam.diyodardayari.api.RetrofitClient;
import com.skteam.diyodardayari.models.User;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.SharedPreferenceUtil;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "DebugTest";
    public Activity activity;
    public User userData;
    public SharedPreferenceUtil sharedPreferenceUtil;
    public FirebaseAuth mAuth;
    public ApiCallsSingleton singleton;
    public void initUserData (){
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        userData = Helper.getLoggedInUser(sharedPreferenceUtil);
        activity = this;
      singleton = ApiCallsSingleton.getInstance(activity);
        FirebaseApp.initializeApp(activity);
        mAuth = FirebaseAuth.getInstance();
        FirebaseAnalytics.getInstance(this);
    }

}
