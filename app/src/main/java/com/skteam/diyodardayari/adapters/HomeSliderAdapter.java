package com.skteam.diyodardayari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.skteam.diyodardayari.databinding.ItemImageSliderBinding;
import com.skteam.diyodardayari.models.HomeData;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class HomeSliderAdapter extends
        SliderViewAdapter<HomeSliderAdapter.ImageviewHolder> {

    private Context context;
    private List<HomeData> mSliderItems = new ArrayList<>();

    public HomeSliderAdapter(Context context, List<HomeData> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<HomeData> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(HomeData sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public ImageviewHolder onCreateViewHolder(ViewGroup parent) {
        ItemImageSliderBinding binding = ItemImageSliderBinding.inflate(LayoutInflater.from(context));
        return new ImageviewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ImageviewHolder viewHolder, final int position) {

        HomeData data = mSliderItems.get(position);

        Glide.with(viewHolder.itemView)
                .load(data.image)
                .fitCenter()
                .into(viewHolder.binding.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }


    static class ImageviewHolder extends SliderViewAdapter.ViewHolder {
        ItemImageSliderBinding binding;

        public ImageviewHolder(ItemImageSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
