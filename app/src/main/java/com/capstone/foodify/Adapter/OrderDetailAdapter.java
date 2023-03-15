package com.capstone.foodify.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>{

    public List<OrderDetail> listOrderDetails;

    public OrderDetailAdapter(){}

    public void setData(List<OrderDetail> listOrderDetails){
        this.listOrderDetails = listOrderDetails;
    }


    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = listOrderDetails.get(position);

        if(orderDetail == null)
            return;

        Picasso.get().load("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxleHBsb3JlLWZlZWR8MXx8fGVufDB8fHx8&w=1000&q=80").into(holder.image_view);
        holder.food_name.setText("Food Name");
        holder.shop_name.setText("Shop Name");
        holder.price.setText("120000 đ");
        holder.quantity.setText("Số lượng: 2");
    }

    @Override
    public int getItemCount() {
        if(listOrderDetails != null)
            return listOrderDetails.size();
        return 0;
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView image_view;
        TextView food_name, shop_name, price, quantity;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            //Init Component
            image_view = itemView.findViewById(R.id.image_view);
            food_name = itemView.findViewById(R.id.food_name_text_view);
            shop_name = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            quantity = itemView.findViewById(R.id.quantity_text_view);
        }
    }
}
