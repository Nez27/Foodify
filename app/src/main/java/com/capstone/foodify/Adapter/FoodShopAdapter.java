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
import com.capstone.foodify.Model.Basket.Basket;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodShopAdapter extends RecyclerView.Adapter<FoodShopAdapter.FoodShopViewHolder> {

    private List<Food> listFoodShop;
    private Context context;

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

        holder.foodName.setText(food.getName());
        holder.quantitySoldOut.setText("1.2k đã bán");
        holder.price.setText(Common.changeCurrencyUnit(food.getCost()));

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
    }

    @Override
    public int getItemCount() {
        if(listFoodShop != null)
            return listFoodShop.size();
        return 0;
    }

    public class FoodShopViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, basket_icon;
        TextView foodName, quantitySoldOut, price;


        public FoodShopViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            foodName = itemView.findViewById(R.id.food_name_text_view);
            quantitySoldOut = itemView.findViewById(R.id.quantity_sold_out);
            price = itemView.findViewById(R.id.price_text_view);
            basket_icon = itemView.findViewById(R.id.basket_icon);
        }
    }
}
