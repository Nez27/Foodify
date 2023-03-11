package com.capstone.foodify.Fragment.Order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.foodify.Model.Order.Order;
import com.capstone.foodify.Adapter.OrderAdapter;
import com.capstone.foodify.R;

import java.util.ArrayList;
import java.util.List;

public class ProcessOderFragment extends Fragment {

    private List<Order> listOrders = new ArrayList<>();
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_process_order, container, false);

        //Init Component
        recyclerView = view.findViewById(R.id.recycler_view_process_order);

        orderAdapter = new OrderAdapter();

        for(int i = 0; i < 5; i++){
            int b = (int)(Math.random()*(999999-100000+1)+100000);
            listOrders.add(new Order(String.valueOf(b)));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        orderAdapter.setData(listOrders);
        recyclerView.setAdapter(orderAdapter);

        return view;
    }
}