package com.capstone.foodify.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static int CURRENT_PAGE = 0;
    private static final int PAGE_SIZE = 8;
    private static final String SORT_BY = "name";
    private static final String SORT_DIR = "asc";
    private static boolean LAST_PAGE;
    private RecyclerView recyclerView_favorite_food;
    private FoodFavoriteAdapter adapter;
    private final List<Food> listFavoriteFood = new ArrayList<>();
    private ImageView back_image;
    private ConstraintLayout listFavoriteFoodView;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private TextView endOfListText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_food);

        //Init component
        back_image = findViewById(R.id.back_image);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        progressBar = findViewById(R.id.progress_bar);
        endOfListText = findViewById(R.id.end_of_list_text);

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
                Toast.makeText(FavoriteFoodActivity.this, "Đã có lỗi từ hệ thống! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
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

            //Show notification food has been delete
            Snackbar snackbar = Snackbar.make(listFavoriteFoodView, foodNameDelete + " remove!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.undoItem(foodDelete, indexDelete, FavoriteFoodActivity.this);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}