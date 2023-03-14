package com.capstone.foodify.Adapter;

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
import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodFavoriteAdapter extends  RecyclerView.Adapter<FoodFavoriteAdapter.FoodFavoriteViewHolder> {

    private List<Food> listFavoriteFood;

    public FoodFavoriteAdapter(){}

    public void setData(List<Food> listFavoriteFood){
        this.listFavoriteFood = listFavoriteFood;
    }
    @NonNull
    @Override
    public FoodFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_favorite, parent, false);
        return new FoodFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodFavoriteViewHolder holder, int position) {
        Food food = listFavoriteFood.get(position);

        if(food == null)
            return;

//        holder.foodName.setText(food.getName());
//        holder.shopName.setText(food.getShopName());
//        holder.price.setText(food.getPrice());
//        Picasso.get().load(food.getImg()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(listFavoriteFood != null)
            return listFavoriteFood.size();

        return 0;
    }

    public static class FoodFavoriteViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, shopName, price;
        public LinearLayout layoutForeGround;
        ImageView imageView;

        public FoodFavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.food_name_text_view);
            shopName = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            imageView = itemView.findViewById(R.id.image_view);

            layoutForeGround = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeItem(int index, Context context){
        listFavoriteFood.remove(index);
        notifyItemRemoved(index);

        FoodApiToken.apiService.removeFoodFromFavorite(index).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Toast.makeText(context, "Remove successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(context, "Error: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void undoItem(Food food, int index, Context context){
        listFavoriteFood.add(index, food);
        notifyItemInserted(index);

//        FoodApiToken.apiService.addFoodToFavorite(food.getId()).enqueue(new Callback<Food>() {
//            @Override
//            public void onResponse(@NonNull Call<Food> call, @NonNull Response<Food> response) {
//                Toast.makeText(context, "Add successful!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Food> call, @NonNull Throwable t) {
//                Toast.makeText(context, "Error: " + t, Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
