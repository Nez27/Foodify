package com.capstone.foodify.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Adapter.AddressAdapter;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;
import com.sjapps.library.customdialog.BasicDialog;
import com.sjapps.library.customdialog.CustomViewDialog;
import com.sjapps.library.customdialog.DialogButtonEvents;
import com.sjapps.library.customdialog.SJDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private ImageView back_image, edit_btn;
    private TextView txt_Address, errorTextDistrictWard;
    private List<Address> listAddress = new ArrayList<>();
    Spinner wardSpinner, districtSpinner;
    EditText edt_address;
    ConstraintLayout progressLayout, progressLayoutDialog;
    CheckBox default_address_checkbox;
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
        edit_btn = findViewById(R.id.edit_button);

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
                showAddAndEditAddressDialog(null);
            }
        });

        //Set event when user click on edit button
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAndEditAddressDialog(defaultAddress);
            }
        });

    }

    private void getAddressList(){
        listAddress.clear();

        listAddress.addAll(Common.CURRENT_USER.getAddresses());

        //Remove default address in address list
        for(Address tempAddress: Common.CURRENT_USER.getAddresses()){
            if(Common.CURRENT_USER.getDefaultAddress() == tempAddress.getId()){
                listAddress.remove(tempAddress);
                break;
            }
        }

        adapter.setData(listAddress);
        recycler_view_address.setAdapter(adapter);

        progressLayout.setVisibility(View.GONE);
    }

    private void initDefaultAddress(){
        //Create a full address string
        String fullAddress = defaultAddress.getAddress() + ", " + defaultAddress.getWard() + ", " + defaultAddress.getDistrict();

        //Bind data to component
        txt_Address.setText(fullAddress);
    }

    private void getDefaultAddress(){
        for(Address tempAddress: Common.CURRENT_USER.getAddresses()){
            if(Common.CURRENT_USER.getDefaultAddress() == tempAddress.getId()){
                defaultAddress = tempAddress;
                initDefaultAddress();
                getAddressList();
                break;
            }
        }

        if(defaultAddress == null){
            progressLayout.setVisibility(View.GONE);
            Toast.makeText(this, "Không lấy được default address!", Toast.LENGTH_SHORT).show();
        }

    }

    private void addAddress(String address, String ward, String district, boolean isChecked){
        progressLayoutDialog.setVisibility(View.VISIBLE);

        //Create object address
        Address newAddress = new Address(address, ward, district);
        FoodApiToken.apiService.createAddressUser(Common.CURRENT_USER.getId(), newAddress).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {

                CustomResponse responseData = response.body();

                if(responseData.getTitle().equals("Create address")){
                    Toast.makeText(AddressManagerActivity.this, "Đã thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                    //Update data user
                    updateUser(isChecked);
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(AddressManagerActivity.this, "Đã có lỗi từ hệ thống, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteAddress(int addressId){

        FoodApiToken.apiService.deleteAddressUser(Common.CURRENT_USER.getId(), addressId).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                Toast.makeText(AddressManagerActivity.this, "Đã xoá địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                updateUser(false);
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(AddressManagerActivity.this, "Đã có lỗi từ hệ thống! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeDefaultAddress(int idAddress){

        Common.CURRENT_USER.setDefaultAddress(idAddress);

        FoodApiToken.apiService.updateUser(Common.CURRENT_USER.getId(), Common.CURRENT_USER).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(AddressManagerActivity.this, "Đã thay đổi địa chỉ mặc định thành công!", Toast.LENGTH_SHORT).show();

                //Update default address
                getDefaultAddress();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AddressManagerActivity.this, "Đã có lỗi khi thay đổi địa chỉ mặc định, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(boolean isChecked){
        //Get user from database
        FoodApiToken.apiService.getUserFromEmail(Common.CURRENT_USER.getEmail()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userData = response.body();
                if(userData != null){
                    Common.CURRENT_USER = userData;
                    getAddressList();

                    if(isChecked){
                        List<Address> tempListAddress = Common.CURRENT_USER.getAddresses();
                        Collections.sort(tempListAddress, Comparator.comparingLong(Address::getId));

                        Address newDefaultAddress = tempListAddress.get(tempListAddress.size() - 1);

                        changeDefaultAddress(newDefaultAddress.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    public void showConfirmDeleteDialog(int addressId, String message){
        BasicDialog basicDialog = new BasicDialog();

        basicDialog.Builder(this)
                .setTitle("Xác nhận xoá?")
                .setMessage("Địa chỉ: \"" + message + "\"?")
                .setMessageAlignment(SJDialog.TEXT_ALIGNMENT_CENTER)
                .setLeftButtonText("Xoá")
                .setRightButtonText("Không")
                .setLeftButtonColor(getResources().getColor(R.color.primaryColor, null))
                .setRightButtonColor(getResources().getColor(R.color.gray, null))
                .setLeftButtonTextColor(getResources().getColor(R.color.white, null))
                .setRightButtonTextColor(getResources().getColor(R.color.white, null))
                .onButtonClick(new DialogButtonEvents() {
                    @Override
                    public void onLeftButtonClick() {
                        deleteAddress(addressId);
                        basicDialog.dismiss();
                    }

                    @Override
                    public void onRightButtonClick() {
                        basicDialog.dismiss();
                    }
                }).show();
    }

    private void editAddress(int userId, Address address, int oldAddressId, boolean isChecked){

        //Change id address to 0 to call api from back-end
        address.setId(0);

        FoodApiToken.apiService.editAddressUser(userId, oldAddressId, address).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {

                if(response.code() == 200){
                    //Back-end create another address with another id
                    //Update user
                    updateUser(isChecked);
                    Toast.makeText(AddressManagerActivity.this, "Đã thay đổi địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                } else if(response.code() == 400){
                    //Address is exist in user
                    //Check if checkbox default address is has been checked
                    if(isChecked){

                        //Restore id old address
                        for(Address tempAddress: Common.CURRENT_USER.getAddresses()){
                            if(tempAddress.getId() == 0){
                                tempAddress.setId(oldAddressId);
                            }
                        }

                        //Change default address
                        changeDefaultAddress(oldAddressId);
                    } else {
                        //Show notification address has been exist in user
                        Toast.makeText(AddressManagerActivity.this, "Địa chỉ đã bị trùng trong tài khoản của bạn, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    }
                }
                    
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {

            }
        });
    }

    public void showAddAndEditAddressDialog(Address oldAddress){
        View view = LayoutInflater.from(this).inflate(R.layout.add_address_dialog,null);

        //Init component
        textInput_address = view.findViewById(R.id.textInput_address);
        wardSpinner = view.findViewById(R.id.ward);
        districtSpinner = view.findViewById(R.id.district);
        edt_address = view.findViewById(R.id.edt_address);
        errorTextDistrictWard = view.findViewById(R.id.errorTextDistrictWard);
        progressLayoutDialog = view.findViewById(R.id.progress_layout);
        default_address_checkbox = view.findViewById(R.id.checkbox_default_address);


        //Set font
        textInput_address.setTypeface(Common.setFontOpenSans(getAssets()));

        //Set event when click on district spinner
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(oldAddress != null){
                    getListWard(i, oldAddress.getWard());
                } else {
                    getListWard(i, null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        CustomViewDialog customViewDialog = new CustomViewDialog();

        //Create dialog
        customViewDialog.Builder(this);


        if(oldAddress == null){
            //If this is the create address form

            //Set title
            customViewDialog.setTitle("Thêm địa chỉ");
            //Set left button text
            customViewDialog.setLeftButtonText("Thêm");

            //Load district and ward
            if(districtList.size() == 0){
                getListDistrict("---Quận");
            } else {
                setAdapter(districtList, "---Quận", districtSpinner);
            }
            getListWard(0, "---Phường");
        } else {
            //If this is a edit form

            //Set title
            customViewDialog.setTitle("Thay đổi địa chỉ");
            //Set left button text
            customViewDialog.setLeftButtonText("Sửa");
            //If this is a default address, hide checkbox default address
            if(oldAddress.getId() == Common.CURRENT_USER.getDefaultAddress())
                default_address_checkbox.setVisibility(View.GONE);

            //Set default value ward and district depend on information from user
            edt_address.setText(oldAddress.getAddress());

            if(districtList.size() == 0){
                getListDistrict(oldAddress.getDistrict());
            } else {
                setAdapter(districtList, oldAddress.getDistrict(), districtSpinner);
            }
        }

        //Create dialog width two buttons
        customViewDialog.dialogWithTwoButtons();
        //Add custom view
        customViewDialog.addCustomView(view);

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

                if(oldAddress == null){
                    //If this is the create address form

                    if(validData(address, ward, district)){
                        addAddress(address, ward, district, default_address_checkbox.isChecked());
                        customViewDialog.dismiss();
                    }
                } else {
                    //If this is a edit form
                    if(validData(oldAddress.getAddress(), oldAddress.getWard(), oldAddress.getDistrict())){

                        //Change information address
                        oldAddress.setAddress(address);
                        oldAddress.setWard(ward);
                        oldAddress.setDistrict(district);

                        //Call api to change address
                        editAddress(Common.CURRENT_USER.getId(), oldAddress, oldAddress.getId(), default_address_checkbox.isChecked());
                    }

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

            if(!district.equals("Huyện Hoàng Sa")){
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

    private void getListDistrict(String defaultValue) {

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

                setAdapter(districtList, defaultValue, districtSpinner);
            }

            @Override
            public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                Toast.makeText(AddressManagerActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListWard(int districtCode, String defaultValue) {

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
                    setAdapter(wardList, defaultValue, wardSpinner);
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