package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.Adapter.FoodFavoriteAdapter;
import com.capstone.foodify.Adapter.RecyclerViewItemTouchHelperFavoriteFood;
import com.capstone.foodify.Common;
import com.capstone.foodify.ItemTouchHelperListener;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFoodActivity extends AppCompatActivity implements ItemTouchHelperListener {

    private RecyclerView recyclerView_favorite_food;
    private FoodFavoriteAdapter adapter;
    private final List<Food> listFavoriteFood = new ArrayList<>();
    private ImageView back_image;
    private NestedScrollView listFavoriteFoodView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_food);

        //Init component
        back_image = findViewById(R.id.back_image);

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
        getListFavoriteFood();

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_favorite_food.setLayoutManager(linearLayoutManager);
        adapter = new FoodFavoriteAdapter();

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView_favorite_food.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelperFavoriteFood(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_favorite_food);
    }

    private void getListFavoriteFood() {
        //Get data from api
//        FoodApiToken.apiService.getListFavoriteFood().enqueue(new Callback<List<Food>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Food>> call, @NonNull Response<List<Food>> response) {
//                List<Food> listFoodData = response.body();
//                if(listFoodData != null){
//                    listFavoriteFood.addAll(listFoodData);
//                }
//                Collections.sort(listFavoriteFood);
//
//                adapter.setData(listFavoriteFood);
//                recyclerView_favorite_food.setAdapter(adapter);
//            }
//
//            @Override
//            public void onFailure(Call<List<Food>> call, Throwable t) {
//                //Check internet connection
//                if(Common.checkInternetConnection(getContext())){
//                    //Has internet connection
//                    Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
//                } else {
//                    //No internet, show notification
//                    Common.showErrorInternetConnectionNotification(getActivity());
//
//                    Log.e("ERROR", "Get list favorite food error!");
//
//                }
//            }
//        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof FoodFavoriteAdapter.FoodFavoriteViewHolder){
            String foodNameDelete = listFavoriteFood.get(viewHolder.getAdapterPosition()).getName();

            Food foodDelete = listFavoriteFood.get(viewHolder.getAdapterPosition());
            int indexDelete = viewHolder.getAdapterPosition();

            //Remove Item
            adapter.removeItem(indexDelete, this);

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