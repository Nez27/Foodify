package com.capstone.foodify.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageSlider = (ImageSlider) view.findViewById(R.id.slider);

        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023773/banner1_dandvd.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023777/banner2_okkqac.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023775/banner3_rklfi6.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023852/banner4_ufsaps.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023778/banner5_b4d1b8.png", null));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        // Inflate the layout for this fragment
        return view;
    }
}