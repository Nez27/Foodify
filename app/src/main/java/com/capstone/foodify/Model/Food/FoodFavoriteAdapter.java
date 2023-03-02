package com.capstone.foodify.Model.Food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodFavoriteAdapter extends  RecyclerView.Adapter<FoodFavoriteAdapter.FoodFavoriteViewHolder> {

    private List<Food> listFavoriteFood;

    public FoodFavoriteAdapter(List<Food> listFavoriteFood) {
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

        holder.foodName.setText(food.getName());
        holder.shopName.setText(food.getShopName());
        holder.price.setText(food.getPrice());
        Picasso.get().load(food.getImg()).into(holder.imageView);
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

    public void removeItem(int index){
        listFavoriteFood.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem(Food food, int index){
        listFavoriteFood.add(index, food);
        notifyItemInserted(index);
    }
}
