package com.capstone.foodify.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;

public class SignInActivity extends AppCompatActivity {

    TextView signUp_textView, forgotPassword_textView;

    TextInputLayout textInput_account, textInput_password;

    ImageView back_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initComponent();

        setFontUI();
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        signUp_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        forgotPassword_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
    private void initComponent() {
        textInput_account = (TextInputLayout) findViewById(R.id.textInput_account);
        textInput_password = (TextInputLayout) findViewById(R.id.textInput_password);
        signUp_textView = (TextView) findViewById(R.id.sign_up_textView);
        forgotPassword_textView = (TextView) findViewById(R.id.forgotPassword_textView);
        back_image = (ImageView) findViewById(R.id.back_image);
    }
    private void setFontUI() {
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

        textInput_account.setTypeface(bebas);
        textInput_password.setTypeface(bebas);
    }

    // this event will enable the back
    // function to the button on press
}