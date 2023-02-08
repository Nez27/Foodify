package com.capstone.foodify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout textInput_email, textInput_password, textInput_phone,
            textInput_address, textInput_repeatPassword, textInput_firstName, textInput_lastName, textInput_birthDay;
    final Calendar myCalendar= Calendar.getInstance();
    EditText editText, edt_birthday;

    Spinner wardSpinner, districtSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setFontUI();

        //Calendar date of birth
        editText=(EditText) findViewById(R.id.edt_birthDay);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignUpActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        wardSpinner = (Spinner) findViewById(R.id.ward);
        districtSpinner = (Spinner) findViewById(R.id.district);

        ArrayList<String> ward = new ArrayList<String>();
        ArrayList<String> district = new ArrayList<String>();

        ward.add("---Phường");
        ward.add("Hoà Hiệp Bắc");
        ward.add("Hòa Khánh Bắc");
        ward.add("Hòa Minh");
        ward.add("Hòa Hiệp Nam");
        ward.add("Hòa Khánh Nam");

        district.add("---Quận");
        district.add("Liên Chiểu");
        district.add("Ngũ Hành Sơn");

        MySpinnerAdapter adapterWard = new MySpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, ward);
        MySpinnerAdapter adapterDistrict = new MySpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, district);
        wardSpinner.setAdapter(adapterWard); // this will set list of values to spinner
        districtSpinner.setAdapter(adapterDistrict); // this will set list of values to spinner

        wardSpinner.setSelection(ward.indexOf("---Phường"));//set selected value in spinner
        districtSpinner.setSelection(district.indexOf("---Quận"));
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

    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setFontUI() {
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

        //Find view ID
        textInput_email= (TextInputLayout) findViewById(R.id.textInput_email);
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        textInput_phone = (TextInputLayout) findViewById(R.id.textInput_phone);
        textInput_address = (TextInputLayout) findViewById(R.id.textInput_address);
        textInput_repeatPassword = (TextInputLayout) findViewById(R.id.textInput_confirmPassword);
        textInput_firstName = (TextInputLayout) findViewById(R.id.textInput_firstName);
        textInput_lastName = (TextInputLayout) findViewById(R.id.textInput_lastName);
        textInput_birthDay = (TextInputLayout) findViewById(R.id.textInput_birthDay);

        edt_birthday = (EditText) findViewById(R.id.edt_birthDay);

        //Set Type face
        textInput_email.setTypeface(bebas);
        textInput_password.setTypeface(bebas);
        textInput_phone.setTypeface(bebas);
        textInput_address.setTypeface(bebas);
        textInput_repeatPassword.setTypeface(bebas);
        textInput_firstName.setTypeface(bebas);
        textInput_lastName.setTypeface(bebas);
        textInput_birthDay.setTypeface(bebas);

        edt_birthday.setTypeface(bebas);
    }
}