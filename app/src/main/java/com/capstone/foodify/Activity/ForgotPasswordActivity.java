package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout textInput_account;
    MaterialButton send_code_button;

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
                onBackPressed();
            }
        });

        send_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, SendEmailActivity.class));
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
        send_code_button = (MaterialButton) findViewById(R.id.send_code_button);
    }

}