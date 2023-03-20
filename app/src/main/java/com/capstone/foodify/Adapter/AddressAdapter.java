package com.capstone.foodify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Activity.AddressManagerActivity;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;
import com.sjapps.library.customdialog.CustomViewDialog;
import com.sjapps.library.customdialog.DialogButtonEvents;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>{

    private List<Address> listAddress;
    private Context context;

    public AddressAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Address> listAddress){
        this.listAddress = listAddress;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = listAddress.get(position);

        if(address == null)
            return;


        String fullAddress;
        //If user choice "Huyện Hoàng Sa" District
        if(address.getWard().equals("---Phường")){
            //Create a full address string
            fullAddress = address.getAddress()  + ", " + address.getDistrict();
        } else {
            fullAddress = address.getAddress() + ", " + address.getWard() + ", " + address.getDistrict();
        }

        //Bind data to component
        holder.text_view_address.setText(fullAddress);

        //Edit address
        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof AddressManagerActivity){
                    ((AddressManagerActivity)context).showAddAndEditAddressDialog(address);
                }
            }
        });

        //Delete address
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof AddressManagerActivity){
                    ((AddressManagerActivity)context).showConfirmDeleteDialog(address.getId(), fullAddress);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listAddress != null)
            return listAddress.size();
        return 0;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder{

        private TextView text_view_address, default_text_view, blank_text_view;
        private ImageView edit_btn, delete_btn;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            text_view_address = itemView.findViewById(R.id.text_view_address);
            default_text_view = itemView.findViewById(R.id.default_text_view);
            blank_text_view = itemView.findViewById(R.id.blank_text_view);

            edit_btn = itemView.findViewById(R.id.edit_button);
            delete_btn = itemView.findViewById(R.id.delete_button);
        }
    }
}
