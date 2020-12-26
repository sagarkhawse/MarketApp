package com.skteam.diyodardayari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.diyodardayari.databinding.ItemCategoryBinding;
import com.skteam.diyodardayari.databinding.ItemServiceBinding;
import com.skteam.diyodardayari.models.HomeData;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.DataViewHolder> {
    private static final String TAG = "BlogAdapterTest";
    private Context context;
    private List<HomeData> list;


    public ServiceAdapter(Context context, List<HomeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceBinding binding = ItemServiceBinding.inflate(LayoutInflater.from(context));
        return new DataViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        HomeData data = list.get(position);
        Glide.with(context).load(data.image).into(holder.binding.imageView);
        holder.binding.tvTitle.setText(data.service_title);
        Spanned spanned = HtmlCompat.fromHtml(data.description, HtmlCompat.FROM_HTML_MODE_COMPACT);
        holder.binding.tvDescription.setText(spanned);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemServiceBinding binding;

        public DataViewHolder(ItemServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
