package com.skteam.diyodardayari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.databinding.ItemCategoryBinding;
import com.skteam.diyodardayari.fragments.ShopsFragment;
import com.skteam.diyodardayari.models.Category;
import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.simpleclasses.Variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.DataViewHolder> implements Filterable {
    private static final String TAG = "BlogAdapterTest";
    private Context context;
    private List<HomeData> list, listAll;

    public CategoryAdapter(Context context, List<HomeData> list) {
        this.context = context;
        this.list = list;
        listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
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
            ((HomeActivity) context).showShopListPage();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<HomeData> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (HomeData obj : listAll) {
                    if (obj.title.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(obj);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends HomeData>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public DataViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
