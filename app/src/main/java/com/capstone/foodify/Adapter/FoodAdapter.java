package com.capstone.foodify.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Activity.FoodDetailActivity;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.Food;
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

        //When food does not have image
        if(food.getImages().size() == 0){
            holder.image.setImageResource(R.drawable.default_image_food);
        } else {
            Picasso.get().load(food.getImages().get(0).getImageUrl()).into(holder.image);
        }

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

        holder.basket_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Basket foodInBasket = Common.getFoodExistInBasket(food.getId());
                if(foodInBasket == null){
                    //If item is not exist in basket

                    //Check food image is null or not
                    String imageTemp = null;
                    if(food.getImages().size() > 0)
                        imageTemp = food.getImages().get(0).getImageUrl();

                    Common.LIST_BASKET_FOOD.add(new Basket(food.getId(), imageTemp, food.getName(), food.getCost(), food.getShop().getName(),
                            "1", food.getDiscountPercent()));
                } else {
                    //If item is exist in basket

                    for(int i = 0; i < Common.LIST_BASKET_FOOD.size(); i++){
                        String foodId = Common.LIST_BASKET_FOOD.get(i).getId();
                        //Find foodId exist
                        if(foodId.equals(food.getId())){
                            //Get quantity food from basket
                            String quantity = Common.LIST_BASKET_FOOD.get(i).getQuantity();

                            //Change quantity food from basket
                            int quantityInt = Integer.parseInt(quantity) + 1;
                            Common.LIST_BASKET_FOOD.get(i).setQuantity(String.valueOf(quantityInt));
                            break;
                        }
                    }

                }
                Toast.makeText(context, "Đã thêm " + food.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
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

        private ImageView image, basket_icon;
        private TextView name, price, discount;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);
            basket_icon = itemView.findViewById(R.id.basket_icon);
        }
    }
}
