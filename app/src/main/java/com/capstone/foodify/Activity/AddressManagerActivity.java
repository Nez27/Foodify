package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.capstone.foodify.Model.Address.Address;
import com.capstone.foodify.Model.Address.AddressAdapter;
import com.capstone.foodify.R;

import java.util.ArrayList;
import java.util.List;

public class AddressManagerActivity extends AppCompatActivity {

    private RecyclerView recycler_view_address;
    private AddressAdapter adapter;
    private Button add_address_button;

    private List<Address> listAddress = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);

        //Init Component
        recycler_view_address = findViewById(R.id.recycler_view_address);
        add_address_button = findViewById(R.id.add_address_button);

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_address.setLayoutManager(linearLayoutManager);

        adapter = new AddressAdapter();

        getAddressList();

    }

    private void getAddressList(){

        for(int i = 1; i <= 10; i++){
            listAddress.add(new Address(i, "Hẻm 215, đường Phạm Như Xương", "phường Hoà Khánh Nam", "Liên Chiểu"));
        }

        adapter.setData(listAddress);
        recycler_view_address.setAdapter(adapter);
    }
}