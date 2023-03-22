package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Adapter.OrderDetailAdapter;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCheckOutActivity extends AppCompatActivity {
    private static String finalAddress = null;
    ImageView back_image;
    OrderDetailAdapter adapter;
    RecyclerView recyclerView;
    TextView txt_userName, txt_phone, edt_address_detect, errorTextDistrictWard, txt_total;
    TextInputLayout textInput_address;
    EditText edt_address;
    Spinner spn_list_address, spn_district, spn_ward;
    ConstraintLayout manual_input_address_layout;
    Button change_address_button, confirm_address_button;
    RadioButton list_address_input, auto_detect_location, manual_input_address, radio_button_selected;
    RadioGroup address_option;
    float total;
    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check_out);

        //Check user is login or not!
        if(Common.CURRENT_USER == null)
            startActivity(new Intent(this, SignInActivity.class));


        //Init Component
        txt_userName = findViewById(R.id.txt_userName);
        txt_phone = findViewById(R.id.txt_phone);
        spn_list_address = findViewById(R.id.list_address);
        spn_district = findViewById(R.id.district);
        spn_ward = findViewById(R.id.ward);
        edt_address_detect = findViewById(R.id.edt_address_detect);
        manual_input_address_layout = findViewById(R.id.manual_input_address_layout);
        adapter = new OrderDetailAdapter();
        recyclerView = findViewById(R.id.list_order);
        back_image = findViewById(R.id.back_image);
        change_address_button = findViewById(R.id.change_address_button);
        confirm_address_button = findViewById(R.id.confirm_address_button);
        list_address_input = findViewById(R.id.list_address_input);
        auto_detect_location = findViewById(R.id.auto_detect_location);
        manual_input_address = findViewById(R.id.manual_input_address);
        edt_address = findViewById(R.id.edt_address);
        address_option = findViewById(R.id.address_option);
        errorTextDistrictWard = findViewById(R.id.errorTextDistrictWard);
        textInput_address = findViewById(R.id.textInput_address);
        txt_total = findViewById(R.id.txt_total);

        if(getIntent() != null){
            total = getIntent().getFloatExtra("total", 0);
            txt_total.setText(Common.changeCurrencyUnit(total));
        }

        //Show list food in basket
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setData(Common.LIST_BASKET_FOOD);
        recyclerView.setAdapter(adapter);

        showListAddressUser();

        //Get list district, ward
        initDataDistrictWard();

        //Set user name and phone
        txt_userName.setText(Common.CURRENT_USER.getFullName());
        txt_phone.setText(Common.CURRENT_USER.getPhoneNumber());

        //Set event for change address button
        change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_address_button.setEnabled(true);
                change_address_button.setVisibility(View.GONE);

                enabledAllInputAddressOption();
            }
        });

        //Set event for confirm address button
        confirm_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedOptionId = address_option.getCheckedRadioButtonId();

                radio_button_selected = findViewById(selectedOptionId);

                if(radio_button_selected == list_address_input){
                    //Action 1
                    finalAddress = spn_list_address.getSelectedItem().toString();
                }

                if(radio_button_selected == auto_detect_location){
                    //Action 2

                }

                if(radio_button_selected == manual_input_address){
                    //Action 3
                    if(validForm()){
                        if(spn_district.getSelectedItem().toString().equals("Huyện Hoàng Sa")){
                            finalAddress = edt_address.getText().toString() + ", " + spn_district.getSelectedItem().toString();
                        } else {
                            finalAddress = edt_address.getText().toString() + ", " + spn_ward.getSelectedItem().toString() + ", " + spn_district.getSelectedItem().toString();
                        }
                    }
                }

                if(finalAddress == null){
                    Toast.makeText(OrderCheckOutActivity.this, "Bạn chưa chọn địa chỉ!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderCheckOutActivity.this, finalAddress, Toast.LENGTH_SHORT).show();

                    confirm_address_button.setEnabled(false);
                    change_address_button.setVisibility(View.VISIBLE);

                    disableAllInputAddressOption();
                }
            }
        });

        //Set event for back icon
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean validForm(){
        //Valid data
        boolean dataHasValidate = true;

        //Check address
        if(edt_address.getText().toString().length() < 8) {
            textInput_address.setError("Trường địa chỉ của bạn tối thiếu từ 8 ký tự trở lên");
            dataHasValidate = false;
        } else {
            textInput_address.setErrorEnabled(false);
        }

        //Check district and ward
        if(spn_district.getSelectedItem().toString().equals("---Quận") || spn_ward.getSelectedItem().toString().equals("---Phường")){

            if(!spn_district.getSelectedItem().toString().equals("Huyện Hoàng Sa")){
                errorTextDistrictWard.setVisibility(View.VISIBLE);
                dataHasValidate = false;
            } else{
                errorTextDistrictWard.setVisibility(View.GONE);
            }

        } else {
            errorTextDistrictWard.setVisibility(View.GONE);
        }

        return dataHasValidate;
    }

    private void initDataDistrictWard(){
        if(districtList.size() == 0){
            getListDistrict();
        } else {
            setAdapter(districtList, "---Quận", spn_district);
        }
        getListWard(0);

        //Add event for district spinner
        spn_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getListWard(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void disableAllInputAddressOption(){
        list_address_input.setEnabled(false);
        auto_detect_location.setEnabled(false);
        manual_input_address.setEnabled(false);

        spn_list_address.setEnabled(false);
        edt_address.setEnabled(false);
        spn_ward.setEnabled(false);
        spn_district.setEnabled(false);
    }

    private void enabledAllInputAddressOption(){
        list_address_input.setEnabled(true);
        auto_detect_location.setEnabled(true);
        manual_input_address.setEnabled(true);

        spn_list_address.setEnabled(true);
        edt_address.setEnabled(true);
        spn_ward.setEnabled(true);
        spn_district.setEnabled(true);
    }

    private void showListAddressUser(){
        List<String> listAddress = new ArrayList<>();
        String defaultAddress = "";

        //Get all list address
        for(Address tempAddress: Common.CURRENT_USER.getAddresses()){
            String fullAddress;

            if(tempAddress.getWard().equals("---Phường")){
                fullAddress = tempAddress.getAddress() + ", " + tempAddress.getDistrict();
            } else {
                fullAddress = tempAddress.getAddress() + ", " + tempAddress.getWard() + ", " + tempAddress.getDistrict();
            }

            listAddress.add(fullAddress);

            if(tempAddress.getId() == Common.CURRENT_USER.getDefaultAddress()){
                defaultAddress = fullAddress;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listAddress);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spn_list_address.setAdapter(adapter);
        spn_list_address.setSelection(listAddress.indexOf(defaultAddress));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.list_address_input:
                if (checked){
                    //If option 1 is checked
                    //Config UI
                    spn_list_address.setVisibility(View.VISIBLE);

                    edt_address_detect.setVisibility(View.GONE);
                    manual_input_address_layout.setVisibility(View.GONE);

                    //Reset address;
                    finalAddress = null;
                }
                break;
            case R.id.auto_detect_location:
                if (checked){
                    //If option 2 is checked
                    //Config UI
                    edt_address_detect.setVisibility(View.VISIBLE);

                    spn_list_address.setVisibility(View.GONE);
                    manual_input_address_layout.setVisibility(View.GONE);

                    //Reset address;
                    finalAddress = null;
                }
                break;
            case R.id.manual_input_address:
                if (checked){
                    //If option 3 is checked
                    //Config UI
                    manual_input_address_layout.setVisibility(View.VISIBLE);

                    spn_list_address.setVisibility(View.GONE);
                    edt_address_detect.setVisibility(View.GONE);

                    //Reset address;
                    finalAddress = null;
                }
                break;
        }
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

                setAdapter(districtList, "---Quận", spn_district);
            }

            @Override
            public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                Toast.makeText(OrderCheckOutActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListWard(int districtCode) {

        if(wardList.size() > 1)
            wardList.clear();

        if(wardList.size() == 0)
            wardList.add("---Phường");

        spn_ward.setEnabled(false);

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
                        spn_ward.setEnabled(false);
                    } else {
                        spn_ward.setEnabled(true);
                    }

                    setAdapter(wardList, "---Phường", spn_ward);
                }

                @Override
                public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                    Toast.makeText(OrderCheckOutActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setAdapter(wardList, "---Phường", spn_ward);
        }
    }

    private void setAdapter(ArrayList<String> data, String defaultItem, Spinner dataSpinner){
        OrderCheckOutActivity.MySpinnerAdapter adapterWard = new OrderCheckOutActivity.MySpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, data);
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