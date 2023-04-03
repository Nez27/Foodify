package com.capstone.foodify.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Common;
import com.capstone.foodify.Fragment.BasketFragment;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketViewHolder> {


    public List<Basket> listBasketFood;

    private final BasketFragment basketFragment;

    public BasketAdapter(BasketFragment basketFragment) {
        this.basketFragment = basketFragment;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Basket> listBasketFood){
        this.listBasketFood = listBasketFood;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_basket, parent, false);
        return new BasketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder holder, int position) {

        Basket foodBasket = listBasketFood.get(position);

        if(foodBasket == null)
            return;

        //Init data
        holder.foodName.setText(foodBasket.getName());
        holder.shopName.setText(foodBasket.getShopName());
        holder.value.setText(foodBasket.getQuantity());

        //Check value discountPercent
        float cost;
        if(foodBasket.getDiscountPercent() > 0){

            //Calculate final cost when apply discountPercent
            cost = foodBasket.getCost() - (foodBasket.getCost() * foodBasket.getDiscountPercent()/100);

            //Show discountPercent value on screen
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText("-" + foodBasket.getDiscountPercent()+ "%");
        } else {
            cost = foodBasket.getCost();
        }

        holder.price.setText(Common.changeCurrencyUnit(cost));

        //Check food image is null or not null
        if(foodBasket.getImg() == null){
            holder.imageView.setImageResource(R.drawable.default_image_food);
        } else {
            Picasso.get().load(foodBasket.getImg()).into(holder.imageView);
        }

        //Calculate total price
        calculateTotalPrice();

        holder.increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantity = Integer.parseInt(foodBasket.getQuantity());

                quantity++;
                foodBasket.setQuantity(String.valueOf(quantity));
                holder.value.setText(String.valueOf(quantity));

                //Update total price
                calculateTotalPrice();
            }
        });

        holder.decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantity = Integer.parseInt(foodBasket.getQuantity());

                quantity--;
                if(quantity == 0){
                    listBasketFood.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();

                    //Update total price
                    calculateTotalPrice();
                } else {
                    foodBasket.setQuantity(String.valueOf(quantity));
                    holder.value.setText(String.valueOf(quantity));

                    //Update total price
                    calculateTotalPrice();
                }
            }
        });
    }

    private void calculateTotalPrice() {
        float total = 0;

        for(Basket foodTemp: listBasketFood){
            total += (foodTemp.getCost()) * (Float.parseFloat(foodTemp.getQuantity())) * (100 - foodTemp.getDiscountPercent())/100;
        }
        basketFragment.total.setText(Common.changeCurrencyUnit(total));
        basketFragment.totalFloat = total;
    }

    @Override
    public int getItemCount() {
        if(listBasketFood != null)
            return listBasketFood.size();
        return 0;
    }

    public static class BasketViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, shopName, price, total, discount, increment, value, decrement;
        public LinearLayout layoutForeGround;
        ImageView imageView;

        public BasketViewHolder(@NonNull View itemView) {
            super(itemView);

            //Init Component
            foodName = itemView.findViewById(R.id.food_name_text_view);
            shopName = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            imageView = itemView.findViewById(R.id.image_view);

            total = itemView.findViewById(R.id.total_text_view);
            discount = itemView.findViewById(R.id.discount);
            increment = itemView.findViewById(R.id.txt_increment);
            decrement = itemView.findViewById(R.id.txt_decrement);
            value = itemView.findViewById(R.id.value);

            layoutForeGround = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeItem(int index, Context context){
        listBasketFood.remove(index);
        notifyItemRemoved(index);

        calculateTotalPrice();

        //Show notification empty basket
        if(listBasketFood.size() == 0){
            Common.FINAL_SHOP = null;
            hideRecyclerViewAndShowNotificationEmpty();
        } else {
            showRecycleViewAndHideNotificationEmpty();
        }
    }
    public void undoItem(Basket food, int index, Context context){
        listBasketFood.add(index, food);
        notifyItemInserted(index);

        calculateTotalPrice();

        ////Show notification empty basket
        if(listBasketFood.size() == 0){
            Common.FINAL_SHOP = null;
            hideRecyclerViewAndShowNotificationEmpty();
        } else {
            showRecycleViewAndHideNotificationEmpty();
        }
    }

    private void showRecycleViewAndHideNotificationEmpty(){
        basketFragment.listBasketFoodLayout.setVisibility(View.VISIBLE);
        basketFragment.empty_layout.setVisibility(View.GONE);
    }

    private void hideRecyclerViewAndShowNotificationEmpty(){
        basketFragment.listBasketFoodLayout.setVisibility(View.GONE);
        basketFragment.empty_layout.setVisibility(View.VISIBLE);
    }
}
