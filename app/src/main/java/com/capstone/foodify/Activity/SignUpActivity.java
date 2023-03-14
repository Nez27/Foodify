package com.capstone.foodify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.DistrictWard.DistrictWardResponse;
import com.capstone.foodify.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String PHONE_CODE = "+84";
    TextInputLayout textInput_email, textInput_password, textInput_phone,
            textInput_address, textInput_repeatPassword, textInput_fullName, textInput_birthDay;
    final Calendar myCalendar= Calendar.getInstance();
    EditText edt_birthday, edt_phone;
    TextView signIn_textView, errorText;
    Spinner wardSpinner, districtSpinner;
    ImageView back_image;
    MaterialButton signUpButton;
    ConstraintLayout progressLayout;

    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponent();

        setFontUI();
        chooseDateOfBirth();

        if(districtList.size() == 0){
            getListDistrict();
        } else {
            setAdapter(districtList, "---Quận", districtSpinner);
        }
        getListWard(0);
        signIn_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getListWard(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Set event for sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();
                signUp();
            }
        });
    }

    private void signUp(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(PHONE_CODE + edt_phone.getText().toString())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        loadCompleted();

                        Intent intent = new Intent(SignUpActivity.this, VerifyAccountActivity.class);
                        intent.putExtra("phone", edt_phone.getText().toString());
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("token", forceResendingToken);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        loadCompleted();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ...
                        loadCompleted();
                        showError(e.toString());
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END auth_test_phone_verify]
    }

    private void showError(String msg){
        errorText.setText(msg);
    }

    private void loading(){
        progressLayout.setVisibility(View.VISIBLE);
        signUpButton.setEnabled(false);
    }

    private void loadCompleted(){
        progressLayout.setVisibility(View.GONE);
        signUpButton.setEnabled(true);
    }
    private void initComponent() {
        textInput_email= (TextInputLayout) findViewById(R.id.textInput_email);
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        textInput_phone = (TextInputLayout) findViewById(R.id.textInput_phone);
        textInput_address = (TextInputLayout) findViewById(R.id.textInput_address);
        textInput_repeatPassword = (TextInputLayout) findViewById(R.id.textInput_confirmPassword);
        textInput_fullName = (TextInputLayout) findViewById(R.id.textInput_fullName);
        textInput_birthDay = (TextInputLayout) findViewById(R.id.textInput_birthDay);

        signUpButton = findViewById(R.id.sign_up_button);

        edt_birthday = (EditText) findViewById(R.id.edt_birthDay);
        edt_phone = findViewById(R.id.edt_phone);

        districtSpinner = (Spinner) findViewById(R.id.district);
        wardSpinner = (Spinner) findViewById(R.id.ward);

        signIn_textView = (TextView) findViewById(R.id.signIn_textView);
        errorText = findViewById(R.id.errorText);

        back_image = (ImageView) findViewById(R.id.back_image);

        progressLayout = findViewById(R.id.progress_layout);

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

                setAdapter(districtList, "---Quận", districtSpinner);
            }

            @Override
            public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListWard(int districtCode) {

        if(wardList.size() > 1)
            wardList.clear();

        if(wardList.size() == 0)
            wardList.add("---Phường");

        wardSpinner.setEnabled(false);

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
                        wardSpinner.setEnabled(false);
                    } else {
                        wardSpinner.setEnabled(true);
                    }

                    setAdapter(wardList, "---Phường", wardSpinner);
                }

                @Override
                public void onFailure(Call<List<DistrictWardResponse>> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setAdapter(wardList, "---Phường", wardSpinner);
        }
    }

    private void setAdapter(ArrayList<String> data, String defaultItem, Spinner dataSpinner){
        MySpinnerAdapter adapterWard = new MySpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, data);
        dataSpinner.setAdapter(adapterWard); // this will set list of values to spinner
        dataSpinner.setSelection(data.indexOf(defaultItem));//set selected value in spinner
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "font/bebas.ttf");

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

    private void chooseDateOfBirth() {
        //Calendar date of birth
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        edt_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignUpActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        edt_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setFontUI() {
        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
        textInput_phone.setTypeface(Common.setFontBebas(getAssets()));
        textInput_address.setTypeface(Common.setFontBebas(getAssets()));
        textInput_repeatPassword.setTypeface(Common.setFontBebas(getAssets()));
        textInput_fullName.setTypeface(Common.setFontBebas(getAssets()));
        textInput_birthDay.setTypeface(Common.setFontBebas(getAssets()));

        edt_birthday.setTypeface(Common.setFontBebas(getAssets()));
    }
}