package com.capstone.foodify.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.capstone.foodify.Model.Image.Image;
import com.capstone.foodify.Model.Review.Review;
import com.capstone.foodify.Adapter.ReviewAdapter;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.mcdev.quantitizerlibrary.AnimationStyle;
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer;
import com.mcdev.quantitizerlibrary.QuantitizerListener;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.willy.ratingbar.RotationRatingBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {

    private HorizontalQuantitizer horizontalQuantitizer;
    private ImageSlider imageSlider;
    private ImageView back_image, favourite_icon;
    private String foodId = "";
    private Food food;
    private TextView foodName_txt, shopName_txt, discount_txt, price_txt, total_txt, countRating_txt, description_content_txt;
    private ConstraintLayout content_view;
    private Button add_to_basket_button;
    private float totalPrice, price;
    private RecyclerView recyclerView_review;
    private ReviewAdapter reviewAdapter;
    private Button rating_button;
    private PopupDialog popupDialog;


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
        add_to_basket_button = findViewById(R.id.add_to_basket_button);
        favourite_icon = findViewById(R.id.favorite_icon);
        total_txt = findViewById(R.id.total_text_view);
        countRating_txt = findViewById(R.id.count_rating_text_view);
        description_content_txt = findViewById(R.id.content_description_text_view);
        recyclerView_review = findViewById(R.id.recycler_view_review);
        rating_button = findViewById(R.id.rating_button);
        content_view = findViewById(R.id.content_view);

        popupDialog = PopupDialog.getInstance(this);

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

                if (totalPrice >= 0)
                    totalPrice = price * i;
                add_to_basket_button.setText("Add to basket - " + Common.changeCurrencyUnit(totalPrice));
            }
        });

        //Get data
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");

        getFoodById(foodId);

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
                    Basket foodInBasket = Common.getFoodExistInBasket(food.getId());
                    if(foodInBasket == null){
                        //If item is not exist in basket

                        //Check food image is null or not
                        String imageTemp = null;
                        if(food.getImages().size() > 0)
                            imageTemp = food.getImages().get(0).getImageUrl();

                        Common.LIST_BASKET_FOOD.add(new Basket(food.getId(), imageTemp, food.getName(), food.getCost(), food.getShop().getName(),
                                String.valueOf(horizontalQuantitizer.getValue()), food.getDiscountPercent()));
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

                    }
                    Toast.makeText(FoodDetailActivity.this, "Đã thêm " + food.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FoodDetailActivity.this, MainActivity.class));

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

    private List<Review> getListReview() {
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

        popupDialog.setStyle(Styles.PROGRESS).setProgressDialogTint(getResources().getColor(R.color.primaryColor, null))
                .setCancelable(false).showDialog();
    }

    private void showUI() {
        content_view.setVisibility(View.VISIBLE);

        popupDialog.dismissDialog();
    }

    private void initData() {
        //Set data price to calculate
        price = food.getCost();

        foodName_txt.setText(food.getName());
        shopName_txt.setText(food.getShop().getName());
        price_txt.setText(Common.changeCurrencyUnit(food.getCost()));
        description_content_txt.setText(food.getDescription());
        countRating_txt.setText(food.getReviewCount() + " rating");

        add_to_basket_button.setText("ADD TO BASKET - " + Common.changeCurrencyUnit(0));

        //Set slider image food
        List<SlideModel> slideModels = new ArrayList<>();
        //Get image food
        if (food.getImages().size() > 0) {
            for (Image imageUrl : food.getImages())
                slideModels.add(new SlideModel(imageUrl.getImageUrl(), null));
        } else {
            slideModels.add(new SlideModel(R.drawable.dish, null));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //Show discount percent when value greater than 0
        if (food.getDiscountPercent() > 0) {
            discount_txt.setText("-" + food.getDiscountPercent() + "%");
        } else {
            discount_txt.setVisibility(View.GONE);
        }
    }

    private void getFoodById(String foodId) {
        FoodApi.apiService.detailFood(foodId).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Food tempFood = response.body();
                if (tempFood != null)
                    food = tempFood;

                initData();
                showUI();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Common.showNotificationError(getBaseContext(), FoodDetailActivity.this);
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

        //TODO: Update review dialog

        dialog.show();
    }
}