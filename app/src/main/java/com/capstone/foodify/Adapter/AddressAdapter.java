package com.capstone.foodify.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Model.Address.Address;
import com.capstone.foodify.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>{

    private List<Address> listAddress;

    public AddressAdapter() {
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

        //Create a full address string
        String fullAddress = address.getAddress() + ", " + address.getWard() + ", " + address.getDistrict();

        //Bind data to component
        holder.text_view_address.setText(fullAddress);


    }

    @Override
    public int getItemCount() {
        if(listAddress != null)
            return listAddress.size();
        return 0;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder{

        private TextView text_view_address, default_text_view, blank_text_view;
        private Button edit_btn, delete_btn;

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
