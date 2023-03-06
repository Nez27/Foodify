package com.capstone.foodify.Fragment.Order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 1:
                return new ShipOrderFragment();
            case 2:
                return new CompleteOrderFragment();
            default:
                return new ProgressOderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
