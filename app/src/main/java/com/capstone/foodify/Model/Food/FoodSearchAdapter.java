package com.capstone.foodify.Model.Food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Common;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder>{

    private List<Food> listFood;
    private Context context;

    public FoodSearchAdapter(Context context){
        this.context = context;
    }

    public FoodSearchAdapter() {}
    public void setData(List<Food> list) {
        this.listFood = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_search, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = listFood.get(position);
        if(food == null){
            return;
        }

        String foodName = food.getName();
        if(foodName.length() >= 19){
            StringBuilder stringBuilder = new StringBuilder(food.getName());
            stringBuilder.replace(19, food.getName().length(), "..." );
            holder.name.setText(stringBuilder);
        } else {
            holder.name.setText(foodName);
        }
        Picasso.get().load(food.getImg()).into(holder.image);


        holder.price.setText(Common.changeCurrencyUnit(Float.parseFloat(food.getPrice())));
    }

    @Override
    public int getItemCount() {
        if(listFood != null) {
            return listFood.size();
        }
        return 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        private final ImageView image;
        private final TextView price, name;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.food_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);

        }
    }
}
