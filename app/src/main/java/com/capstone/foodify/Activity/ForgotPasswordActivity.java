package com.capstone.foodify.Activity;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.capstone.foodify.Common;
import com.capstone.foodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout textInput_email;
    private EditText edt_email;
    private MaterialButton send_code_button;
    private ConstraintLayout progress_layout;
    private ImageView back_image;

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
        setContentView(R.layout.activity_forgot_password);

        initComponent();

        //Check internet connection
        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }

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

        textInput_email.setTypeface(Common.setFontKohoBold(getAssets()));
    }

    private void initComponent() {
        textInput_email = findViewById(R.id.textInput_email);
        back_image = findViewById(R.id.back_image);
        send_code_button = findViewById(R.id.send_code_button);
        edt_email = findViewById(R.id.edt_email);
        progress_layout = findViewById(R.id.progress_layout);
    }

}