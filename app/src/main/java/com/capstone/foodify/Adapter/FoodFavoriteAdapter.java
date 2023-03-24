package com.capstone.foodify.Adapter;

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
