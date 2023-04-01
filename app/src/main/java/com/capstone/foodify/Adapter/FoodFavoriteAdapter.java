package com.capstone.foodify.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApiToken;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodFavoriteAdapter extends  RecyclerView.Adapter<FoodFavoriteAdapter.FoodFavoriteViewHolder> {

    private List<Food> listFavoriteFood;
    private Context context;

    public FoodFavoriteAdapter(Context context){
        this.context = context;
    }

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

        holder.foodName.setText(food.getName());
        holder.shopName.setText(food.getShop().getName());

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

        //When food does not have image
        if(food.getImages().size() == 0){
            holder.imageView.setImageResource(R.drawable.default_image_food);
        } else {
            Picasso.get().load(food.getImages().get(0).getImageUrl()).into(holder.imageView);
        }

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

        //Move to food detail when user click on item
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
        if(listFavoriteFood != null)
            return listFavoriteFood.size();

        return 0;
    }

    public static class FoodFavoriteViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, shopName, price, discount;
        public LinearLayout layoutForeGround;
        ImageView imageView, basket_icon;

        public FoodFavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.food_name_text_view);
            shopName = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            imageView = itemView.findViewById(R.id.image_view);
            discount = itemView.findViewById(R.id.discount);

            basket_icon = itemView.findViewById(R.id.basket_icon);

            layoutForeGround = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeItem(Food food, int index, Context context){
        listFavoriteFood.remove(index);
        notifyItemRemoved(index);

        FoodApiToken.apiService.removeFoodFromFavorite(Common.CURRENT_USER.getId(), Integer.parseInt(food.getId())).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Toast.makeText(context, "Đã xoá " + food.getName() + " khỏi danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(context, "Oops! Đã có lỗi, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void undoItem(Food food, int index, Context context){
        listFavoriteFood.add(index, food);
        notifyItemInserted(index);

        FoodApiToken.apiService.addFoodToFavorite(Common.CURRENT_USER.getId(), Integer.parseInt(food.getId())).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Toast.makeText(context, "Đã hoàn tác thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(context, "Oops! Đã có lỗi, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
