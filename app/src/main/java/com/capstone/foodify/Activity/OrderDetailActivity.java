package com.capstone.foodify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.Order;
import com.capstone.foodify.Model.OrderDetail;
import com.capstone.foodify.Adapter.OrderDetailAdapter;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.sjapps.library.customdialog.BasicDialog;
import com.sjapps.library.customdialog.DialogButtonEvents;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String AWAITING = "AWAITING";
    private TextView order_tracking_number, user_name, phone, address, orderTime, total, status;
    private ImageView back_image;
    private List<Basket> listOrderDetails = new ArrayList<>();
    OrderDetailAdapter adapter;
    RecyclerView recyclerView;
    private Order order;
    private Button btn_cancel_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //Init Component
        initComponent();

        //Get data from previous fragment
        if(getIntent() != null)
            order = (Order) getIntent().getSerializableExtra("order");

        if(order.getStatus().equals(AWAITING))
            btn_cancel_order.setVisibility(View.VISIBLE);

        //Init data
        order_tracking_number.setText("Order Id: #" + order.getOrderTrackingNumber());
        user_name.setText("Họ và tên: " + Common.CURRENT_USER.getFullName());
        phone.setText("Số điện thoại: " + Common.CURRENT_USER.getPhoneNumber());
        address.setText("Địa chỉ: " + order.getAddress());
        orderTime.setText("Thời gian đặt: " + order.getOrderTime());
        total.setText("Tổng: " + Common.changeCurrencyUnit(order.getTotal()));
        status.setText("Trạng thái đơn hàng: " + translateStatus(order.getStatus()));


        for(OrderDetail tempFood: order.getOrderDetails()){
            Basket food = new Basket(tempFood.getFood().getId(), tempFood.getFood().getImages().get(0).getImageUrl(),
                    tempFood.getFood().getName(), tempFood.getFood().getCost() ,tempFood.getFood().getShop().getName(), tempFood.getQuantity(),
                    tempFood.getFood().getDiscountPercent(), tempFood.getFood().getShop().getId());
            listOrderDetails.add(food);
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setData(listOrderDetails);
        recyclerView.setAdapter(adapter);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Set event for delete order button
        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialogConfirm();
            }
        });
    }

    private void showDeleteDialogConfirm(){
        BasicDialog dialog = new BasicDialog();

        dialog.Builder(this)
                .setTitle("Xác nhận huỷ đơn hàng này?")
                .setLeftButtonText("Xác nhận")
                .setRightButtonText("Không")
                .setDialogBackgroundColor(getResources().getColor(R.color.white, null))
                .setLeftButtonColor(getResources().getColor(R.color.primaryColor, null))
                .setRightButtonColor(getResources().getColor(R.color.gray, null))
                .onButtonClick(new DialogButtonEvents() {
                    @Override
                    public void onLeftButtonClick() {
                        deleteOrderUser();
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightButtonClick() {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    
    private void deleteOrderUser(){
        FoodApiToken.apiService.updateOrderStatus(Common.CURRENT_USER.getId(), order.getId(), "CANCELED").enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                if(response.code() == 200){
                    new AestheticDialog.Builder(OrderDetailActivity.this, DialogStyle.RAINBOW, DialogType.SUCCESS)
                            .setTitle("Thông báo!")
                            .setMessage("Đã huỷ đơn thành công!")
                            .setCancelable(false)
                            .setOnClickListener(new OnDialogClickListener() {
                                @Override
                                public void onClick(@NonNull AestheticDialog.Builder builder) {

                                    OrderActivity.orderActivity.finish();

                                    startActivity(new Intent(OrderDetailActivity.this, OrderActivity.class));
                                    builder.dismiss();
                                    finish();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Đã có lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Đã có lỗi kết nối đến hệ thống! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initComponent(){
        order_tracking_number = findViewById(R.id.order_tracking_number);
        user_name = findViewById(R.id.user_name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        orderTime = findViewById(R.id.order_time);
        total = findViewById(R.id.total);
        adapter = new OrderDetailAdapter();
        recyclerView = findViewById(R.id.recycler_view_order_detail);
        back_image = findViewById(R.id.back_image);
        btn_cancel_order = findViewById(R.id.cancel_order_button);
        status = findViewById(R.id.status);
    }

    private String translateStatus(String status){
        String statusHasTranslate = null;

        if(status.equals("AWAITING")){
            statusHasTranslate = "Chờ xác nhận";
        }

        if(status.equals("CONFIRMED")){
            statusHasTranslate = "Đã xác nhận";
        }

        if(status.equals("SHIPPING")){
            statusHasTranslate = "Đang giao";
        }

        if(status.equals("COMPLETED")){
            statusHasTranslate = "Đã giao";
        }

        if(status.equals("CANCELED")){
            statusHasTranslate = "Đã huỷ";
        }

        return statusHasTranslate;
    }
}