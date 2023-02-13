package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.capstone.foodify.API.DistrictWardApi;
import com.capstone.foodify.Model.DistrictWard.Datas;
import com.capstone.foodify.Model.DistrictWard.DisctrictWardResponse;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final int PROVINCE_CODE = 48;
    private static final int LIMIT = -1;

    TextInputLayout textInput_email, textInput_password, textInput_phone,
            textInput_address, textInput_repeatPassword, textInput_firstName, textInput_lastName, textInput_birthDay;
    final Calendar myCalendar= Calendar.getInstance();
    EditText edt_birthday;
    TextView signIn_textView;
    Spinner wardSpinner, districtSpinner;
    ImageView back_image;

    private static final ArrayList<String> wardList = new ArrayList<>();
    private static final ArrayList<String> districtList = new ArrayList<>();
    private static List<Datas> districtListData = new ArrayList<>();
    private static List<Datas> wardListData = new ArrayList<>();

    String selectedDistrict = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponent();

        setFontUI();
        chooseDateOfBirth();

        if(districtListData.size() == 0){
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
                int districtCode = 0;

                selectedDistrict = districtSpinner.getSelectedItem().toString();

                //Get id district from selected item district;
                for(Datas dataTemp: districtListData){
                    if(selectedDistrict.equals(dataTemp.getName_with_type())){
                        districtCode = Integer.parseInt(dataTemp.getCode());
                        break;
                    }
                }
                getListWard(districtCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void initComponent() {
        textInput_email= (TextInputLayout) findViewById(R.id.textInput_email);
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        textInput_phone = (TextInputLayout) findViewById(R.id.textInput_phone);
        textInput_address = (TextInputLayout) findViewById(R.id.textInput_address);
        textInput_repeatPassword = (TextInputLayout) findViewById(R.id.textInput_confirmPassword);
        textInput_firstName = (TextInputLayout) findViewById(R.id.textInput_firstName);
        textInput_lastName = (TextInputLayout) findViewById(R.id.textInput_lastName);
        textInput_birthDay = (TextInputLayout) findViewById(R.id.textInput_birthDay);

        edt_birthday = (EditText) findViewById(R.id.edt_birthDay);

        districtSpinner = (Spinner) findViewById(R.id.district);
        wardSpinner = (Spinner) findViewById(R.id.ward);

        signIn_textView = (TextView) findViewById(R.id.signIn_textView);

        back_image = (ImageView) findViewById(R.id.back_image);

    }

    private void getListDistrict() {

        districtList.add("---Quận");

        DistrictWardApi.apiService.districtResponse(PROVINCE_CODE, LIMIT).enqueue(new Callback<DisctrictWardResponse>() {
            @Override
            public void onResponse(Call<DisctrictWardResponse> call, Response<DisctrictWardResponse> response) {
                DisctrictWardResponse districtResponse = response.body();
                if(districtResponse != null && districtResponse.getExitcode() == 1){

                    districtListData = districtResponse.getData().getData();
                    for(Datas tempData: districtListData){
                        districtList.add(tempData.getName_with_type());
                    }
                };

                setAdapter(districtList, "---Quận", districtSpinner);
            }

            @Override
            public void onFailure(Call<DisctrictWardResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListWard(int districtCode) {

        if(wardList.size() > 1)
            wardList.clear();

        if(wardList.size() == 0)
            wardList.add("---Phường");

        if(districtCode != 0){
            DistrictWardApi.apiService.wardResponse(districtCode, LIMIT).enqueue(new Callback<DisctrictWardResponse>() {
                @Override
                public void onResponse(Call<DisctrictWardResponse> call, Response<DisctrictWardResponse> response) {

                    DisctrictWardResponse wardResponse = response.body();
                    if(wardResponse != null && wardResponse.getExitcode() == 1){

                        wardListData = wardResponse.getData().getData();
                        for(Datas tempData: wardListData){
                            wardList.add(tempData.getName_with_type());
                        }
                    };

                    setAdapter(wardList, "---Phường", wardSpinner);
                }

                @Override
                public void onFailure(Call<DisctrictWardResponse> call, Throwable t) {
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
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

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