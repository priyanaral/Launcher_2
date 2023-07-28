package com.android.myapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<AppInfo> appList;
    private ViewPager2 viewPager2;
    private AppItemClickListener appItemClickListener;
    SliderAdapter(List<AppInfo> appList, ViewPager2 viewPager2,AppItemClickListener appItemClickListener) {
        this.appList = appList;
        this.viewPager2 = viewPager2;
        this.appItemClickListener = appItemClickListener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_app_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.appIconImageView.setImageDrawable(appInfo.getIcon());
        holder.appNameTextView.setText(appInfo.getAppName());

        if (position == appList.size()-2){
            viewPager2.post(runnable);
        }
        holder.appIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = appInfo.getPackageName();
                appItemClickListener.onAppItemClick(packageName);
            }
        });

    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public interface AppItemClickListener {
        void onAppItemClick(String packageName);
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView appIconImageView;
        private TextView appNameTextView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
        }
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            appList.addAll(appList);
            notifyDataSetChanged();
        }
    };
}
