package com.capstone.foodify.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.capstone.foodify.Service.SmsBroadcastReceiver;
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
    private static final String TAG = "VerifyAccountActivity";
    private static final String PHONE_CODE = "+84";
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private ImageView close_image;
    private  MaterialButton confirm_code;
    private String verificationId, phone, password = null;
    private ConstraintLayout progressLayout;
    private TextView resendCodeText, resendCodeTextButton, countDown, errorText;
    private User user;
    private FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_verify_account);

        initComponent();

        //Check internet connection
        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }

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
                                                FirebaseAuth.getInstance().signOut();
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
                                            Log.e(TAG, t.toString());
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
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    inputCode2.requestFocus(View.FOCUS_DOWN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    inputCode1.requestFocus(View.FOCUS_DOWN);
                } else {
                    if (s.length() == 1) {
                        inputCode3.requestFocus(View.FOCUS_DOWN);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    inputCode2.requestFocus(View.FOCUS_DOWN);
                } else {
                    if (s.length() == 1) {
                        inputCode4.requestFocus(View.FOCUS_DOWN);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    inputCode3.requestFocus(View.FOCUS_DOWN);
                } else {
                    if (s.length() == 1) {
                        inputCode5.requestFocus(View.FOCUS_DOWN);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    inputCode4.requestFocus(View.FOCUS_DOWN);
                } else {
                    if (s.length() == 1) {
                        inputCode6.requestFocus(View.FOCUS_DOWN);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    inputCode5.requestFocus(View.FOCUS_DOWN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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