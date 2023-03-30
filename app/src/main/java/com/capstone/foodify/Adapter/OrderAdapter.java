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
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Order;
import com.capstone.foodify.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    public List<Order> listOrders;
    private Context context;

    public OrderAdapter(Context context) {
        this.context = context;
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

        if(order == null)
            return;

        holder.order_tracking_number.setText("Order Id: #" + order.getOrderTrackingNumber());
        holder.user_name.setText("Họ và tên: " + Common.CURRENT_USER.getFullName());
        holder.phone.setText("Số điện thoại: " + Common.CURRENT_USER.getPhoneNumber());
        holder.address.setText("Địa chỉ: " + order.getAddress());
        holder.orderTime.setText("Thời gian đặt: " + order.getOrderTime());

        if(order.getTotal() != null)
            holder.total.setText("Tổng: " + Common.changeCurrencyUnit(order.getTotal()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("order", order);
                context.startActivity(intent);
            }
        });
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
        }
    }

    public static Timestamp convertStringToTimestamp(String strDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            // you can change format of date
            Date date = formatter.parse(strDate);
            Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }
}
