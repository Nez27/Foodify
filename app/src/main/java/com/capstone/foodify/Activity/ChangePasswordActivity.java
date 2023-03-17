package com.capstone.foodify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Typeface;
import android.os.Bundle;
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
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputLayout textInput_old_password, textInput_password, textInput_password_confirm;
    EditText edtOldPassword, edtPassword, edtPasswordConfirm;
    ImageView back_image;
    ConstraintLayout progressLayout;

    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initComponent();
        setFontUI();

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
                                        new AestheticDialog.Builder(ChangePasswordActivity.this, DialogStyle.RAINBOW, DialogType.SUCCESS)
                                                .setTitle("Thàng công!")
                                                .setMessage("Đã thay đổi mật khẩu thành công!")
                                                .setCancelable(true)
                                                .show();

                                        progressLayout.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Đã có lỗi từ hệ thống, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                        progressLayout.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            new AestheticDialog.Builder(ChangePasswordActivity.this, DialogStyle.RAINBOW, DialogType.ERROR)
                                    .setTitle("Lỗi!")
                                    .setMessage("Mật khẩu không đúng! Xin vui lòng thử lại!")
                                    .setCancelable(true)
                                    .show();
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
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
        textInput_old_password.setTypeface(Common.setFontBebas(getAssets()));
        textInput_password_confirm.setTypeface(Common.setFontBebas(getAssets()));
    }
}