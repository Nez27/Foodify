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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextInputLayout textInput_email;
    EditText edt_email;
    MaterialButton send_code_button;
    ConstraintLayout progress_layout;
    ImageView back_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initComponent();

        mAuth = FirebaseAuth.getInstance();

        //Back button
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        send_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_layout.setVisibility(View.VISIBLE);
                if(checkEmailValid()){
                    mAuth.setLanguageCode("vi");

                    mAuth.fetchSignInMethodsForEmail(edt_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {

                                        Common.notificationDialog(ForgotPasswordActivity.this, DialogStyle.FLAT, DialogType.ERROR, "Thông báo!", "Email " +
                                                "này chưa được đăng ký ở hệ thống! Vui lòng kiểm tra lại!");

                                        progress_layout.setVisibility(View.GONE);
                                    } else {
                                        sendEmail();
                                    }

                                }
                            });
                }
            }
        });

        setFontUI();
    }

    private void sendEmail(){

        mAuth.sendPasswordResetEmail(edt_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Common.notificationDialog(ForgotPasswordActivity.this, DialogStyle.FLAT, DialogType.SUCCESS, "Email đã được gửi", "Vui lòng kiểm tra " +
                            "hộp thư và làm theo hướng dẫn để tiếp tục!");

                    progress_layout.setVisibility(View.GONE);
                }
            }
        });

    }

    private boolean checkEmailValid() {
        boolean dataHasValidate = true;

        //Check phone number
        if (edt_email.getText().toString().isEmpty()) {
            textInput_email.setError("Email không được bỏ trống!");
            dataHasValidate = false;
        } else {

            //Check email valid
            Pattern patternEmail = Pattern.compile(Common.VALID_EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcherEmail = patternEmail.matcher(edt_email.getText().toString());
            if (!matcherEmail.matches()) {
                textInput_email.setError("Địa chỉ email không đúng định dạng. Xin vui lòng kiểm tra lại!");
                dataHasValidate = false;
            } else {
                textInput_email.setErrorEnabled(false);
            }
        }

        return dataHasValidate;
    }
    private void setFontUI() {

        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
    }

    private void initComponent() {
        textInput_email = findViewById(R.id.textInput_email);
        back_image = findViewById(R.id.back_image);
        send_code_button = findViewById(R.id.send_code_button);
        edt_email = findViewById(R.id.edt_email);
        progress_layout = findViewById(R.id.progress_layout);
    }

}