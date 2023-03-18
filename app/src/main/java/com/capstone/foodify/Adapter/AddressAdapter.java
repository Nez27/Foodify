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

        //Create a full address string
        String fullAddress = address.getAddress() + ", " + address.getWard() + ", " + address.getDistrict();

        //Bind data to component
        holder.text_view_address.setText(fullAddress);

        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAddressDialog();
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listAddress != null)
            return listAddress.size();
        return 0;
    }

    private void showAddAddressDialog(){
        View view = LayoutInflater.from(context).inflate(R.layout.add_address_dialog,null);

        //Init component
        TextInputLayout textInput_address = view.findViewById(R.id.textInput_address);
        Spinner wardSpinner = view.findViewById(R.id.ward);
        Spinner districtSpinner = view.findViewById(R.id.district);
        EditText edt_address = view.findViewById(R.id.edt_address);
        TextView errorTextDistrictWard = view.findViewById(R.id.errorTextDistrictWard);
        ConstraintLayout progressLayoutDialog = view.findViewById(R.id.progress_layout);

        //Set font
//        textInput_address.setTypeface(Common.setFontOpenSans(getAssets()));

        //Load district and ward
//        if(districtList.size() == 0){
//            getListDistrict();
//        } else {
//            setAdapter(districtList, "---Quận", districtSpinner);
//        }
//        getListWard(0);
//
//        //Set event when click on district spinner
//        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                getListWard(i);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        CustomViewDialog customViewDialog = new CustomViewDialog();

        //Create dialog
        customViewDialog.Builder(context);
        //Set title
        customViewDialog.setTitle("Thêm địa chỉ");
        //Create dialog width two buttons
        customViewDialog.dialogWithTwoButtons();
        //Add custom view
        customViewDialog.addCustomView(view);
        //Set left button text
        customViewDialog.setLeftButtonText("Thêm");
        //Set right button text
        customViewDialog.setRightButtonText("Trở về");
        //Set color for left button
//        customViewDialog.setLeftButtonColor(getResources().getColor(R.color.primaryColor, null));
//        //Set color for right button
//        customViewDialog.setRightButtonColor(getResources().getColor(R.color.gray, null));

        //Set event button
        customViewDialog.onButtonClick(new DialogButtonEvents() {
            @Override
            public void onLeftButtonClick() {

                //Get data
//                address = edt_address.getText().toString();
//                ward = wardSpinner.getSelectedItem().toString();
//                district = districtSpinner.getSelectedItem().toString();
//
//                if(validData(address, ward, district)){
//                    addAddress(address, ward, district);
//                    customViewDialog.dismiss();
//                }
            }

            @Override
            public void onRightButtonClick() {
                customViewDialog.dismiss();
            }
        });

        customViewDialog.show();
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
