package com.capstone.foodify;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

        textInput_password.setTypeface(bebas);
        textInput_confirmPassword.setTypeface(bebas);
    }

    private void initComponent() {
        close_image = (ImageView) findViewById(R.id.close_image);
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        textInput_confirmPassword = (TextInputLayout) findViewById(R.id.textInput_confirmPassword);
    }
}