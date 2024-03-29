package com.capstone.foodify.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Activity.OrderCheckOutActivity;
import com.capstone.foodify.Activity.SignInActivity;
import com.capstone.foodify.Common;
import com.capstone.foodify.ItemTouchHelperListener;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Adapter.BasketAdapter;
import com.capstone.foodify.Adapter.RecyclerViewItemTouchHelperBasketFood;
import com.capstone.foodify.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment implements ItemTouchHelperListener {

    public RecyclerView recyclerView_basket_food;
    private BasketAdapter adapter;
    private List<Basket> listBasketFood = new ArrayList<>();
    private LinearLayout listBasketFoodView;
    public NestedScrollView listBasketFoodLayout;
    private Button btnCheckOut;
    public ConstraintLayout empty_layout;
    public TextView total;
    public float totalFloat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basket, container, false);

        //Init Component
        recyclerView_basket_food = view.findViewById(R.id.recycler_view_basket_food);
        listBasketFoodView = view.findViewById(R.id.list_basket_food_view);
        adapter = new BasketAdapter(this, getContext());
        total = view.findViewById(R.id.total_text_view);
        empty_layout = view.findViewById(R.id.empty_layout);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
        listBasketFoodLayout = view.findViewById(R.id.list_basket_food_layout);

        //Check status internet
        if(Common.getConnectionType(requireContext()) == 0){
            Common.showErrorInternetConnectionNotification(getActivity());
        }

        //Check list basket is null or not!
        checkListBasket();

        //Get list food in basket
        getListBasketFood();

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_basket_food.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView_basket_food.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelperBasketFood(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_basket_food);

        //Set event for button check out
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.LIST_BASKET_FOOD.size() == 0){
                    Toast.makeText(getContext(), "Giỏ hàng của bạn hiện đang trống!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), OrderCheckOutActivity.class);
                    intent.putExtra("total", totalFloat);
                    startActivity(intent);
                }

            }
        });

        return view;
    }

    private void checkListBasket() {
        if(Common.LIST_BASKET_FOOD.size() > 0){
            showRecycleViewAndHideNotificationEmpty();
        } else {
            hideRecyclerViewAndShowNotificationEmpty();
            total.setText(Common.changeCurrencyUnit(0));
        }
    }

    private void showRecycleViewAndHideNotificationEmpty(){
        listBasketFoodLayout.setVisibility(View.VISIBLE);
        empty_layout.setVisibility(View.GONE);
    }

    private void hideRecyclerViewAndShowNotificationEmpty(){
        listBasketFoodLayout.setVisibility(View.GONE);
        empty_layout.setVisibility(View.VISIBLE);
    }
    private void getListBasketFood(){
        //Get list food from basket
        listBasketFood = Common.LIST_BASKET_FOOD;
        adapter.setData(listBasketFood);
        recyclerView_basket_food.setAdapter(adapter);
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
            Snackbar snackbar = Snackbar.make(listBasketFoodView, foodNameDelete + " đã xoá!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.undoItem(foodDelete, indexDelete, getContext());
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        listBasketFood = Common.LIST_BASKET_FOOD;
        adapter.setData(listBasketFood);

        checkListBasket();
    }
}