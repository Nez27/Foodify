package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.Adapter.OrderDetailAdapter;
import com.capstone.foodify.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView order_tracking_number, user_name, phone, address, orderTime, total;
    private ImageView back_image;
    public List<OrderDetail> listOrderDetails = new ArrayList<>();
    OrderDetailAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //Init Component
        order_tracking_number = findViewById(R.id.order_tracking_number);
        user_name = findViewById(R.id.user_name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        orderTime = findViewById(R.id.order_time);
        total = findViewById(R.id.total);
        adapter = new OrderDetailAdapter();
        recyclerView = findViewById(R.id.recycler_view_order_detail);
        back_image = findViewById(R.id.back_image);

        //Init temp data
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();

        int b = (int)(Math.random()*(999999-100000+1)+100000);
        order_tracking_number.setText("Order Id: #" + b);
        user_name.setText("Tên: Nguyễn Văn A");
        phone.setText("0987654321");
        address.setText("Phạm Như Xương, Hoà Khánh Nam, Liên Chiểu");
        orderTime.setText(dtf.format(now));
        total.setText("Tổng: 1.200.000đ");

        for(int i = 0; i < 5; i++){
            listOrderDetails.add(new OrderDetail());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

//        adapter.setData(listOrderDetails);
        recyclerView.setAdapter(adapter);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}