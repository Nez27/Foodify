package com.capstone.foodify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout textInput_account;

    ImageView back_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initComponent();

        //Back button
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
            }
        });

        setFontUI();
    }

    private void setFontUI() {
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

        textInput_account = (TextInputLayout) findViewById(R.id.textInput_account);

        textInput_account.setTypeface(bebas);
    }

    private void initComponent() {
        back_image = (ImageView) findViewById(R.id.back_image);
    }

}