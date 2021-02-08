package com.skteam.diyodardayari.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.service.autofill.UserData;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.adapters.CategoryAdapter;
import com.skteam.diyodardayari.adapters.HomeSliderAdapter;
import com.skteam.diyodardayari.adapters.ServicesAdapter;
import com.skteam.diyodardayari.adapters.UserListAdapter;
import com.skteam.diyodardayari.databinding.FragmentHomeBinding;
import com.skteam.diyodardayari.databinding.FragmentMyServiceBinding;
import com.skteam.diyodardayari.databinding.FragmentProfileBinding;
import com.skteam.diyodardayari.databinding.FragmentShopsBinding;
import com.skteam.diyodardayari.fragments.HomeFragment;
import com.skteam.diyodardayari.models.Category;
import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.models.User;
import com.skteam.diyodardayari.simpleclasses.Constants;
import com.skteam.diyodardayari.simpleclasses.Functions;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public void getAllCategories(FragmentProfileBinding binding, Context context, String cat_id) {
        mService.homeDataApi("category")
                .enqueue(new Callback<HomeData>() {
                    @Override
                    public void onResponse(@NonNull Call<HomeData> call, @NonNull Response<HomeData> response) {
                        HomeData data = response.body();
                        if (data != null) {
                            if (data.code.equals(Constants.SUCCESS_CODE)) {

                                ArrayList<Category> arrayList = new ArrayList<>();
                                arrayList.clear();
                                arrayList.add(new Category("0", "select category"));
                                for (HomeData catData : data.result) {
                                    arrayList.add(new Category(catData.id, catData.title));
                                }

                                ArrayAdapter<Category> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayList);
                                binding.spinnerCategory.setAdapter(adapter);

                                for (Category dta : arrayList) {
                                    if (dta.getId().equals(cat_id)) {
//                                        binding.spinnerCategory.setSelection(arrayList.indexOf(dta.getId()));
                                        binding.spinnerCategory.setSelection(adapter.getPosition(new Category(dta.getId(), dta.getName())));

                                    }
                                }

                            } else {
                                Functions.ShowToast(context, data.error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HomeData> call, @NonNull Throwable t) {

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
                                        binding.imageSlider.setAutoCycle(true);
                                        binding.imageSlider.startAutoCycle();
                                        break;

//                                    case "service":
//                                        binding.rvServices.setAdapter(new UserListAdapter(context, serverResponse.result));
//                                        break;


                                    case "category":
                                        CategoryAdapter catAdapter = new CategoryAdapter(context, serverResponse.result);
                                        binding.rvCategories.setAdapter(catAdapter);
                                        binding.etSearchView.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                catAdapter.getFilter().filter(charSequence);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {

                                            }
                                        });
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

    public void getShopsByCategory(Context context, FragmentShopsBinding binding, String category_id) {
        mService.userList()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User resBody = response.body();
                        if (resBody != null) {
                            if (resBody.code.equals(Constants.SUCCESS_CODE)) {

                                ArrayList<User> shopList = new ArrayList<>();
                                for (User userData : resBody.result) {
                                    if (userData.category_id.equals(category_id)) {
                                        shopList.add(userData);
                                    }
                                }

                                UserListAdapter adapter = new UserListAdapter(context, shopList);
                                binding.rvShops.setAdapter(adapter);
                                binding.progressBar.setVisibility(View.GONE);
                                binding.etSearchView.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        adapter.getFilter().filter(charSequence);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {

                                    }
                                });

                            } else {
                                Functions.ShowToast(context, resBody.error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

//    public void services(Context context, FragmentMyServiceBinding binding) {
//        mService.homeDataApi("service")
//                .enqueue(new Callback<HomeData>() {
//                    @Override
//                    public void onResponse(@NonNull Call<HomeData> call, @NonNull Response<HomeData> response) {
//                        HomeData data = response.body();
//                        if (data != null) {
//                            if (data.code.equals(Constants.SUCCESS_CODE)) {
//
//                                binding.tvNoServiceFound.setVisibility(View.GONE);
//                                binding.progressBar.setVisibility(View.GONE);
//                                binding.rvMyServices.setAdapter(new ServicesAdapter(context, data.result));
//                            } else {
//                                binding.progressBar.setVisibility(View.GONE);
//                                binding.tvNoServiceFound.setVisibility(View.VISIBLE);
//                                Functions.ShowToast(context, data.error_msg);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<HomeData> call, @NonNull Throwable t) {
//                        Log.d(TAG, "onFailure: " + t.getMessage());
//                        binding.progressBar.setVisibility(View.GONE);
//                        binding.tvNoServiceFound.setVisibility(View.VISIBLE);
//
//                    }
//                });
//    }






    public void updateUserProfile(String user_id, String user_name, String whatsapp,
                                  String shop_name, String shop_address,
                                  String business_desc,
                                  String shop_time, String service,
                                  String category_id, String phone,
                                  String email, Context context, FragmentProfileBinding binding) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.lytSaveDetail.setVisibility(View.GONE);
        mService.updateUserData(user_id, user_name, whatsapp, shop_name, shop_address, business_desc, shop_time
                , service, category_id, phone, email)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User resBody = response.body();
                        if (resBody != null) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.lytSaveDetail.setVisibility(View.VISIBLE);
                            if (resBody.code.equals(Constants.SUCCESS_CODE)) {
                                //user data updated

                                //get all details of users
                                getMyData(user_id,context);
                                ((HomeActivity) context).setFragment(new HomeFragment(), "home_fragment");
                                ((HomeActivity) context).binding.bottomNav.setVisibility(View.VISIBLE);
                            } else {
                                Functions.ShowToast(context, resBody.error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Functions.ShowToast(context, t.getMessage());
                        binding.progressBar.setVisibility(View.GONE);
                        binding.lytSaveDetail.setVisibility(View.VISIBLE);
                    }
                });
    }


    public void loadUserList(Context context, FragmentHomeBinding binding) {
        mService.userList()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        List<User> list;
                        User resBody = response.body();
                        if (resBody != null) {
                            if (resBody.code.equals(Constants.SUCCESS_CODE)) {
                                list = resBody.result;
                                Collections.shuffle(list);
                                binding.rvUsers.setAdapter(new UserListAdapter(context, list));
                            } else {
                                Functions.ShowToast(context, resBody.error_msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Functions.ShowToast(context, t.getMessage());
                    }
                });
    }


    public void updateUserImage(Context context, String img_path, String userid) {
        MultipartBody.Part dp = Functions.prepareFilePart(context, "image", img_path);
        RequestBody user_id = Functions.createPartFromString(userid);
        mService.updateUserImage(dp, user_id)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.body() != null) {
                            if (response.body().code.equals(Constants.SUCCESS_CODE)) {
                                Functions.ShowToast(context, "Image Updated Successfully");
                                //get all details of users
                         getMyData(userid,context);
                            } else {
                                Functions.ShowToast(context, "Please try again");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Functions.ShowToast(context, t.getMessage());
                    }
                });
    }

    public void getMyData(String user_id, Context context){
        mService.getMyData(user_id)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User body = response.body();
                        if (body!=null){
                            if (body.code.equals(Constants.SUCCESS_CODE)) {
                                //save user data
                                SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);
                                Helper.setLoggedInUser(sharedPreferenceUtil, body.result.get(0));
                            } else {
                                Functions.ShowToast(context, body.error_msg);
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
Functions.ShowToast(context,t.getMessage());
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
