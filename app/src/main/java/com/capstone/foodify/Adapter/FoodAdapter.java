package com.capstone.foodify.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Activity.FoodDetailActivity;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder>{

    private List<Food> listFood;
    private Context context;

    public FoodAdapter(Context context){
        this.context = context;
    }

    public FoodAdapter() {}
    public void setData(List<Food> list) {
        this.listFood = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = listFood.get(position);
        if(food == null){
            return;
        }

        String foodName = food.getName();
        if(foodName.length() >= 30){
            StringBuilder stringBuilder = new StringBuilder(food.getName());
            stringBuilder.replace(30, food.getName().length(), "..." );
            holder.name.setText(stringBuilder);
        } else {
            holder.name.setText(foodName);
        }
        Picasso.get().load(food.getImages().get(0).getImageUrl()).into(holder.image);

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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("FoodId", food.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listFood != null) {
            return listFood.size();
        }
        return 0;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView name, price, discount;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);
        }
    }
}
