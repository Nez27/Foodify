package com.capstone.foodify.Model.Food;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_foods, parent, false);
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
        holder.price.setText(food.getPrice());
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
        private TextView name, price;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);

        }
    }
}
