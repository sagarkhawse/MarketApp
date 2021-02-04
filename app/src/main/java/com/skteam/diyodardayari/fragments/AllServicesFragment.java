package com.skteam.diyodardayari.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.adapters.ServiceCategoryAdapter;
import com.skteam.diyodardayari.adapters.ServicesAdapter;
import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.api.RetrofitApi;
import com.skteam.diyodardayari.api.RetrofitClient;
import com.skteam.diyodardayari.databinding.FragmentAllServicesBinding;
import com.skteam.diyodardayari.databinding.FragmentMyServiceBinding;
import com.skteam.diyodardayari.models.Services;
import com.skteam.diyodardayari.simpleclasses.Constants;
import com.skteam.diyodardayari.simpleclasses.Functions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllServicesFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private Context context;
    private FragmentAllServicesBinding binding;
    private ApiCallsSingleton singleton;
    private RetrofitApi mService;



    public AllServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllServicesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        context = getContext();
        mService = RetrofitClient.getAPI();
//        String cat_id = getArguments().getString("service_cat_id");
        mService.fetchServices("services")
                .enqueue(new Callback<Services>() {
                    @Override
                    public void onResponse(@NonNull Call<Services> call, @NonNull Response<Services> response) {
                        if (response.body() != null) {

                            if (response.body().code.equals(Constants.SUCCESS_CODE)){
                                binding.rvMyServices.setAdapter(new ServicesAdapter(context,response.body().result));
                                binding.progressBar.setVisibility(View.GONE);
                            }else{
                                Functions.ShowToast(context,response.body().error_msg);
                            }

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Services> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
        return view;
    }
}