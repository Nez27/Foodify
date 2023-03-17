package com.capstone.foodify.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAndProfileActivity extends AppCompatActivity {

    TextInputLayout textInput_email, textInput_phone, textInput_fullName, textInput_birthDay;

    EditText edt_birthday, edt_email, edt_phone, edt_fullName;
    final Calendar myCalendar= Calendar.getInstance();
    LinearLayout change_password;
    Button update_button;
    ImageView back_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_profile);

        initComponent();
        setFontUI();

        //Init data
        initData();

        chooseDateOfBirth();

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountAndProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

    }

    private void initData() {
        edt_email.setText(Common.CURRENT_USER.getEmail());
        edt_phone.setText(Common.CURRENT_USER.getPhoneNumber());
        edt_fullName.setText(Common.CURRENT_USER.getFullName());
        edt_birthday.setText(Common.CURRENT_USER.getDateOfBirth());
    }

    private void initComponent() {
        //Init Component

        textInput_email= findViewById(R.id.textInput_email);
        textInput_phone = findViewById(R.id.textInput_phone);
        textInput_fullName = findViewById(R.id.textInput_fullName);
        textInput_birthDay = findViewById(R.id.textInput_birthDay);
        change_password = findViewById(R.id.change_password);

        update_button = findViewById(R.id.updated_button);

        back_image = findViewById(R.id.back_image);

        edt_birthday = findViewById(R.id.edt_birthDay);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_fullName = findViewById(R.id.edt_fullName);
    }

    private void setFontUI() {
        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
        textInput_phone.setTypeface(Common.setFontBebas(getAssets()));
        textInput_fullName.setTypeface(Common.setFontBebas(getAssets()));
        textInput_birthDay.setTypeface(Common.setFontBebas(getAssets()));

        edt_birthday.setTypeface(Common.setFontBebas(getAssets()));
    }

    private void chooseDateOfBirth() {
        //Get date, month, year from user
        String [] dateParts = edt_birthday.getText().toString().split("-");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        //Calendar date of birth
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        }, Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        edt_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void updateLabel(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(Common.FORMAT_DATE, Locale.US);
        edt_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void updateUser() {
        //Get data from form
        String fullName = edt_fullName.getText().toString();
        String dateOfBirth = edt_birthday.getText().toString();

        //Create object User
        Common.CURRENT_USER.setFullName(fullName);
        Common.CURRENT_USER.setDateOfBirth(dateOfBirth);

        FoodApiToken.apiService.updateUser(Common.CURRENT_USER.getId(), Common.CURRENT_USER).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(AccountAndProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Common.showErrorServerNotification(AccountAndProfileActivity.this);
            }
        });
    }
}