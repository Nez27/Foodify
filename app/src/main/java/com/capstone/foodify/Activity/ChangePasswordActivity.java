package com.capstone.foodify.Activity;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.foodify.Common;
import com.capstone.foodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout textInput_old_password, textInput_password, textInput_password_confirm;
    private EditText edtOldPassword, edtPassword, edtPasswordConfirm;
    private ImageView back_image;
    private ConstraintLayout progressLayout;

    Button changePasswordButton;

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
        setContentView(R.layout.activity_change_password);

        initComponent();
        setFontUI();

        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }

        //Handle event when back image on click
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show progress bar
                progressLayout.setVisibility(View.VISIBLE);

                if(validateData()){
                    changePassword(edtOldPassword.getText().toString(), edtPasswordConfirm.getText().toString());
                } else {
                    progressLayout.setVisibility(View.GONE);
                }
            }
        });

        textInput_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInput_password.setErrorEnabled(false);
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_password.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_password_confirm.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validateData(){
        boolean dataHasValidate = true;

        //Check password
        Pattern patternPassword = Pattern.compile(Common.PASSWORD_PATTERN);
        Matcher matcherPassword = patternPassword.matcher(edtPassword.getText().toString());

        if(!matcherPassword.matches() || edtPassword.getText().toString().length() < 8) {
            textInput_password.setError("Mật khẩu của bạn cần tối thiểu có 8 ký tự, 1 ký tự viết hoa, 1 số và 1 ký tự đặc biệt!");
            dataHasValidate = false;
        } else {
            textInput_password.setErrorEnabled(false);
        }

        if(!edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString())){
            textInput_password_confirm.setError("Mật khẩu không giống nhau. Vui lòng kiểm tra lại!");
            dataHasValidate = false;
        } else {
            textInput_password_confirm.setErrorEnabled(false);
        }

        return dataHasValidate;
    }

    private void changePassword(String oldPassword, String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(Common.CURRENT_USER.getEmail(), oldPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Common.notificationDialog(ChangePasswordActivity.this, DialogStyle.RAINBOW, DialogType.SUCCESS, "Thành công!", "Đã thay đổi mật khẩu thành công!");

                                        progressLayout.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Đã có lỗi từ hệ thống, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                        progressLayout.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Common.notificationDialog(ChangePasswordActivity.this, DialogStyle.RAINBOW, DialogType.ERROR, "Lỗi!", "Mật khẩu không đúng! Xin vui lòng thử lại!");

                            progressLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initComponent() {
        textInput_old_password = findViewById(R.id.textInput_old_password);
        textInput_password = findViewById(R.id.textInput_password);
        textInput_password_confirm = findViewById(R.id.textInput_password_confirm);

        edtOldPassword = findViewById(R.id.edt_old_password);
        edtPassword = findViewById(R.id.edt_password);
        edtPasswordConfirm = findViewById(R.id.edt_confirmPassword);

        back_image = findViewById(R.id.back_image);

        changePasswordButton = findViewById(R.id.change_password_button);

        progressLayout = findViewById(R.id.progress_layout);
    }

    private void setFontUI() {
        textInput_password.setTypeface(Common.setFontKohoBold(getAssets()));
        textInput_old_password.setTypeface(Common.setFontKohoBold(getAssets()));
        textInput_password_confirm.setTypeface(Common.setFontKohoBold(getAssets()));
    }
}