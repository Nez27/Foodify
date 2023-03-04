package com.capstone.foodify.Model.Basket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketViewHolder> {

        public List<Basket> listBasketFood;

    public BasketAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Basket> listBasketFood){
        this.listBasketFood = listBasketFood;
        notifyDataSetChanged();
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
        holder.price.setText(foodBasket.getPrice());
        Picasso.get().load(foodBasket.getImg()).into(holder.imageView);
        holder.quantity.setNumber(foodBasket.getQuantity());

        //Update quantity food when change quantity number
        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                foodBasket.setQuantity(String.valueOf(newValue));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listBasketFood != null)
            return listBasketFood.size();
        return 0;
    }

    public static class BasketViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, shopName, price;
        public LinearLayout layoutForeGround;
        ImageView imageView;
        ElegantNumberButton quantity;

        public BasketViewHolder(@NonNull View itemView) {
            super(itemView);

            //Init Component
            foodName = itemView.findViewById(R.id.food_name_text_view);
            shopName = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            imageView = itemView.findViewById(R.id.image_view);
            quantity = itemView.findViewById(R.id.quantity);

            layoutForeGround = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeItem(int index, Context context){
        listBasketFood.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem(Basket food, int index, Context context){
        listBasketFood.add(index, food);
        notifyItemInserted(index);
    }
}
