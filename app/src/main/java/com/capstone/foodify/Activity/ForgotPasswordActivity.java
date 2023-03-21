package com.capstone.foodify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputLayout textInput_phone;
    EditText edt_phone;
    MaterialButton send_code_button;
    ConstraintLayout progress_layout;
    ImageView back_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initComponent();

        //Back button
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        send_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findUserByPhone(edt_phone.getText().toString());
            }
        });

        setFontUI();
    }

    private void findUserByPhone(String phone){

        User user = new User();
        user.setPhoneNumber(phone);

        FoodApi.apiService.checkEmailOrPhoneExist(user).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                CustomResponse responseData = response.body();

                if(responseData != null){
                    if(responseData.isTrue()){
                        //If this phone number has already register in server, send the code

                    } else {
                        //If this phone number
                        Toast.makeText(ForgotPasswordActivity.this, "Hệ thống hiện chưa tìm thấy số điện thoại này, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {

            }
        });
    }

    private void sendCode(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("vi");
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(Common.PHONE_CODE + edt_phone.getText().toString())
                .setTimeout(0L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {


                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyAccountActivity.class);
                        intent.putExtra("phone", edt_phone.getText().toString());
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra(Common.IS_FORGOT_PASSWORD, Common.IS_FORGOT_PASSWORD);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ...
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setFontUI() {
        textInput_phone = (TextInputLayout) findViewById(R.id.textInput_phone);
        textInput_phone.setTypeface(Common.setFontBebas(getAssets()));
    }

    private void initComponent() {
        back_image = (ImageView) findViewById(R.id.back_image);
        send_code_button = (MaterialButton) findViewById(R.id.send_code_button);
        edt_phone = findViewById(R.id.edt_phone);
        progress_layout = findViewById(R.id.progress_layout);
    }

}