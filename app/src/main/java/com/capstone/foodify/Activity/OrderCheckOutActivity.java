package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.capstone.foodify.Adapter.OrderDetailAdapter;
import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.R;

import java.util.ArrayList;
import java.util.List;

public class OrderCheckOutActivity extends AppCompatActivity {

    public List<OrderDetail> listOrderDetails = new ArrayList<>();
    OrderDetailAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check_out);

        adapter = new OrderDetailAdapter();
        recyclerView = findViewById(R.id.list_order);

        for(int i = 0; i < 5; i++){
            listOrderDetails.add(new OrderDetail());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setData(listOrderDetails);
        recyclerView.setAdapter(adapter);
    }
}