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

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder>{

    public static final int LENGTH_CHARACTER = 40;
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

        //Init data
        String foodName = food.getName();
        if(foodName.length() >= LENGTH_CHARACTER){
            StringBuilder stringBuilder = new StringBuilder(food.getName());
            stringBuilder.replace(LENGTH_CHARACTER, food.getName().length(), "..." );
            holder.name.setText(stringBuilder);
        } else {
            holder.name.setText(foodName);
        }

        String shopName = food.getShop().getName();

        if(shopName.length() >= 18){
            StringBuilder stringBuilder = new StringBuilder(food.getShop().getName());
            stringBuilder.replace(18, food.getShop().getName().length(), "..." );
            holder.shopName.setText(stringBuilder);
        } else {
            holder.shopName.setText(shopName);
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

        //Set event basket icon
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

        //Set event when user click to item food, it will move to screen detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        private final ImageView image, basket_icon;
        private final TextView price, name, discount, shopName;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.food_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            discount = itemView.findViewById(R.id.discount);
            basket_icon = itemView.findViewById(R.id.basket_icon);
            shopName = itemView.findViewById(R.id.shop_name_text_view);
        }
    }
}
