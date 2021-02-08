package com.skteam.diyodardayari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

import com.skteam.diyodardayari.databinding.ItemServicesBinding;
import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.models.Services;
import com.skteam.diyodardayari.simpleclasses.Variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.DataViewHolder> implements Filterable {
    private static final String TAG = "BlogAdapterTest";
    private Context context;
    private List<Services> list;
    private List<Services> listAll;


    public ServicesAdapter(Context context, List<Services> list) {
        this.context = context;
        this.list = list;
        listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServicesBinding binding = ItemServicesBinding.inflate(LayoutInflater.from(context),parent,false);
        return new DataViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Services data = list.get(position);
        holder.binding.tvServiceTitle.setText(data.service_title);
        holder.binding.tvServiceContact.setText(data.contact_number);
        holder.binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                String phoneNumber = String.format("tel: %s",
                        holder.binding.tvServiceContact.getText().toString());
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse(phoneNumber));
                if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(dialIntent);
                } else {
                    Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
                }
            }
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
            List<Services> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filteredList.addAll(listAll);
            }else{
                for (Services obj : listAll){
                    if (obj.service_title.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(obj);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
list.clear();
list.addAll((Collection<? extends Services>) filterResults.values);
notifyDataSetChanged();

        }
    };

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemServicesBinding binding;

        public DataViewHolder(ItemServicesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
