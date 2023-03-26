package com.capstone.foodify.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.capstone.foodify.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAccountActivity extends AppCompatActivity {
    private static final String PHONE_CODE = "+84";
    ActivityResultLauncher<Intent> activityResultLauncher;
    SmsBroadcastReceiver smsBroadcastReceiver;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    ImageView close_image;
    MaterialButton confirm_code;
    String verificationId, phone, password = null;
    ConstraintLayout progressLayout;
    TextView resendCodeText, resendCodeTextButton, countDown, errorText;
    User user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        initComponent();

        //Auto fill OTP Code
        requestSMSPermission();
        startSmartUserContent();
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();

                            if(data != null){
                                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                                getOtpFromMessage(message);
                            }
                        }
                    }
                });

        //Get code
        if(getIntent() != null){
            verificationId = getIntent().getStringExtra("verificationId");
            phone = getIntent().getStringExtra("phone");
            user = (User) getIntent().getSerializableExtra("user");
            password = getIntent().getStringExtra("password");
        }

        //Init firebase auth
        mAuth = FirebaseAuth.getInstance();


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
                finish();
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

    private void getOtpFromMessage(String message) {
        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            Double otp = Double.parseDouble(matcher.group(0));

            inputCode6.setText(String.valueOf(otp % 10));
            otp = otp / 10;
            inputCode5.setText(String.valueOf(otp % 10));
            otp = otp / 10;
            inputCode4.setText(String.valueOf(otp % 10));
            otp = otp / 10;
            inputCode3.setText(String.valueOf(otp % 10));
            otp = otp / 10;
            inputCode2.setText(String.valueOf(otp % 10));
            otp = otp / 10;
            inputCode1.setText(String.valueOf(otp % 10));
        }
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
        if(verificationId != null && user != null && password != null) {
            //Show progress bar
            progressLayout.setVisibility(View.VISIBLE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    code
            );

            if(phoneAuthCredential.getSmsCode().equals(code)){
                //Create user account
                mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //Add information user to server
                                    FoodApi.apiService.register(user).enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {

                                            if(response.code() == 200){
                                                progressLayout.setVisibility(View.GONE);
                                                //Create user success;
                                                Toast.makeText(VerifyAccountActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(VerifyAccountActivity.this, SignInActivity.class));
                                                finish();
                                            } else {
                                                progressLayout.setVisibility(View.GONE);
                                                Toast.makeText(VerifyAccountActivity.this, "Lỗi hệ thống! Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            progressLayout.setVisibility(View.GONE);
                                            showErrorText(t.toString());
                                            Common.showErrorServerNotification(VerifyAccountActivity.this, "Không thể tạo tài khoản! Vui lòng thử lại sau!");
                                        }
                                    });
                                } else {
                                    progressLayout.setVisibility(View.GONE);
                                    Common.showErrorServerNotification(VerifyAccountActivity.this, "Đã có lỗi kết nối! Vui lòng thử lại sau!");
                                }
                            }
                        });
            }


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

    private void requestSMSPermission()
    {
        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission);
        if(grant != PackageManager.PERMISSION_GRANTED){
            String[] permission_list = new String[1];
            permission_list[0] = permission;

            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void startSmartUserContent(){

        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    private void registerBroadcastReceiver(){
        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                activityResultLauncher.launch(intent);
            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
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

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
}