package com.skteam.diyodardayari.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skteam.diyodardayari.MainActivity;
import com.skteam.diyodardayari.activity.HomeActivity;
import com.skteam.diyodardayari.databinding.ItemShopsBinding;
import com.skteam.diyodardayari.models.User;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.DataViewHolder> implements Filterable {
    private static final String TAG = "BlogAdapterTest";
    private Context context;
    private List<User> list,listAll;


    public UserListAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopsBinding binding = ItemShopsBinding.inflate(LayoutInflater.from(context),parent,false);
        return new DataViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        User data = list.get(position);
        Glide.with(context).load(data.image).into(holder.binding.imageView);
        holder.binding.tvTitle.setText(data.shop_name);
//        Spanned spanned = HtmlCompat.fromHtml(data.shop_address, HtmlCompat.FROM_HTML_MODE_COMPACT);
//        holder.binding.tvDescription.setText(spanned);
        holder.binding.tvCategory.setText(data.category_title);

        holder.binding.btnMoreDetail.setOnClickListener(view -> {
            SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);
            Helper.saveProfileDetails(sharedPreferenceUtil,data);
            ((HomeActivity)context).showUserDetailsPage();
        });

        holder.binding.ivPhoneCall.setOnClickListener(view -> {
            String phoneNumber = String.format("tel: %s",
                    data.phone);
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse(phoneNumber));
            if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(dialIntent);
            } else {
                Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
            }
        });

        holder.binding.ivWhatsapp.setOnClickListener(view -> {
            String contact = "+91 "+data.whatsapp; // use country code with your phone number
            String url = "https://api.whatsapp.com/send?phone=" + contact;
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(context, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
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
            List<User> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(listAll);
            }else {
                for (User obj : listAll){
                    if (obj.shop_name.toLowerCase().contains(charSequence.toString().toLowerCase())){
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
list.addAll((Collection<? extends User>) filterResults.values);
notifyDataSetChanged();
        }
    };

    static class DataViewHolder extends RecyclerView.ViewHolder {
        ItemShopsBinding binding;

        public DataViewHolder(ItemShopsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
