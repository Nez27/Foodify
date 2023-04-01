package com.capstone.foodify.Adapter;

import android.app.Activity;
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
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.List;

public class FoodShopAdapter extends RecyclerView.Adapter<FoodShopAdapter.FoodShopViewHolder> {

    private List<Food> listFoodShop;
    private Context context;
    private static final int MAX_CHARACTER = 35;

    public FoodShopAdapter(Context context) {
        this.context = context;
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

        if(food.getImages().size() == 0){
            holder.imageView.setImageResource(R.drawable.default_image_food);
        } else {
            Picasso.get().load(food.getImages().get(0).getImageUrl()).into(holder.imageView);
        }
        holder.price.setText(Common.changeCurrencyUnit(food.getCost()));

        String foodName = food.getName();
        if(foodName.length() >= MAX_CHARACTER){
            StringBuilder stringBuilder = new StringBuilder(food.getName());
            stringBuilder.replace(MAX_CHARACTER, food.getName().length(), "..." );
            holder.foodName.setText(stringBuilder);
        } else {
            holder.foodName.setText(foodName);
        }

        String description = food.getDescription();
        if(description.length() >= MAX_CHARACTER){
            StringBuilder stringBuilder = new StringBuilder(food.getDescription());
            stringBuilder.replace(MAX_CHARACTER, food.getDescription().length(), "..." );
            holder.description.setText(stringBuilder);
        } else {
            holder.description.setText(description);
        }

        //Check value discountPercent
        float cost;
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

        //Set event basket icon
        holder.basket_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.FINAL_SHOP == null && Common.LIST_BASKET_FOOD.size() == 0){
                    Common.FINAL_SHOP = food.getShop().getName();
                }

                if(Common.FINAL_SHOP.equals(food.getShop().getName())){
                    Common.addFoodToBasket(food);
                    Toast.makeText(context, "Đã thêm " + food.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                } else {
                    new AestheticDialog.Builder((Activity) context, DialogStyle.FLAT, DialogType.INFO)
                            .setTitle("Thông báo!")
                            .setMessage("Bạn chỉ có thể đặt những món ăn cùng cửa hàng, xin vui lòng kiểm tra lại giỏ hàng!")
                            .setAnimation(DialogAnimation.SHRINK)
                            .setCancelable(true)
                            .show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listFoodShop != null)
            return listFoodShop.size();
        return 0;
    }

    public class FoodShopViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, basket_icon;
        TextView foodName, description, price, discount;


        public FoodShopViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            foodName = itemView.findViewById(R.id.food_name_text_view);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price_text_view);
            basket_icon = itemView.findViewById(R.id.basket_icon);
            discount = itemView.findViewById(R.id.discount);
        }
    }
}
