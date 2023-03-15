package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Adapter.AddressAdapter;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;
import com.sjapps.library.customdialog.CustomViewDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressManagerActivity extends AppCompatActivity {

    private RecyclerView recycler_view_address;
    private AddressAdapter adapter;
    private Button add_address_button;
    private ImageView back_image;

    private List<Address> listAddress = new ArrayList<>();
    Spinner wardSpinner, districtSpinner;

    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);

        //Init Component
        recycler_view_address = findViewById(R.id.recycler_view_address);
        add_address_button = findViewById(R.id.add_address_button);
        back_image = findViewById(R.id.back_image);

        //Set layout recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_address.setLayoutManager(linearLayoutManager);

        adapter = new AddressAdapter();

        getAddressList();

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

        for(int i = 1; i <= 10; i++){
            listAddress.add(new Address(i, "Hẻm 215, đường Phạm Như Xương", "phường Hoà Khánh Nam", "Liên Chiểu"));
        }

        adapter.setData(listAddress);
        recycler_view_address.setAdapter(adapter);
    }

    private void showAddAddressDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.add_address_dialog,null);

        //Init component
        TextInputLayout textInputLayout = view.findViewById(R.id.textInput_address);
        wardSpinner = view.findViewById(R.id.ward);
        districtSpinner = view.findViewById(R.id.district);

        //Set font
        textInputLayout.setTypeface(Common.setFontOpenSans(getAssets()));

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
        customViewDialog.setLeftButtonText("Trở về");
        //Set right button text
        customViewDialog.setRightButtonText("Thêm");
        //Set color for left button
        customViewDialog.setLeftButtonColor(getResources().getColor(R.color.gray, null));
        //Set color for right button
        customViewDialog.setRightButtonColor(getResources().getColor(R.color.primaryColor, null));


        customViewDialog.show();
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