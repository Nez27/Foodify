package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.Common;
import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    ImageView close_image;
    TextInputLayout textInput_password, textInput_confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initComponent();

        setFontUI();

        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setFontUI() {
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
        textInput_confirmPassword.setTypeface(Common.setFontBebas(getAssets()));
    }

    private void initComponent() {
        close_image = (ImageView) findViewById(R.id.close_image);
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        textInput_confirmPassword = (TextInputLayout) findViewById(R.id.textInput_confirmPassword);
    }
}