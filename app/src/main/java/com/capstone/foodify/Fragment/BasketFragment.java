package com.capstone.foodify.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Common;
import com.capstone.foodify.ItemTouchHelperListener;
import com.capstone.foodify.Model.Basket.Basket;
import com.capstone.foodify.Model.Basket.BasketAdapter;
import com.capstone.foodify.Model.Basket.RecyclerViewItemTouchHelperBasketFood;
import com.capstone.foodify.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment implements ItemTouchHelperListener {

    private RecyclerView recyclerView_basket_food;
    private BasketAdapter adapter;
    private List<Basket> listBasketFood = new ArrayList<>();
    private RelativeLayout listBasketFoodView;

    public TextView total;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basket, container, false);

        //Init Component
        recyclerView_basket_food = view.findViewById(R.id.recycler_view_basket_food);
        listBasketFoodView = view.findViewById(R.id.list_basket_food_view);
        adapter = new BasketAdapter(this);
        total = view.findViewById(R.id.total_text_view);

        //Init data
        total.setText(Common.changeCurrencyUnit(0));

        //Get list food in basket
        getListBasketFood();

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_basket_food.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView_basket_food.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelperBasketFood(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_basket_food);

        return view;
    }

    private void getListBasketFood(){
        //Get list food from basket
        listBasketFood = Common.LIST_BASKET_FOOD;
        adapter.setData(listBasketFood);
        recyclerView_basket_food.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On Resume Fragment");
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof BasketAdapter.BasketViewHolder){
            String foodNameDelete = listBasketFood.get(viewHolder.getAdapterPosition()).getName();

            Basket foodDelete = listBasketFood.get(viewHolder.getAdapterPosition());
            int indexDelete = viewHolder.getAdapterPosition();

            //Remove Item
            adapter.removeItem(indexDelete, getContext());

            //Show notification food has been delete
            Snackbar snackbar = Snackbar.make(listBasketFoodView, foodNameDelete + " remove!", Snackbar.LENGTH_LONG);
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