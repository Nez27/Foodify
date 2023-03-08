package com.capstone.foodify.Model.Shop;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Activity.ShopDetailActivity;
import com.capstone.foodify.R;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder>{

    private List<Shop> shopList;

    private Context context;

    public ShopAdapter(Context context){
        this.context = context;
    }

    public void setData(List<Shop> list) {
        this.shopList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);

        if(shop == null)
            return;

        holder.imgView.setImageResource(shop.getResourceId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ShopDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(shopList != null)
            return shopList.size();
        return 0;
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgView;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.shopImage);
        }
    }
}
