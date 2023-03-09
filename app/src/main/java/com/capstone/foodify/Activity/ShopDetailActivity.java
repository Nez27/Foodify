package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.FoodFavoriteAdapter;
import com.capstone.foodify.Model.Food.FoodShopAdapter;
import com.capstone.foodify.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopDetailActivity extends AppCompatActivity {

    private List<Food> listFood = new ArrayList<>();
    ImageView imageShop, back_image;
    RecyclerView recycler_view_food_shop;
    FoodShopAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        //Init Component
        imageShop = findViewById(R.id.image_shop);
        recycler_view_food_shop = findViewById(R.id.recycler_view_food_shop);
        back_image = findViewById(R.id.back_image);
        adapter = new FoodShopAdapter();

        //Init temp data
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/Free%20Food%20Advertising%20Banner%20Template.jpg?alt=media&token=8b836fd7-f18f-46c6-a14f-27680e51a1d5").into(imageShop);

//        for(int i = 1; i <= 10; i++){
//            listFood.add(new Food("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxleHBsb3JlLWZlZWR8MXx8fGVufDB8fHx8&w=1000&q=80", "Food " + i, "50.000đ"));
//        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_food_shop.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recycler_view_food_shop.addItemDecoration(itemDecoration);

        adapter.setData(listFood);
        recycler_view_food_shop.setAdapter(adapter);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}