package com.capstone.foodify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;

import java.util.concurrent.TimeUnit;

public class VerifyAccountActivity extends AppCompatActivity {
    private static final String PHONE_CODE = "+84";

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    ImageView close_image;
    MaterialButton confirm_code;
    String verificationId, phone = null;
    ConstraintLayout progressLayout;
    TextView resendCodeText, resendCodeTextButton, countDown, errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        initComponent();

        //Get code
        if(getIntent() != null){
            verificationId = getIntent().getStringExtra("verificationId");
            phone = getIntent().getStringExtra("phone");
        }


        //Init text changedListener
        textChangedListenerEditTextOTP();

        //Add event to confirm button
        confirm_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCode();
            }
        });

        //Add event to close icon
        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyAccountActivity.this, SignUpActivity.class));
            }
        });

        //Count down resend code
        CountDownTimer count = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDown.setText(millisUntilFinished / 1000 + " giây");
            }

            @Override
            public void onFinish() {
                showResendCode();
            }
        };
        count.start();

        //Add event to resend button
        resendCodeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideResendCode();
                resendCode(phone);
                count.start();
            }
        });
    }

    private void showResendCode(){
        resendCodeTextButton.setVisibility(View.VISIBLE);

        resendCodeText.setVisibility(View.GONE);
        countDown.setVisibility(View.GONE);
    }

    private void hideResendCode(){
        resendCodeTextButton.setVisibility(View.GONE);

        resendCodeText.setVisibility(View.VISIBLE);
        countDown.setVisibility(View.VISIBLE);
    }

    private void resendCode(String phone){
        PhoneAuthOptions options = PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setActivity(VerifyAccountActivity.this)
                .setPhoneNumber(PHONE_CODE + phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(VerifyAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String newVerificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = newVerificationId;
                        Toast.makeText(VerifyAccountActivity.this, "Mã OTP đã được gửi!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void confirmCode(){
        if(inputCode1.getText().toString().trim().isEmpty()
                || inputCode2.getText().toString().trim().isEmpty()
                || inputCode3.getText().toString().trim().isEmpty()
                || inputCode4.getText().toString().trim().isEmpty()
                || inputCode5.getText().toString().trim().isEmpty()
                || inputCode6.getText().toString().trim().isEmpty())    {
            Toast.makeText(VerifyAccountActivity.this, "Mã code không đúng định dạng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String code =
                inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();
        if(verificationId != null) {
            //Show progress bar
            progressLayout.setVisibility(View.VISIBLE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    code
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Dismiss progress layout
                            progressLayout.setVisibility(View.GONE);

                            if(task.isSuccessful()) {

                                Toast.makeText(VerifyAccountActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(VerifyAccountActivity.this, SignInActivity.class));
                            } else {
                                Toast.makeText(VerifyAccountActivity.this, "Mã code không đúng. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showErrorText(String msg){
        errorText.setText(msg);
    }

    private void textChangedListenerEditTextOTP(){
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(inputCode1.getText().toString().length()== 0)     //size as per your requirement
                {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });

        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(inputCode2.getText().toString().length()== 0)     //size as per your requirement
                {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });

        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(inputCode3.getText().toString().length()== 0)     //size as per your requirement
                {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });

        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(inputCode4.getText().toString().length()== 0)     //size as per your requirement
                {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });

        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(inputCode5.getText().toString().length()== 0)     //size as per your requirement
                {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });
    }

    private void initComponent() {
        confirm_code = (MaterialButton) findViewById(R.id.confirm_code);
        close_image = (ImageView) findViewById(R.id.close_image);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        progressLayout = findViewById(R.id.progress_layout);

        resendCodeText = findViewById(R.id.textview_resendCode);
        resendCodeTextButton = findViewById(R.id.textview_resendCode_button);
        countDown = findViewById(R.id.textview_countdown);
        errorText = findViewById(R.id.errorText);
    }
}