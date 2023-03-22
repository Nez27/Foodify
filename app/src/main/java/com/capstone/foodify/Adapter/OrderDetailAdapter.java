package com.capstone.foodify.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>{

    public List<Basket> listFoodInBasket;

    public OrderDetailAdapter(){}

    public void setData(List<Basket> listFoodInBasket){
        this.listFoodInBasket = listFoodInBasket;
    }


    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        Basket food = listFoodInBasket.get(position);

        if(food == null)
            return;

        Picasso.get().load(food.getImg()).into(holder.image_view);
        holder.food_name.setText(food.getName());
        holder.shop_name.setText(food.getShopName());
        holder.quantity.setText("Số lượng: " + food.getQuantity());

        //Check value discountPercent
        float cost = 0;
        if(food.getDiscountPercent() > 0){

            //Calculate final cost when apply discountPercent
            cost = food.getCost() - (food.getCost() * food.getDiscountPercent()/100);

            //Show discountPercent value on screen
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText("-" + food.getDiscountPercent()+ "%");
        } else {
            cost = food.getCost();
        }

        holder.price.setText(Common.changeCurrencyUnit(cost));
    }

    @Override
    public int getItemCount() {
        if(listFoodInBasket != null)
            return listFoodInBasket.size();
        return 0;
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView image_view;
        TextView food_name, shop_name, price, quantity, discount;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            //Init Component
            image_view = itemView.findViewById(R.id.image_view);
            food_name = itemView.findViewById(R.id.food_name_text_view);
            shop_name = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            quantity = itemView.findViewById(R.id.quantity_text_view);
            discount = itemView.findViewById(R.id.discount);
        }
    }
}
