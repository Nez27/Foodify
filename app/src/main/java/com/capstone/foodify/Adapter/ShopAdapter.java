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

import com.capstone.foodify.Activity.ShopDetailActivity;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

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

        //Init data
        Picasso.get().load(shop.getImageUrl()).into(holder.imageView);

        holder.shopName.setText(shop.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ShopDetailActivity.class));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailActivity.class);
                intent.putExtra("ShopId", shop.getId());
                context.startActivity(intent);
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

        private ImageView imageView;
        private TextView shopName;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            shopName = itemView.findViewById(R.id.shopName);
        }
    }
}
