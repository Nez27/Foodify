package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.mcdev.quantitizerlibrary.AnimationStyle;
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer;

import java.util.ArrayList;
import java.util.List;

public class FoodDetailActivity extends AppCompatActivity {

    HorizontalQuantitizer horizontalQuantitizer;
    ImageSlider imageSlider;
    ImageView back_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Init Component
        horizontalQuantitizer = findViewById(R.id.quantity_option);
        imageSlider = findViewById(R.id.slider);
        back_image = findViewById(R.id.back_image);

        horizontalQuantitizer.setTextAnimationStyle(AnimationStyle.SLIDE_IN_REVERSE);
        horizontalQuantitizer.setPlusIconBackgroundColor(R.color.white);
        horizontalQuantitizer.setMinusIconBackgroundColor(R.color.white);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/basil-minced-pork-with-rice-fried-egg.jpg?alt=media&token=838ea008-5525-44fb-a5b0-cf77877184a8", null));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/delicious-vietnamese-food-including-pho-ga-noodles-spring-rolls-white-table.jpg?alt=media&token=c19919c7-f69d-4933-b955-3fa627e411a0", null));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/fried-spring-rolls-cutting-board.jpg?alt=media&token=a2f64b21-578c-427f-bbc8-767280ee82e0", null));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/pieces-chicken-fillet-with-mushrooms-stewed-tomato-sauce-with-boiled-broccoli-rice-proper-nutrition-healthy-lifestyle-dietetic-menu-top-view.jpg?alt=media&token=deded3b6-db7b-4a8d-9720-66ddef916d6f", null));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/top-view-fresh-delicious-vietnamese-food-table.jpg?alt=media&token=eec8a996-2a12-4b47-b369-8d4d1e91b20a", null));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}