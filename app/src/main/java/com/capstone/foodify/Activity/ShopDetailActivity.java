package com.capstone.foodify.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private static final String TAG = "ShopDetailActivity";
    private static int CURRENT_PAGE;
    private static int PAGE_SIZE = 8;
    private static String SORT_BY = "name";
    private static String SORT_DIR = "asc";
    private static boolean LAST_PAGE = false;
    private int shopId;
    private List<Food> listFood = new ArrayList<>();
    private TextView content_description_text_view, student_shop_text_view, end_of_list_text_view, txt_shop_name;
    private ImageView imageShop, back_image;
    private RecyclerView recycler_view_food_shop;
    private FoodShopAdapter adapter;
    private LinearLayout empty_layout;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private Shop shop;
    private ConstraintLayout progressLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @IntRange(from = 0, to = 3)
    public int getConnectionType() {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        //Init Component
        initComponent();

        //Check internet connection
        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }

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

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor, null));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CURRENT_PAGE = 0;
                getDetailShop(shopId);
                nestedScrollView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);

                recycler_view_food_shop.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                end_of_list_text_view.setVisibility(View.GONE);

                empty_layout.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initComponent() {
        imageShop = findViewById(R.id.image_shop);
        recycler_view_food_shop = findViewById(R.id.recycler_view_food_shop);
        back_image = findViewById(R.id.back_image);
        adapter = new FoodShopAdapter(this);
        content_description_text_view = findViewById(R.id.content_description_text_view);
        student_shop_text_view = findViewById(R.id.student_shop_text_view);
        nestedScrollView = findViewById(R.id.food_detail);
        progressBar = findViewById(R.id.progress_bar);
        end_of_list_text_view = findViewById(R.id.end_of_list_text);
        progressLayout = findViewById(R.id.progress_layout);
        txt_shop_name = findViewById(R.id.txt_shop_name);
        empty_layout = findViewById(R.id.empty_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
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

                if(listFood.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    end_of_list_text_view.setVisibility(View.GONE);

                    empty_layout.setVisibility(View.VISIBLE);
                    return;
                }


                if(LAST_PAGE){
                    progressBar.setVisibility(View.GONE);
                    end_of_list_text_view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
                progressLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getDetailShop(int shopId) {
        FoodApi.apiService.getShopById(shopId).enqueue(new Callback<Shop>() {
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
                Log.e(TAG, t.toString());
            }
        });
    }

    private void initData(Shop shop) {
        //Init data
        Picasso.get().load(shop.getImageUrl()).into(imageShop);
        txt_shop_name.setText(shop.getName());
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