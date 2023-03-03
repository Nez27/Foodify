package com.capstone.foodify.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.ItemTouchHelperListener;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.FoodFavoriteAdapter;
import com.capstone.foodify.R;
import com.capstone.foodify.RecyclerViewItemTouchHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteFragment extends Fragment implements ItemTouchHelperListener {

    private RecyclerView recyclerView_favorite_food;
    private FoodFavoriteAdapter adapter;
    private final List<Food> listFavoriteFood = new ArrayList<>();

    private NestedScrollView listFavoriteFoodView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView_favorite_food = rootView.findViewById(R.id.recycler_view_favorite_food);
        listFavoriteFoodView = rootView.findViewById(R.id.list_favorite_food_view);

        //Get list favorite food
        getListFavoriteFood();

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_favorite_food.setLayoutManager(linearLayoutManager);
        adapter = new FoodFavoriteAdapter();


        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView_favorite_food.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_favorite_food);
        return rootView;
    }

    private void getListFavoriteFood() {
        //Get data from api
        FoodApi.apiService.getListFavoriteFood().enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(@NonNull Call<List<Food>> call, @NonNull Response<List<Food>> response) {
                List<Food> listFoodData = response.body();
                if(listFoodData != null){
                    listFavoriteFood.addAll(listFoodData);
                }
                Collections.sort(listFavoriteFood);

                adapter.setData(listFavoriteFood);
                recyclerView_favorite_food.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
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
            adapter.removeItem(indexDelete, getContext());

            //Show notification food has been delete
            Snackbar snackbar = Snackbar.make(listFavoriteFoodView, foodNameDelete + " remove!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.undoItem(foodDelete, indexDelete, getContext());
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}