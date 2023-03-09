package com.capstone.foodify.Model.Food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodShopAdapter extends RecyclerView.Adapter<FoodShopAdapter.FoodShopViewHolder> {

    private List<Food> listFoodShop;

    public FoodShopAdapter() {
    }

    public void setData(List<Food> listFoodShop){
        this.listFoodShop = listFoodShop;
    }

    @NonNull
    @Override
    public FoodShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_shop, parent, false);
        return new FoodShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodShopViewHolder holder, int position) {
        Food food = listFoodShop.get(position);

        if(food == null)
            return;

//        Picasso.get().load(food.getImg()).into(holder.imageView);
//        holder.foodName.setText(food.getName());
//        holder.quantitySoldOut.setText("1.2k đã bán");
//        holder.price.setText(food.getPrice());
    }

    @Override
    public int getItemCount() {
        if(listFoodShop != null)
            return listFoodShop.size();
        return 0;
    }

    public class FoodShopViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView foodName, quantitySoldOut, price;


        public FoodShopViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            foodName = itemView.findViewById(R.id.food_name_text_view);
            quantitySoldOut = itemView.findViewById(R.id.quantity_sold_out);
            price = itemView.findViewById(R.id.price_text_view);
        }
    }
}
