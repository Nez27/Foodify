package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Adapter.AddressAdapter;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;
import com.sjapps.library.customdialog.CustomViewDialog;
import com.sjapps.library.customdialog.DialogButtonEvents;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressManagerActivity extends AppCompatActivity {

    private Address defaultAddress = null;
    private RecyclerView recycler_view_address;
    private AddressAdapter adapter;
    private Button add_address_button;
    private TextInputLayout textInput_address;
    private ImageView back_image;
    private TextView txt_Address, errorTextDistrictWard;
    private List<Address> listAddress = new ArrayList<>();
    Spinner wardSpinner, districtSpinner;
    EditText edt_address;
    ConstraintLayout progressLayout, progressLayoutDialog;
    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();
    String address, ward, district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);

        //Init Component
        recycler_view_address = findViewById(R.id.recycler_view_address);
        add_address_button = findViewById(R.id.add_address_button);
        back_image = findViewById(R.id.back_image);
        txt_Address = findViewById(R.id.text_view_address);
        progressLayout = findViewById(R.id.progress_layout);

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_address.setLayoutManager(linearLayoutManager);

        adapter = new AddressAdapter(this);

        getDefaultAddress();

        //Set event image view on click
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        add_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAddressDialog();
            }
        });

    }

    private void getAddressList(){
        listAddress.addAll(Common.CURRENT_USER.getAddresses());

        adapter.setData(listAddress);
        recycler_view_address.setAdapter(adapter);

        progressLayout.setVisibility(View.GONE);
    }

    private void initData(){
        //Create a full address string
        String fullAddress = defaultAddress.getAddress() + ", " + defaultAddress.getWard() + ", " + defaultAddress.getDistrict();

        //Bind data to component
        txt_Address.setText(fullAddress);
    }

    private void getDefaultAddress(){
        for(Address tempAddress: Common.CURRENT_USER.getAddresses()){
            if(Common.CURRENT_USER.getDefaultAddress() == tempAddress.getId()){
                defaultAddress = tempAddress;
                initData();
                getAddressList();
                break;
            }
        }

        if(defaultAddress == null){
            progressLayout.setVisibility(View.GONE);
            Toast.makeText(this, "Đã có lỗi từ hệ thống, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
        }

    }

    private void addAddress(String address, String ward, String district){
        progressLayoutDialog.setVisibility(View.VISIBLE);

        //Create object address
        Address newAddress = new Address(address, ward, district);
        FoodApiToken.apiService.createAddressUser(Common.CURRENT_USER.getId(), newAddress).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                Toast.makeText(AddressManagerActivity.this, "Đã thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                //Add address to list address at local storage
                listAddress.add(newAddress);

                adapter.notifyDataSetChanged();

                progressLayoutDialog.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(AddressManagerActivity.this, "Đã có lỗi từ hệ thống, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddAddressDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.add_address_dialog,null);

        //Init component
        textInput_address = view.findViewById(R.id.textInput_address);
        wardSpinner = view.findViewById(R.id.ward);
        districtSpinner = view.findViewById(R.id.district);
        edt_address = view.findViewById(R.id.edt_address);
        errorTextDistrictWard = view.findViewById(R.id.errorTextDistrictWard);
        progressLayoutDialog = view.findViewById(R.id.progress_layout);

        //Set font
        textInput_address.setTypeface(Common.setFontOpenSans(getAssets()));

        //Load district and ward
        if(districtList.size() == 0){
            getListDistrict();
        } else {
            setAdapter(districtList, "---Quận", districtSpinner);
        }
        getListWard(0);

        //Set event when click on district spinner
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getListWard(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        CustomViewDialog customViewDialog = new CustomViewDialog();

        //Create dialog
        customViewDialog.Builder(this);
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
        customViewDialog.setLeftButtonColor(getResources().getColor(R.color.primaryColor, null));
        //Set color for right button
        customViewDialog.setRightButtonColor(getResources().getColor(R.color.gray, null));

        //Set event button
        customViewDialog.onButtonClick(new DialogButtonEvents() {
            @Override
            public void onLeftButtonClick() {

                //Get data
                address = edt_address.getText().toString();
                ward = wardSpinner.getSelectedItem().toString();
                district = districtSpinner.getSelectedItem().toString();

                if(validData(address, ward, district)){
                    addAddress(address, ward, district);
                    customViewDialog.dismiss();
                }
            }

            @Override
            public void onRightButtonClick() {
                customViewDialog.dismiss();
            }
        });

        customViewDialog.show();
    }

    private boolean validData(String address, String ward, String district){
        //Valid data
        boolean dataHasValidate = true;

        //Check address
        if(address.length() < 8) {
            textInput_address.setError("Trường địa chỉ của bạn tối thiếu từ 8 ký tự trở lên");
            dataHasValidate = false;
        } else {
            textInput_address.setErrorEnabled(false);
        }

        //Check district and ward
        if(district.equals("---Quận") || ward.equals("---Phường")){
            errorTextDistrictWard.setVisibility(View.VISIBLE);

            dataHasValidate = false;
        } else {
            errorTextDistrictWard.setVisibility(View.GONE);
        }

        return dataHasValidate;
    }

    private void getListDistrict() {

        districtList.add("---Quận");

        FoodApi.apiService.districtResponse().enqueue(new Callback<List<DistrictWardResponse>>() {
            @Override
            public void onResponse(Call<List<DistrictWardResponse>> call, Response<List<DistrictWardResponse>> response) {
                List<DistrictWardResponse> districtResponse = response.body();
                if(districtResponse != null){
                    for(DistrictWardResponse tempDistrict: districtResponse){
                        districtList.add(tempDistrict.getName());
                    }
                }

                setAdapter(districtList, "---Quận", districtSpinner);
            }

            @Override
            public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                Toast.makeText(AddressManagerActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListWard(int districtCode) {

        if(wardList.size() > 1)
            wardList.clear();

        if(wardList.size() == 0)
            wardList.add("---Phường");

        wardSpinner.setEnabled(false);

        if(districtCode != 0){
            FoodApi.apiService.wardResponse(districtCode).enqueue(new Callback<List<DistrictWardResponse>>() {
                @Override
                public void onResponse(Call<List<DistrictWardResponse>> call, Response<List<DistrictWardResponse>> response) {
                    List<DistrictWardResponse> wardData = response.body();

                    if(wardData != null){
                        for(DistrictWardResponse tempWard: wardData){
                            wardList.add(tempWard.getName());
                        }
                    }

                    if(wardList.size() <= 1){
                        wardSpinner.setEnabled(false);
                    } else {
                        wardSpinner.setEnabled(true);
                    }
                    setAdapter(wardList, "---Phường", wardSpinner);
                }

                @Override
                public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                    Toast.makeText(AddressManagerActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setAdapter(wardList, "---Phường", wardSpinner);
        }
    }

    private void setAdapter(ArrayList<String> data, String defaultItem, Spinner dataSpinner){
        MySpinnerAdapter adapterWard = new MySpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, data);
        dataSpinner.setAdapter(adapterWard); // this will set list of values to spinner
        dataSpinner.setSelection(data.indexOf(defaultItem));//set selected value in spinner
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "font/bebas.ttf");

        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
    }
}