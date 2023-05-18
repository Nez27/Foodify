package com.capstone.foodify.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.API.GoogleMapApi;
import com.capstone.foodify.Adapter.OrderDetailAdapter;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.Model.GoogleMap.GoogleMapResponse;
import com.capstone.foodify.Model.GoogleMap.Location;
import com.capstone.foodify.Model.Order;
import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.R;
import com.capstone.foodify.ZaloPay.Api.CreateOrder;
import com.capstone.foodify.ZaloPay.Helper.Helpers;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderCheckOutActivity extends AppCompatActivity {
    private static final String TAG = "OrderCheckOutActivity";
    private static final String TAKE_FOOD_FROM_SHOP = "Đến shop lấy";
    private static final String ZALO_PAYMENT_METHOD = "ZALO PAY";
    private static final String CASH_PAYMENT = "CASH";
    private static Double LAT_SHOP = 0.0;
    private static Double LNG_SHOP = 0.0;
    private static String finalAddress, tempAddress = null;
    private static double finalLat, finalLng = 0;
    private ImageView back_image;
    private OrderDetailAdapter adapter;
    private RecyclerView recyclerView;
    private TextView txt_userName, txt_phone, edt_address_detect, errorTextDistrict, errorTextWard, txt_total, txt_distance, txt_ship_cost;
    private TextInputLayout textInput_address;
    private EditText edt_address;
    private Spinner spn_list_address, spn_district, spn_ward;
    private ConstraintLayout manual_input_address_layout;
    private Button change_address_button, confirm_address_button, btn_ZaloPay, btn_Pay;
    private RadioButton list_address_input, auto_detect_location, manual_input_address, radio_button_selected, take_food_from_shop;
    private RadioGroup address_option;
    private ConstraintLayout progress_layout;
    private float total, totalProduct, shipCost;
    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();

    private void initComponent(){
        txt_userName = findViewById(R.id.txt_userName);
        txt_phone = findViewById(R.id.txt_phone);
        spn_list_address = findViewById(R.id.list_address);
        spn_district = findViewById(R.id.district);
        spn_ward = findViewById(R.id.ward);
        edt_address_detect = findViewById(R.id.edt_address_detect);
        manual_input_address_layout = findViewById(R.id.manual_input_address_layout);
        adapter = new OrderDetailAdapter(OrderCheckOutActivity.this);
        recyclerView = findViewById(R.id.list_order);
        back_image = findViewById(R.id.back_image);
        change_address_button = findViewById(R.id.change_address_button);
        confirm_address_button = findViewById(R.id.confirm_address_button);
        list_address_input = findViewById(R.id.list_address_input);
        auto_detect_location = findViewById(R.id.auto_detect_location);
        manual_input_address = findViewById(R.id.manual_input_address);
        edt_address = findViewById(R.id.edt_address);
        address_option = findViewById(R.id.address_option);
        errorTextDistrict = findViewById(R.id.errorTextDistrict);
        errorTextWard = findViewById(R.id.errorTextWard);
        textInput_address = findViewById(R.id.textInput_address);
        txt_total = findViewById(R.id.txt_total);
        take_food_from_shop = findViewById(R.id.take_food_from_shop);
        progress_layout = findViewById(R.id.progress_layout);
        txt_distance = findViewById(R.id.txt_distance);
        txt_ship_cost = findViewById(R.id.txt_ship_cost);
        btn_ZaloPay = findViewById(R.id.btnZaloPay);
        btn_Pay = findViewById(R.id.btnPay);
    }

    @IntRange(from = 0, to = 3)
    public int getConnectionType() {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check_out);

        //Check user is login or not!
        if(Common.CURRENT_USER == null)
            startActivity(new Intent(this, SignInActivity.class));

        //Check internet connection
        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }


        //Init Component
        initComponent();

        //Reload user data
        Common.reloadUser(this);

        //Check current location is null or not
        if(Common.CURRENT_LOCATION == null){
            auto_detect_location.setEnabled(false);

            Common.notificationDialog(OrderCheckOutActivity.this, DialogStyle.FLAT, DialogType.INFO, "Thông báo", "Tính năng \"Lấy vị trí theo thiết bị của bạn\" sẽ không khả dụng do ứng dụng chưa được cấp quyền vị trí!");
        }


        //Get intent from previous activity
        if(getIntent() != null){
            totalProduct = getIntent().getFloatExtra("total", 0);
            txt_total.setText(Common.changeCurrencyUnit(total));
        }


        getLocationShop();

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

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

                finalAddress = null;

                confirm_address_button.setVisibility(View.VISIBLE);
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
                    if(tempAddress == null)
                        auto_detect_location.setText("Không thể lấy được vị trí! Vui lòng thử lại sau");
                    else
                        finalAddress = tempAddress;
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

                if(radio_button_selected == take_food_from_shop){
                    //Action 4
                    finalAddress = TAKE_FOOD_FROM_SHOP;
                }

                if(finalAddress == null){
                    Toast.makeText(OrderCheckOutActivity.this, "Bạn chưa xác nhận địa chỉ!", Toast.LENGTH_SHORT).show();
                } else {
                    progress_layout.setVisibility(View.VISIBLE);
                    confirm_address_button.setVisibility(View.GONE);
                    change_address_button.setVisibility(View.VISIBLE);

                    disableAllInputAddressOption();

                    if(radio_button_selected != take_food_from_shop){

                        if(LAT_SHOP != 0.0 && LNG_SHOP != 0.0){
                            total = 0;
                            getDistanceAndCalculateShipCost(finalAddress);

                            btn_Pay.setEnabled(true);
                            btn_ZaloPay.setEnabled(true);
                        } else {

                            Common.notificationDialog(OrderCheckOutActivity.this, DialogStyle.FLAT, DialogType.INFO, "Thông báo!", "Chưa thể lấy được vị trí shop, vui lòng thử lại hoặc có thể chọn \"Đến shop lấy\" để tiếp tục!");

                            progress_layout.setVisibility(View.GONE);

                            btn_Pay.setEnabled(false);
                            btn_ZaloPay.setEnabled(false);
                        }

                    } else {
                        //Show distance to user
                        txt_distance.setText("0 km");

                        txt_ship_cost.setText(Common.changeCurrencyUnit(0));

                        //Change total value
                        total = totalProduct;

                        txt_total.setText(Common.changeCurrencyUnit(total));
                        progress_layout.setVisibility(View.GONE);

                        btn_Pay.setEnabled(true);
                        btn_ZaloPay.setEnabled(true);
                    }

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

        initZaloPaymentMethod();

        //Set event for button pay by CASH
        btn_Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_layout.setVisibility(View.VISIBLE);

                if(!Common.CURRENT_USER.isLocked()){
                    if(finalAddress != null){
                        //Create order tracking number
                        createOrderFood(Helpers.getAppTransId(), CASH_PAYMENT);
                    } else {
                        Toast.makeText(OrderCheckOutActivity.this, "Bạn chưa xác nhận địa chỉ!", Toast.LENGTH_SHORT).show();
                        progress_layout.setVisibility(View.GONE);
                    }
                } else {
                    //Show notification account locked
                    FirebaseAuth.getInstance().signOut();
                    Common.showErrorServerNotification(OrderCheckOutActivity.this, "Tài khoản của bạn đã bị khoá!");
                }

            }
        });
    }

    private void getAddress() {

        if(Common.tempCurrentAddressUser == null){
            GoogleMapApi.apiService.getAddress(Common.CURRENT_LOCATION.getLatitude() + "," + Common.CURRENT_LOCATION.getLongitude(), Common.MAP_API).enqueue(new Callback<GoogleMapResponse>() {
                @Override
                public void onResponse(Call<GoogleMapResponse> call, Response<GoogleMapResponse> response) {
                    if(response.code() == 200){
                        GoogleMapResponse responseData = response.body();

                        String address = responseData.getResults().get(0).getFormattedAddress();

                        Common.tempCurrentAddressUser = address;

                        edt_address_detect.setText(address);

                        tempAddress = address;
                        progress_layout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<GoogleMapResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            edt_address_detect.setText(Common.tempCurrentAddressUser);

            tempAddress = Common.tempCurrentAddressUser;
            progress_layout.setVisibility(View.GONE);
        }

    }

    private void initZaloPaymentMethod(){

        // handle CreateOrder
        btn_ZaloPay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                progress_layout.setVisibility(View.VISIBLE);


                if(!Common.CURRENT_USER.isLocked()){
                    //Create order
                    //Check address is null or not
                    if(finalAddress != null){

                        CreateOrder orderApi = new CreateOrder();

                        //Create order tracking number
                        String orderTrackingNumber = Helpers.getAppTransId();
                        try {
                            JSONObject data = orderApi.createOrder(String.valueOf((int) total), orderTrackingNumber);
                            String code = data.getString("return_code");

                            if (code.equals("1")) {
                                //Success create order
                                String token = data.getString("zp_trans_token");

                                ZaloPaySDK.getInstance().payOrder(OrderCheckOutActivity.this, token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                createOrderFood(orderTrackingNumber, ZALO_PAYMENT_METHOD);
                                            }

                                        });
                                    }

                                    @Override
                                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                        Common.notificationDialog(OrderCheckOutActivity.this, DialogStyle.RAINBOW, DialogType.INFO, "Thông báo!", "Huỷ thanh toán thành công!");
                                        progress_layout.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                        new AestheticDialog.Builder(OrderCheckOutActivity.this, DialogStyle.FLAT, DialogType.ERROR)
                                                .setTitle("Thanh toán chưa thành công!")
                                                .setMessage("Xin vui lòng thử lại!")
                                                .setCancelable(true)
                                                .setOnClickListener(new OnDialogClickListener() {
                                                    @Override
                                                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                                                        startActivity(new Intent(OrderCheckOutActivity.this, MainActivity.class));
                                                        builder.dismiss();
                                                        finish();
                                                    }
                                                })
                                                .show();
                                        progress_layout.setVisibility(View.GONE);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(OrderCheckOutActivity.this, "Bạn chưa xác nhận địa chỉ!", Toast.LENGTH_SHORT).show();
                        progress_layout.setVisibility(View.GONE);
                    }

                } else {
                    //Show notification account locked
                    FirebaseAuth.getInstance().signOut();
                    Common.showErrorServerNotification(OrderCheckOutActivity.this, "Tài khoản của bạn đã bị khoá!");
                }

                progress_layout.setVisibility(View.GONE);
            }
        });
    }

    private void createOrderFood(String transactionId, String paymentMethod) {

        //Create list order detail object
        List<OrderDetail> listOrderDetail = new ArrayList<>();
        for(Basket tempFood: Common.LIST_BASKET_FOOD){
            OrderDetail orderTemp = new OrderDetail(Integer.parseInt(tempFood.getId()), tempFood.getQuantity());
            listOrderDetail.add(orderTemp);
        }

        //Create order object
        Order order = new Order(transactionId, paymentMethod, shipCost, "AWAITING" , finalAddress, listOrderDetail, String.valueOf(finalLat), String.valueOf(finalLng));

        FoodApiToken.apiService.createOrder(Common.CURRENT_USER.getId(), order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.code() == 201){
                    new AestheticDialog.Builder(OrderCheckOutActivity.this, DialogStyle.FLAT, DialogType.SUCCESS)
                            .setTitle("Thành công!")
                            .setMessage("Bạn đã đặt hàng thành công cho đơn hàng #" + transactionId)
                            .setCancelable(false)
                            .setOnClickListener(new OnDialogClickListener() {
                                @Override
                                public void onClick(@NonNull AestheticDialog.Builder builder) {
                                    Common.LIST_BASKET_FOOD.clear();
                                    Common.FINAL_SHOP = null;

                                    startActivity(new Intent(OrderCheckOutActivity.this, OrderActivity.class));
                                    builder.dismiss();
                                    finish();
                                }
                            }).show();
                } else {
                    Toast.makeText(OrderCheckOutActivity.this, "Đã có lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getDistanceAndCalculateShipCost(String address) {
        //Get location from address
        if(!address.equals(TAKE_FOOD_FROM_SHOP)){
            GoogleMapApi.apiService.getGeoCode(address, Common.MAP_API).enqueue(new Callback<GoogleMapResponse>() {
                @Override
                public void onResponse(Call<GoogleMapResponse> call, Response<GoogleMapResponse> response) {
                    GoogleMapResponse jsonResponse = response.body();

                    Double lat = jsonResponse.getResults().get(0).getGeometry().getLocation().getLat();
                    Double lng = jsonResponse.getResults().get(0).getGeometry().getLocation().getLng();

                    finalLat = lat;
                    finalLng = lng;

                    LatLng addressFrom = new LatLng(lat, lng);
                    LatLng addressTo = new LatLng(LAT_SHOP, LNG_SHOP);

                    int distance = CalculationByDistance(addressFrom, addressTo);

                    //Show distance to user
                    txt_distance.setText(distance + " km");

                    //Calculate ship cost
                    if(distance <= 3){
                        shipCost = 15000;
                    } else if(distance <= 10){
                        shipCost = 5000 * (float) distance;
                    } else {
                        shipCost = 3000 * (float) distance;
                    }

                    txt_ship_cost.setText(Common.changeCurrencyUnit(shipCost));

                    //Update total order
                    total = totalProduct + shipCost;
                    txt_total.setText(Common.changeCurrencyUnit(total));

                    progress_layout.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<GoogleMapResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            progress_layout.setVisibility(View.GONE);
        }

    }

    private void getLocationShop(){
        FoodApi.apiService.getShopById(Common.LIST_BASKET_FOOD.get(0).getShopId()).enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                if(response.code() == 200){
                    Shop shopTemp = response.body();
                    
                    LAT_SHOP = shopTemp.getLat();
                    LNG_SHOP = shopTemp.getLng();
                } else {
                    Toast.makeText(OrderCheckOutActivity.this, "Lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }

    public int CalculationByDistance(LatLng from, LatLng to) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = from.latitude;
        double lat2 = to.latitude;
        double lon1 = from.longitude;
        double lon2 = to.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        DecimalFormat newFormat = new DecimalFormat("####");

        return Integer.valueOf(newFormat.format(valueResult));
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

        //Check ward
        if(spn_ward.getSelectedItem().toString().equals("---Phường")){
            errorTextWard.setVisibility(View.VISIBLE);

            dataHasValidate = false;
        } else {
            errorTextWard.setVisibility(View.GONE);
        }

        //Check district
        if(spn_district.getSelectedItem().toString().equals("---Quận")){
            errorTextDistrict.setVisibility(View.VISIBLE);

            dataHasValidate = false;
        } else {
            errorTextDistrict.setVisibility(View.GONE);
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
        take_food_from_shop.setEnabled(false);

        spn_list_address.setEnabled(false);
        edt_address.setEnabled(false);
        spn_ward.setEnabled(false);
        spn_district.setEnabled(false);
    }

    private void enabledAllInputAddressOption(){
        list_address_input.setEnabled(true);
        take_food_from_shop.setEnabled(true);
        manual_input_address.setEnabled(true);
        if(Common.CURRENT_LOCATION != null){
            //If current location not null;
            auto_detect_location.setEnabled(true);
        }

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
                    progress_layout.setVisibility(View.VISIBLE);
                    if(Common.CURRENT_LOCATION != null)
                        getAddress();
                    else
                        edt_address_detect.setText("Không thể lấy được vị trí do bạn chưa cấp quyền ứng dụng!");

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

            case R.id.take_food_from_shop:
                if(checked){
                    manual_input_address_layout.setVisibility(View.GONE);
                    spn_list_address.setVisibility(View.GONE);
                    edt_address_detect.setVisibility(View.GONE);

                    //Reset address;
                    finalAddress = null;
                }
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
                Log.e(TAG, t.toString());
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
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            setAdapter(wardList, "---Phường", spn_ward);
        }
    }

    private void setAdapter(ArrayList<String> data, String defaultItem, Spinner dataSpinner){
        OrderCheckOutActivity.MySpinnerAdapter adapterWard = new OrderCheckOutActivity.MySpinnerAdapter(getApplicationContext(), R.layout.spinner_text_view, data);
        dataSpinner.setAdapter(adapterWard); // this will set list of values to spinner
        dataSpinner.setSelection(data.indexOf(defaultItem));//set selected value in spinner
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "font/koho_bold.ttf");

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}