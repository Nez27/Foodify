package com.capstone.foodify.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.foodify.R;
import com.google.android.material.button.MaterialButton;

public class SendEmailActivity extends AppCompatActivity {

    ImageView close_image;
    MaterialButton confirm_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        initComponent();

        confirm_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendEmailActivity.this, ResetPasswordActivity.class));
            }
        });

        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initComponent() {
        confirm_code = (MaterialButton) findViewById(R.id.confirm_code);
        close_image = (ImageView) findViewById(R.id.close_image);
    }
}