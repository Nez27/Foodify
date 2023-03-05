package com.capstone.foodify.Model.Basket;

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
import com.capstone.foodify.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketViewHolder> {

    public List<Basket> listBasketFood;

    private final BasketFragment basketFragment;

    public BasketAdapter(BasketFragment basketFragment) {
        this.basketFragment = basketFragment;
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
        holder.price.setText(Common.changeCurrencyUnit(Float.parseFloat(foodBasket.getPrice())));
        Picasso.get().load(foodBasket.getImg()).into(holder.imageView);
        holder.quantity.setNumber(foodBasket.getQuantity());

        //Calculate total price
        calculateTotalPrice();

        //Update quantity food when change quantity number
        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                foodBasket.setQuantity(String.valueOf(newValue));

                //Update total price
                calculateTotalPrice();
            }
        });
    }

    private void calculateTotalPrice() {
        float total = 0;

        for(Basket foodTemp: listBasketFood){
            total += (Float.parseFloat(foodTemp.getPrice())) * (Float.parseFloat(foodTemp.getQuantity())) * (100 - Long.parseLong(foodTemp.getDiscount()))/100;
        }
        basketFragment.total.setText(Common.changeCurrencyUnit(total));
    }

    @Override
    public int getItemCount() {
        if(listBasketFood != null)
            return listBasketFood.size();
        return 0;
    }

    public static class BasketViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, shopName, price, total;
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
            total = itemView.findViewById(R.id.total_text_view);

            layoutForeGround = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeItem(int index, Context context){
        listBasketFood.remove(index);
        notifyItemRemoved(index);

        calculateTotalPrice();
    }

    public void undoItem(Basket food, int index, Context context){
        listBasketFood.add(index, food);
        notifyItemInserted(index);

        calculateTotalPrice();
    }
}
