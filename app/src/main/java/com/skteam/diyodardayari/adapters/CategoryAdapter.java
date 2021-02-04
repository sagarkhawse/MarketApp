package com.skteam.diyodardayari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.databinding.ItemCategoryBinding;
import com.skteam.diyodardayari.fragments.ShopsFragment;
import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.simpleclasses.Variables;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.DataViewHolder> {
    private static final String TAG = "BlogAdapterTest";
    private Context context;
    private List<HomeData> list;


    public CategoryAdapter(Context context, List<HomeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(context),parent,false);
        return new DataViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        HomeData data = list.get(position);
        Glide.with(context).load(data.image).into(holder.binding.ivCategory);
        holder.binding.tvCatTitle.setText(data.title);

        holder.itemView.setOnClickListener(view -> {
        Variables.category_id = data.id;
            ((HomeActivity)context).showShopListPage();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public DataViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
