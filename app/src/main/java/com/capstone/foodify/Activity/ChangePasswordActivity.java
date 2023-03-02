package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.R;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputLayout textInput_old_password, textInput_password, textInput_password_confirm;

    ImageView back_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initComponent();
        setFontUI();

        //Handle event when back image on click
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initComponent() {
        textInput_old_password = findViewById(R.id.textInput_old_password);
        textInput_password = findViewById(R.id.textInput_password);
        textInput_password_confirm = findViewById(R.id.textInput_password_confirm);

        back_image = findViewById(R.id.back_image);
    }

    private void setFontUI() {
        Typeface bebas= Typeface.createFromAsset(getAssets(), "font/bebas.ttf");

        textInput_password.setTypeface(bebas);
        textInput_old_password.setTypeface(bebas);
        textInput_password_confirm.setTypeface(bebas);
    }
}