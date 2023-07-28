package com.android.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import androidx.viewpager2.widget.ViewPager2;

public class AutoScrollRunnable implements Runnable {
    private final ViewPager2 viewPager2;
    private final int itemCount;
    private final long interval;
    private boolean isRunning = false;

    public AutoScrollRunnable(ViewPager2 viewPager2, int itemCount, long interval) {
        this.viewPager2 = viewPager2;
        this.itemCount = itemCount;
        this.interval = interval;
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            viewPager2.postDelayed(this, interval);
        }
    }

    public void stop() {
        isRunning = false;
        viewPager2.removeCallbacks(this);
    }

    @Override
    public void run() {
        int currentItem = viewPager2.getCurrentItem();
        int nextItem = (currentItem + 1) % itemCount;
        viewPager2.setCurrentItem(nextItem, true); // 'true' enables smooth scrolling
        viewPager2.postDelayed(this, interval);
    }
}
