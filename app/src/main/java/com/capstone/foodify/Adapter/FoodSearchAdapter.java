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
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

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

        holder.name.setText(food.getName());
        holder.shopName.setText(food.getShop().getName());

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
                if(Common.FINAL_SHOP == null && Common.LIST_BASKET_FOOD.size() == 0){
                    Common.FINAL_SHOP = food.getShop().getName();
                }

                if(Common.FINAL_SHOP.equals(food.getShop().getName())){
                    Common.addFoodToBasket(food);
                    Toast.makeText(context, "Đã thêm " + food.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                } else {

                    Common.notificationDialog((Activity) context, DialogStyle.FLAT, DialogType.INFO, "Thông báo!", "Bạn chỉ có thể đặt những món ăn cùng cửa hàng, xin vui lòng kiểm tra lại giỏ hàng!");
                }
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
