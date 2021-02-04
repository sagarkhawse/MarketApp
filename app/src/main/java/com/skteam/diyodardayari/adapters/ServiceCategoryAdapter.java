package com.skteam.diyodardayari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.databinding.ItemServiceCategoryBinding;
import com.skteam.diyodardayari.databinding.ItemServicesBinding;
import com.skteam.diyodardayari.fragments.AllServicesFragment;
import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.models.Services;

import java.util.List;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.DataViewHolder> {
    private static final String TAG = "BlogAdapterTest";
    private Context context;
    private List<Services> list;


    public ServiceCategoryAdapter(Context context, List<Services> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceCategoryBinding binding = ItemServiceCategoryBinding.inflate(LayoutInflater.from(context),parent,false);
        return new DataViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Services data = list.get(position);
        holder.binding.tvCatTitle.setText(data.cat_title);
        Glide.with(context).load(data.image).into(holder.binding.ivCategory);
        holder.binding.btnViewAll.setOnClickListener(view -> {
            ((HomeActivity)context).showAllServicePage(data.id);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemServiceCategoryBinding binding;

        public DataViewHolder(ItemServiceCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
