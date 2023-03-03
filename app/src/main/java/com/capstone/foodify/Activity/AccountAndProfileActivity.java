package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccountAndProfileActivity extends AppCompatActivity {

    TextInputLayout textInput_email, textInput_phone, textInput_fullName, textInput_birthDay;

    EditText edt_birthday;
    final Calendar myCalendar= Calendar.getInstance();
    LinearLayout change_password;

    ImageView back_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_profile);

        initComponent();
        setFontUI();
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


    }

    private void initComponent() {
        textInput_email= (TextInputLayout) findViewById(R.id.textInput_email);
        textInput_phone = (TextInputLayout) findViewById(R.id.textInput_phone);
        textInput_fullName = (TextInputLayout) findViewById(R.id.textInput_fullName);
        textInput_birthDay = (TextInputLayout) findViewById(R.id.textInput_birthDay);
        change_password = (LinearLayout) findViewById(R.id.change_password);

        back_image = (ImageView) findViewById(R.id.back_image);

        edt_birthday = (EditText) findViewById(R.id.edt_birthDay);
    }

    private void setFontUI() {
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

        //Set Type face
        textInput_email.setTypeface(bebas);
        textInput_phone.setTypeface(bebas);
        textInput_fullName.setTypeface(bebas);
        textInput_birthDay.setTypeface(bebas);

        edt_birthday.setTypeface(bebas);
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
                new DatePickerDialog(AccountAndProfileActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        edt_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }
}