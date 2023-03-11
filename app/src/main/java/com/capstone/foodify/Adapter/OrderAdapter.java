package com.capstone.foodify.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Activity.OrderDetailActivity;
import com.capstone.foodify.Model.Order.Order;
import com.capstone.foodify.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    public List<Order> listOrders;

    public OrderAdapter() {
    }

    public void setData(List<Order> listOrders){
        this.listOrders = listOrders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = listOrders.get(position);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();

        if(order == null)
            return;

        holder.order_tracking_number.setText("Order Id: #" + order.getOrderTrackingNumber());
        holder.user_name.setText("Tên: Nguyễn Văn A");
        holder.phone.setText("0987654321");
        holder.address.setText("Phạm Như Xương, Hoà Khánh Nam, Liên Chiểu");
        holder.orderTime.setText(dtf.format(now));
        holder.total.setText("Tổng: 1.200.000đ");
    }

    @Override
    public int getItemCount() {
        if(listOrders != null)
            return listOrders.size();
        return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{

        TextView order_tracking_number, user_name, phone, address, orderTime, total;
        Context context;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();

            order_tracking_number = itemView.findViewById(R.id.order_tracking_number);
            user_name = itemView.findViewById(R.id.user_name);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            orderTime = itemView.findViewById(R.id.order_time);
            total = itemView.findViewById(R.id.total);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, OrderDetailActivity.class));
                }
            });
        }
    }
}
