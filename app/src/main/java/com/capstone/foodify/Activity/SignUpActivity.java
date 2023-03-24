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
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Model.DistrictWardResponse;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout textInput_password, textInput_phone, textInput_email, textInput_id_card,
            textInput_address, textInput_confirmPassword, textInput_fullName, textInput_birthDay;
    final Calendar myCalendar= Calendar.getInstance();
    private EditText edt_birthday, edt_phone, edt_passWord, edt_confirmPassword, edt_fullName, edt_address, edt_email, edt_id_card;
    private TextView signIn_textView, errorText, errorTextDistrictWard;
    private Spinner wardSpinner, districtSpinner;
    private ImageView back_image;
    private MaterialButton signUpButton;
    private ConstraintLayout progressLayout;
    private String email, phone, name, dateOfBirth, address, district, ward, password = null, identifiedCode;

    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();

    private void initComponent() {
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        textInput_phone = (TextInputLayout) findViewById(R.id.textInput_phone);
        textInput_address = (TextInputLayout) findViewById(R.id.textInput_address);
        textInput_confirmPassword = (TextInputLayout) findViewById(R.id.textInput_confirmPassword);
        textInput_fullName = (TextInputLayout) findViewById(R.id.textInput_fullName);
        textInput_birthDay = (TextInputLayout) findViewById(R.id.textInput_birthDay);
        textInput_email = findViewById(R.id.textInput_email);
        textInput_id_card = findViewById(R.id.textInput_id_card);

        signUpButton = findViewById(R.id.sign_up_button);

        edt_birthday = (EditText) findViewById(R.id.edt_birthDay);
        edt_phone = findViewById(R.id.edt_phone);
        edt_confirmPassword = findViewById(R.id.edt_confirmPassword);
        edt_passWord = findViewById(R.id.edt_password);
        edt_fullName = findViewById(R.id.edt_fullName);
        edt_address = findViewById(R.id.edt_address);
        edt_email = findViewById(R.id.edt_email);
        edt_id_card = findViewById(R.id.edt_id_card);

        districtSpinner = (Spinner) findViewById(R.id.district);
        wardSpinner = (Spinner) findViewById(R.id.ward);

        signIn_textView = (TextView) findViewById(R.id.signIn_textView);
        errorText = findViewById(R.id.errorText);
        errorTextDistrictWard = findViewById(R.id.errorTextDistrictWard);

        back_image = (ImageView) findViewById(R.id.back_image);

        progressLayout = findViewById(R.id.progress_layout);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponent();

        setFontUI();
        chooseDateOfBirth();

        //Get list district
        if(districtList.size() == 0){
            getListDistrict();
        } else {
            setAdapter(districtList, "---Quận", districtSpinner);
        }

        //Get list ward
        getListWard(0);

        //Add event when click on sign in button
        signIn_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        //Add event when click on back image
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Add event for district spinner
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
                if(validateData()){
                    loading();
                    checkEmailOrPhoneExist();
                }
            }
        });
    }

    private void checkEmailOrPhoneExist(){
        //Create user object
        User user = new User();
        user.setAddress(new Address(address, ward, district));
        user.setDateOfBirth(dateOfBirth);
        user.setEmail(email);
        user.setFullName(name);
        user.setIdentifiedCode(identifiedCode);
        user.setImageUrl("");
        user.setLocked(false);
        user.setPhoneNumber(phone);
        user.setRoleName("ROLE_USER");

        FoodApi.apiService.checkEmailOrPhoneExist(user).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                CustomResponse dataTemp = response.body();

                boolean isExist = false;

                if(dataTemp != null)
                    isExist = dataTemp.isTrue();

                if(!isExist){
                    //If user not exist on database, the app will create account
                    signUp(user);
                } else {
                    loadCompleted();
                    Toast.makeText(SignUpActivity.this, "Email or Phone exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateData(){
        boolean dataHasValidate = true;

        getDataFromForm();

        //Check email valid
        Pattern patternEmail = Pattern.compile(Common.VALID_EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcherEmail = patternEmail.matcher(email);
        if(!matcherEmail.matches()){
            textInput_email.setError("Địa chỉ email không đúng định dạng. Xin vui lòng kiểm tra lại!");
            dataHasValidate = false;
        } else {
            textInput_email.setErrorEnabled(false);
        }

        //Check phone number
        if(phone.isEmpty()){
            textInput_phone.setError("Số điện thoại không được bỏ trống!");
            dataHasValidate = false;
        } else {


            Pattern patternPhone = Pattern.compile(Common.PHONE_PATTERN);
            Matcher matcherPhone = patternPhone.matcher(phone);
            if(!matcherPhone.find()){
                textInput_phone.setError("Số điện thoại không đúng định dạng. Vui lòng kiểm tra lại!");
                dataHasValidate = false;
            } else {
                textInput_phone.setErrorEnabled(false);
            }
        }


        //Check full name
        if(name.length() < 8){
            textInput_fullName.setError("Tên của bạn cần phải có 8 ký tự trở lên!");
            dataHasValidate = false;
        } else {
            textInput_fullName.setErrorEnabled(false);
        }

        //Check identified Code
        if(identifiedCode.length() < 9 && identifiedCode.length() > 10){
            textInput_id_card.setError("CCCD / CMND không hợp lệ!");
            dataHasValidate = false;
        } else {
            textInput_id_card.setErrorEnabled(false);
        }

        //Check birthday
        if(dateOfBirth.isEmpty()){
            textInput_birthDay.setError("Ngày tháng năm sinh của bạn còn trống!");
            dataHasValidate = false;

        } else {
            //Calculate user age
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Common.FORMAT_DATE);
            LocalDate birthday = LocalDate.parse(edt_birthday.getText().toString(), formatter);
            if(calculateAge(birthday, LocalDate.now()) < 16){
                textInput_birthDay.setError("Xin lỗi, bạn chưa đủ tuổi đề dùng app này!");
                dataHasValidate = false;
            } else {
                textInput_birthDay.setErrorEnabled(false);
            }
        }

        //Check address
        if(address.length() < 8) {
            textInput_address.setError("Trường địa chỉ của bạn tối thiếu từ 8 ký tự trở lên");
            dataHasValidate = false;
        } else {
            textInput_address.setErrorEnabled(false);
        }

        //Check district and ward
        if(district.equals("---Quận") || ward.equals("---Phường")){
            errorTextDistrictWard.setVisibility(View.VISIBLE);

            dataHasValidate = false;
        } else {
            errorTextDistrictWard.setVisibility(View.GONE);
        }



        //Check password
        Pattern patternPassword = Pattern.compile(Common.PASSWORD_PATTERN);
        Matcher matcherPassword = patternPassword.matcher(edt_passWord.getText().toString());

        if(!matcherPassword.matches() || edt_passWord.getText().toString().length() < 8) {
            textInput_password.setError("Mật khẩu của bạn cần tối thiểu có 8 ký tự, 1 ký tự viết hoa, 1 số và 1 ký tự đặc biệt!");
            dataHasValidate = false;
        } else {
            textInput_password.setErrorEnabled(false);
        }

        if(!edt_passWord.getText().toString().equals(edt_confirmPassword.getText().toString())){
            textInput_confirmPassword.setError("Mật khẩu không giống nhau. Vui lòng kiểm tra lại!");
            dataHasValidate = false;
        } else {
            textInput_confirmPassword.setErrorEnabled(false);
        }

        return dataHasValidate;
    }


    private void getDataFromForm(){
        email = edt_email.getText().toString();
        phone = edt_phone.getText().toString();
        name = edt_fullName.getText().toString();
        dateOfBirth = edt_birthday.getText().toString();
        address = edt_address.getText().toString();
        district = districtSpinner.getSelectedItem().toString();
        ward = wardSpinner.getSelectedItem().toString();
        password = edt_confirmPassword.getText().toString();
        identifiedCode = edt_id_card.getText().toString();
    }


    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    private void signUp(User user){
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
                        loadCompleted();

                        Intent intent = new Intent(SignUpActivity.this, VerifyAccountActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("phone", edt_phone.getText().toString());
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("token", forceResendingToken);
                        intent.putExtra("password", password);
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
    }

    private void loadCompleted() {
        progressLayout.setVisibility(View.GONE);
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
        SimpleDateFormat dateFormat=new SimpleDateFormat(Common.FORMAT_DATE, Locale.US);
        edt_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setFontUI() {
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
        textInput_phone.setTypeface(Common.setFontBebas(getAssets()));
        textInput_address.setTypeface(Common.setFontBebas(getAssets()));
        textInput_confirmPassword.setTypeface(Common.setFontBebas(getAssets()));
        textInput_fullName.setTypeface(Common.setFontBebas(getAssets()));
        textInput_birthDay.setTypeface(Common.setFontBebas(getAssets()));
        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
        textInput_id_card.setTypeface(Common.setFontBebas(getAssets()));
    }
}