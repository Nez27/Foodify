package com.capstone.foodify.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Adapter.FoodFavoriteAdapter;
import com.capstone.foodify.Adapter.RecyclerViewItemTouchHelperFavoriteFood;
import com.capstone.foodify.Common;
import com.capstone.foodify.ItemTouchHelperListener;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFoodActivity extends AppCompatActivity implements ItemTouchHelperListener {
    private static final String TAG = "FavoriteFoodActivity";
    private static int CURRENT_PAGE = 0;
    private static final int PAGE_SIZE = 8;
    private static final String SORT_BY = "name";
    private static final String SORT_DIR = "asc";
    private static boolean LAST_PAGE;
    private RecyclerView recyclerView_favorite_food;
    private FoodFavoriteAdapter adapter;
    private final List<Food> listFavoriteFood = new ArrayList<>();
    private ImageView back_image;
    private ConstraintLayout listFavoriteFoodView, listFavoriteFoodLayout;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private TextView endOfListText;
    private LinearLayout no_food_favorite_layout;

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
        setContentView(R.layout.activity_favorite_food);

        //Init component
        initComponent();

        //Check internet connection
        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }

        //Set event when user scroll down
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


        //Set event for back image
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //If user not sign in yet, app will move to sign in screen
        if(Common.CURRENT_USER == null)
            startActivity(new Intent(FavoriteFoodActivity.this, SignInActivity.class));

        recyclerView_favorite_food = findViewById(R.id.recycler_view_favorite_food);
        listFavoriteFoodView = findViewById(R.id.list_favorite_food_view);

        //Get list favorite food
        CURRENT_PAGE = 0;
        getListFavoriteFood(CURRENT_PAGE);

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_favorite_food.setLayoutManager(linearLayoutManager);
        adapter = new FoodFavoriteAdapter(this);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView_favorite_food.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelperFavoriteFood(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_favorite_food);
    }

    private void initComponent() {
        back_image = findViewById(R.id.back_image);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        progressBar = findViewById(R.id.progress_bar);
        endOfListText = findViewById(R.id.end_of_list_text);
        no_food_favorite_layout = findViewById(R.id.no_food_favorite_layout);
        listFavoriteFoodLayout = findViewById(R.id.list_favorite_food_layout);
    }

    private void dataLoadMore() {
        if(!LAST_PAGE){
            getListFavoriteFood(++CURRENT_PAGE);
        } else {
            progressBar.setVisibility(View.GONE);
            endOfListText.setVisibility(View.VISIBLE);
        }
    }

    private void getListFavoriteFood(int page) {
        //Get data from api
        FoodApiToken.apiService.getListFavoriteFood(Common.CURRENT_USER.getId(), page, PAGE_SIZE, SORT_BY, SORT_DIR).enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {

                Foods foodData = response.body();

                if(foodData != null){
                    listFavoriteFood.addAll(foodData.getProducts());
                    LAST_PAGE = foodData.getPage().isLast();
                }

                if(listFavoriteFood.size() == 0){
                    listFavoriteFoodLayout.setVisibility(View.GONE);
                    no_food_favorite_layout.setVisibility(View.VISIBLE);
                } else {
                    listFavoriteFoodLayout.setVisibility(View.VISIBLE);
                    no_food_favorite_layout.setVisibility(View.GONE);
                }

                if(LAST_PAGE){
                    progressBar.setVisibility(View.GONE);
                    endOfListText.setVisibility(View.VISIBLE);
                }


                Collections.sort(listFavoriteFood);

                adapter.setData(listFavoriteFood);
                recyclerView_favorite_food.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }



    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof FoodFavoriteAdapter.FoodFavoriteViewHolder){
            String foodNameDelete = listFavoriteFood.get(viewHolder.getAdapterPosition()).getName();

            Food foodDelete = listFavoriteFood.get(viewHolder.getAdapterPosition());
            int indexDelete = viewHolder.getAdapterPosition();

            //Remove Item
            adapter.removeItem(foodDelete, indexDelete, this);

            if(listFavoriteFood.size() == 0){
                listFavoriteFoodLayout.setVisibility(View.GONE);
                no_food_favorite_layout.setVisibility(View.VISIBLE);
            }

            //Show notification food has been delete
            Snackbar snackbar = Snackbar.make(listFavoriteFoodView, "Đã xoá " + foodNameDelete + " khỏi danh sách yêu thích!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.undoItem(foodDelete, indexDelete, FavoriteFoodActivity.this);

                    listFavoriteFoodLayout.setVisibility(View.VISIBLE);
                    no_food_favorite_layout.setVisibility(View.GONE);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}