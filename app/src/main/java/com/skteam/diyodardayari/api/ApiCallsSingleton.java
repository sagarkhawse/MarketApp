package com.skteam.diyodardayari.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.adapters.CategoryAdapter;
import com.skteam.diyodardayari.adapters.HomeSliderAdapter;
import com.skteam.diyodardayari.adapters.ServiceAdapter;
import com.skteam.diyodardayari.databinding.FragmentHomeBinding;
import com.skteam.diyodardayari.databinding.FragmentMyServiceBinding;
import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.models.User;
import com.skteam.diyodardayari.simpleclasses.Constants;
import com.skteam.diyodardayari.simpleclasses.Functions;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallsSingleton {
    private static final String TAG = "ApiCallsSingletonTest";
    RetrofitApi mService;
    String app_version;


    @SuppressLint("StaticFieldLeak")
    private static ApiCallsSingleton mInstance;


    private ApiCallsSingleton(Context context) {

        mService = RetrofitClient.getAPI();
        app_version = Functions.getAppVersion(context);
    }

    public static ApiCallsSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiCallsSingleton(context);
        }
        return mInstance;
    }


    public void register(String user_id, String email, String name, String phone, String signup_type, Context context) {
        mService.registerUser(user_id,
                email,
                name,
                phone,
                app_version,
                signup_type)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        assert response.body() != null;
                        saveUserDataAndOpenHomeActivity(response.body(), context);
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


    public void loadHomeData(String data, FragmentHomeBinding binding, Context context) {
        mService.homeDataApi(data)
                .enqueue(new Callback<HomeData>() {
                    @Override
                    public void onResponse(@NonNull Call<HomeData> call, @NonNull Response<HomeData> response) {

                        if (response.body() != null) {
                            if (response.body().code.equals(Constants.SUCCESS_CODE)) {
                                HomeData serverResponse = response.body();

                                switch (data) {
                                    case "slider":
                                        HomeSliderAdapter adapter = new HomeSliderAdapter(context, serverResponse.result);
                                        binding.imageSlider.setSliderAdapter(adapter);
                                        break;

                                    case "service":
                                        binding.rvServices.setAdapter(new ServiceAdapter(context, serverResponse.result));
                                        break;


                                    case "category":
                                        CategoryAdapter catAdapter = new CategoryAdapter(context, serverResponse.result);
                                        binding.rvCategories.setAdapter(catAdapter);
                                        break;
                                }

                            } else {
                                Functions.ShowToast(context, response.body().error_msg);
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<HomeData> call, @NonNull Throwable t) {

                    }
                });

    }


    public void myServices(Context context, FragmentMyServiceBinding binding) {
        mService.homeDataApi("service")
                .enqueue(new Callback<HomeData>() {
                    @Override
                    public void onResponse(@NonNull Call<HomeData> call, @NonNull Response<HomeData> response) {
                        HomeData data = response.body();
                        if (data != null) {
                            if (data.code.equals(Constants.SUCCESS_CODE)) {
                                List<HomeData> myServiceDataList = new ArrayList<>();
                                SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);
                                User user = Helper.getLoggedInUser(sharedPreferenceUtil);
                                Log.d(TAG, "onResponse: " + user.user_id);

                                for (HomeData myServiceData : data.result) {
                                    if (user != null) {
                                        if (myServiceData.posted_by.equals(user.user_id)) {
                                            myServiceDataList.add(myServiceData);
                                            Log.d(TAG, "onResponse: " + myServiceDataList);
                                        }
                                    }
                                }
                                binding.tvNoServiceFound.setVisibility(View.GONE);
                                binding.progressBar.setVisibility(View.GONE);
                                binding.rvMyServices.setAdapter(new ServiceAdapter(context, myServiceDataList));
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.tvNoServiceFound.setVisibility(View.VISIBLE);
                                Functions.ShowToast(context, data.error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HomeData> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvNoServiceFound.setVisibility(View.VISIBLE);

                    }
                });
    }


    private void saveUserDataAndOpenHomeActivity(User body, Context context) {
        if (body.code.equals(Constants.SUCCESS_CODE)) {
            //save user data
            SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);
            Log.d(TAG, "saveUserDataAndOpenHomeActivity: " + body.result.get(0));
            Helper.setLoggedInUser(sharedPreferenceUtil, body.result.get(0));
            context.startActivity(new Intent(context, HomeActivity.class));
        } else {
            Functions.ShowToast(context, body.error_msg);
        }

    }

}
