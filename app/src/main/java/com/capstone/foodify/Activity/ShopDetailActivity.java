package com.capstone.foodify.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Adapter.FoodShopAdapter;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopDetailActivity extends AppCompatActivity {

    private static int CURRENT_PAGE;
    private static int PAGE_SIZE = 8;
    private static String SORT_BY = "name";
    private static String SORT_DIR = "asc";
    private static boolean LAST_PAGE = false;
    private int shopId;
    private List<Food> listFood = new ArrayList<>();
    TextView content_description_text_view, student_shop_text_view, end_of_list_text_view;
    ImageView imageShop, back_image;
    RecyclerView recycler_view_food_shop;
    FoodShopAdapter adapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private Shop shop;
    private ConstraintLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        //Init Component
        imageShop = findViewById(R.id.image_shop);
        recycler_view_food_shop = findViewById(R.id.recycler_view_food_shop);
        back_image = findViewById(R.id.back_image);
        adapter = new FoodShopAdapter(this);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        content_description_text_view = findViewById(R.id.content_description_text_view);
        student_shop_text_view = findViewById(R.id.student_shop_text_view);
        nestedScrollView = findViewById(R.id.food_detail);
        progressBar = findViewById(R.id.progress_bar);
        end_of_list_text_view = findViewById(R.id.end_of_list_text);
        progressLayout = findViewById(R.id.progress_layout);

        //Get data
        if (getIntent() != null)
            shopId = getIntent().getIntExtra("ShopId", 0);

        //Set the first page after call api
        CURRENT_PAGE = 0;
        getDetailShop(shopId);
        nestedScrollView.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_food_shop.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recycler_view_food_shop.addItemDecoration(itemDecoration);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        //When user scroll to bottom, load more data
                        dataLoadMore();
                    }
                }
            });
        }

    }

    private void getListFood(int shopId){
        FoodApi.apiService.listFoodByShopId(shopId, CURRENT_PAGE++, PAGE_SIZE, SORT_BY, SORT_DIR).enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                Foods tempFood = response.body();

                if(tempFood == null)
                    return;

                //Init last page from api
                LAST_PAGE = tempFood.getPage().isLast();

                //Add food to list
                listFood.addAll(tempFood.getProducts());

                adapter.setData(listFood);
                recycler_view_food_shop.setAdapter(adapter);

                initData(shop);

                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                //Check internet connection
                Common.showNotificationError(getBaseContext(), ShopDetailActivity.this);
                progressLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getDetailShop(int shopId) {
        FoodApi.apiService.detailShop(shopId).enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                Shop tempShop = response.body();

                //Init shop data to "local data" shop;
                if(tempShop != null)
                    shop = tempShop;

                getListFood(shopId);
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                Common.showNotificationError(getBaseContext(), ShopDetailActivity.this);
            }
        });
    }

    private void initData(Shop shop) {
        //Init data
        Picasso.get().load(shop.getImageUrl()).into(imageShop);
        collapsingToolbarLayout.setTitle(shop.getName());
        content_description_text_view.setText(shop.getDescription());

        //If shop is studentShop, enable tag student shop
        if(shop.isStudent()){
            student_shop_text_view.setVisibility(View.VISIBLE);
        }

        //If list data don't have pagination
        if(LAST_PAGE)
            hideProgressBarAndShowEndOfListText();

        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void hideProgressBarAndShowEndOfListText(){
        progressBar.setVisibility(View.GONE);
        end_of_list_text_view.setVisibility(View.VISIBLE);
    }

    private void dataLoadMore() {
        if(!LAST_PAGE){
            getListFood(shopId);
        } else {
            hideProgressBarAndShowEndOfListText();
        }
    }
}