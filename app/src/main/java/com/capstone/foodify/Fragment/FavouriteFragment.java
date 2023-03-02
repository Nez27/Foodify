package com.capstone.foodify.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.foodify.ItemTouchHelperListener;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.FoodFavoriteAdapter;
import com.capstone.foodify.R;
import com.capstone.foodify.RecyclerViewItemTouchHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavouriteFragment extends Fragment implements ItemTouchHelperListener {

    private RecyclerView recyclerView_favorite_food;
    private FoodFavoriteAdapter adapter;
    private List<Food> listFavoriteFood = new ArrayList<>();

    private NestedScrollView listFavoriteFoodView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView_favorite_food = rootView.findViewById(R.id.recycler_view_favorite_food);
        listFavoriteFoodView = rootView.findViewById(R.id.list_favorite_food_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_favorite_food.setLayoutManager(linearLayoutManager);

        listFavoriteFood = getListFavoriteFood();
        adapter = new FoodFavoriteAdapter(getListFavoriteFood());
        recyclerView_favorite_food.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView_favorite_food.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_favorite_food);
        return rootView;
    }

    private List<Food> getListFavoriteFood() {
        List<Food> listFoodTemp = new ArrayList<>();

        for(int i = 1; i <= 20; i++){
            listFoodTemp.add(new Food(String.valueOf(i), "https://cdn.netspace.edu.vn/images/2022/09/29/hoc-nau-mi-quang-1-1024.jpg", "Food "+ i, "25000", "Shop " + i));
        }

        return listFoodTemp;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof FoodFavoriteAdapter.FoodFavoriteViewHolder){
            String foodNameDelete = listFavoriteFood.get(viewHolder.getAdapterPosition()).getName();

            Food foodDelete = listFavoriteFood.get(viewHolder.getAdapterPosition());
            int indexDelete = viewHolder.getAdapterPosition();

            //Remove Item
            adapter.removeItem(indexDelete);

            //Show notification food has been delete
            Snackbar snackbar = Snackbar.make(listFavoriteFoodView, foodNameDelete + "remove!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.undoItem(foodDelete, indexDelete);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}