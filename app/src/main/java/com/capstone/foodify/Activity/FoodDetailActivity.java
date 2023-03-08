package com.capstone.foodify.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Basket.Basket;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.ImageFood;
import com.capstone.foodify.Model.Review.Review;
import com.capstone.foodify.Model.Review.ReviewAdapter;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.mcdev.quantitizerlibrary.AnimationStyle;
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer;
import com.mcdev.quantitizerlibrary.QuantitizerListener;
import com.willy.ratingbar.RotationRatingBar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {

    private HorizontalQuantitizer horizontalQuantitizer;
    private ImageSlider imageSlider;
    private ImageView back_image, favourite_icon;
    private String foodId = "";
    private Food food;
    private static final List<String> imageFood = new ArrayList<>();
    private TextView foodName_txt, shopName_txt, discount_txt, price_txt, total_txt, countRating_txt, description_content_txt;
    private ConstraintLayout content_view;
    private ProgressBar progressBar;
    private Button add_to_basket_button;
    private View line;
    private float totalPrice, price;
    private RecyclerView recyclerView_review;
    private ReviewAdapter reviewAdapter;
    private Button rating_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Init Component
        horizontalQuantitizer = findViewById(R.id.quantity_option);
        imageSlider = findViewById(R.id.slider);
        back_image = findViewById(R.id.back_image);
        foodName_txt = findViewById(R.id.food_name_text_view);
        shopName_txt = findViewById(R.id.shop_name_text_view);
        discount_txt = findViewById(R.id.discount);
        price_txt = findViewById(R.id.price);
        progressBar = findViewById(R.id.progress_bar);
        add_to_basket_button = findViewById(R.id.add_to_basket_button);
        favourite_icon = findViewById(R.id.favorite_icon);
        line = findViewById(R.id.line);
        total_txt = findViewById(R.id.total_text_view);
        countRating_txt = findViewById(R.id.count_rating_text_view);
        description_content_txt = findViewById(R.id.content_description_text_view);
        recyclerView_review = findViewById(R.id.recycler_view_review);
        rating_button = findViewById(R.id.rating_button);
        content_view = findViewById(R.id.content_view);

        reviewAdapter = new ReviewAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView_review.setLayoutManager(linearLayoutManager);

        reviewAdapter.setData(getListReview());
        recyclerView_review.setAdapter(reviewAdapter);


        hideUI();

        //Customize UI Horizontal Quantitizer
        horizontalQuantitizer.setTextAnimationStyle(AnimationStyle.SLIDE_IN_REVERSE);
        horizontalQuantitizer.setPlusIconBackgroundColor(R.color.white);
        horizontalQuantitizer.setMinusIconBackgroundColor(R.color.white);

        horizontalQuantitizer.setQuantitizerListener(new QuantitizerListener() {
            @Override
            public void onIncrease() {
            }

            @Override
            public void onDecrease() {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onValueChanged(int i) {

                if(totalPrice >= 0)
                    totalPrice = price * i;
                add_to_basket_button.setText("Add to basket - " + Common.changeCurrencyUnit(totalPrice));
            }
        });

        //Get data
        if(getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");

        getImageFoodById(foodId);

        //Turn previous action when click
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Open dialog review
        rating_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReviewDialog(Gravity.CENTER);
            }
        });

        //Add food to basket
        add_to_basket_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(horizontalQuantitizer.getValue() > 0){
                    Basket foodInBasket = getFoodExistInBasket(food.getId());
                    if(foodInBasket == null){
                        //If item is not exist in basket
                        Common.LIST_BASKET_FOOD.add(new Basket(food.getId(), imageFood.get(0), food.getName(), food.getPrice(), "Shop Name",
                                String.valueOf(horizontalQuantitizer.getValue()), "0"));
                        Toast.makeText(FoodDetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FoodDetailActivity.this, MainActivity.class));
                    } else {
                        //If item is exist in basket

                        for(int i = 0; i < Common.LIST_BASKET_FOOD.size(); i++){
                            String foodId = Common.LIST_BASKET_FOOD.get(i).getId();
                            //Find foodId exist
                            if(foodId.equals(food.getId())){
                                //Get quantity food from basket
                                String quantity = Common.LIST_BASKET_FOOD.get(i).getQuantity();

                                //Change quantity food from basket
                                int quantityInt = Integer.parseInt(quantity) + horizontalQuantitizer.getValue();
                                Common.LIST_BASKET_FOOD.get(i).setQuantity(String.valueOf(quantityInt));
                                break;
                            }
                        }

                        Toast.makeText(FoodDetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FoodDetailActivity.this, MainActivity.class));
                    }

                } else {
                    Toast.makeText(FoodDetailActivity.this, "Bạn chưa chọn số lượng đồ ăn cần đặt!", Toast.LENGTH_SHORT).show();   
                }
            }
        });

        //Move to shop detail screen when onClick
        shopName_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodDetailActivity.this, ShopDetailActivity.class));
            }
        });
    }

    private Basket getFoodExistInBasket(String foodId){
        return Common.LIST_BASKET_FOOD.stream().filter(food -> foodId.equals(food.getId())).findFirst().orElse(null);
    }

    private List<Review> getListReview(){
        ArrayList<Review> listReview = new ArrayList<>();

        listReview.add(new Review("User A", 5, "Very Good!"));
        listReview.add(new Review("User B", 1, "Bad!"));
        listReview.add(new Review("User C", 4, "Delicious!"));
        listReview.add(new Review("User D", 3, "OK!"));
        listReview.add(new Review("User E", 5, "Awesome!"));

        return listReview;
    }

    private void hideUI() {
        content_view.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void showUI() {
        content_view.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
    }

    private void initData(){
        price = Float.parseFloat(food.getPrice());

        foodName_txt.setText(food.getName());
        shopName_txt.setText("Shop Name");
        price_txt.setText(Common.changeCurrencyUnit(Float.parseFloat(food.getPrice())));
        discount_txt.setText("-" + food.getDiscount() + "%");
        description_content_txt.setText(food.getDescription());
        countRating_txt.setText(food.getReviewCount() + " rating");

        add_to_basket_button.setText("ADD TO BASKET - " + Common.changeCurrencyUnit(0));

        List<SlideModel> slideModels = new ArrayList<>();
        if(imageFood != null){
            for(String imageUrl: imageFood)
                slideModels.add(new SlideModel(imageUrl, null));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

    }

    private void getFoodById(String foodId){
        FoodApi.apiService.detailFood(foodId).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Food tempFood = response.body();
                if(tempFood != null)
                    food = tempFood;

                initData();
                showUI();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi Food" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getImageFoodById(String foodId) {
        FoodApi.apiService.getImageFoodById(foodId).enqueue(new Callback<List<ImageFood>>() {
            @Override
            public void onResponse(Call<List<ImageFood>> call, Response<List<ImageFood>> response) {
                List<ImageFood> imageFoodData = response.body();
                if (imageFoodData != null){
                    imageFood.clear();
                    for(ImageFood imageFoodTemp: imageFoodData){
                        imageFood.add(imageFoodTemp.getImageUrl());
                    }
                }

                getFoodById(foodId);
            }

            @Override
            public void onFailure(Call<List<ImageFood>> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi Image" + t, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openReviewDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);

        Window window = dialog.getWindow();
        if(window == null)
            return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        EditText review_content = dialog.findViewById(R.id.edit_text_review);
        RotationRatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Button confirm = dialog.findViewById(R.id.confirm_button);
        Button cancel = dialog.findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //TO DO

        dialog.show();
    }
}