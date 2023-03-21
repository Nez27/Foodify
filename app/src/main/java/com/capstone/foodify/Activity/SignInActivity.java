package com.capstone.foodify.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Common;
import com.capstone.foodify.Fragment.HomeFragment;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView signUp_textView, forgotPassword_textView;
    TextInputLayout textInput_email, textInput_password;
    TextInputEditText edt_email, edt_password;
    MaterialButton signInButton;
    ImageView back_image;
    ConstraintLayout progressLayout;
    String email, password = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init firebase app
        FirebaseApp.initializeApp(SignInActivity.this);
        setContentView(R.layout.activity_sign_in);

        //Init Paper
        Paper.init(this);

        if(Common.CURRENT_USER != null)
            startActivity(new Intent(this, MainActivity.class));

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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


        //Set event when user click sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();

                if(validData()){
                    signIn();
                }
            }
        });
    }

    private void initData(){
        email = edt_email.getText().toString();
        password = edt_password.getText().toString();
    }

    private boolean validData(){
        boolean dataHasValidate = true;

        //Check phone number
        if(email.isEmpty()){
            textInput_email.setError("Email không được bỏ trống!");
            dataHasValidate = false;
        } else {

            //Check email valid
            Pattern patternEmail = Pattern.compile(Common.VALID_EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcherEmail = patternEmail.matcher(email);
            if(!matcherEmail.matches()){
                textInput_email.setError("Địa chỉ email không đúng định dạng. Xin vui lòng kiểm tra lại!");
                dataHasValidate = false;
            } else {
                textInput_email.setErrorEnabled(false);
            }
        }

        //Check password
        Pattern patternPassword = Pattern.compile(Common.PASSWORD_PATTERN);
        Matcher matcherPassword = patternPassword.matcher(edt_password.getText().toString());

        if(!matcherPassword.matches() || edt_password.getText().toString().length() < 8) {
            textInput_password.setError("Mật khẩu của bạn cần tối thiểu có 8 ký tự, 1 ký tự viết hoa, 1 số và 1 ký tự đặc biệt!");
            dataHasValidate = false;
        } else {
            textInput_password.setErrorEnabled(false);
        }

        return dataHasValidate;
    }

    private void signIn(){
        //Show progress bar
        progressLayout.setVisibility(View.VISIBLE);

        mAuth.setLanguageCode("vi");

        mAuth.signInWithEmailAndPassword(edt_email.getText().toString(), edt_password.getText().toString())
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Save user
                            Paper.book().write("user", user);

                            user.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if(task.isSuccessful()){
                                                Common.TOKEN = task.getResult().getToken();



                                                //Get user from database
                                                FoodApiToken.apiService.getUserFromEmail(user.getEmail()).enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        User userData = response.body();
                                                        if(userData != null){
                                                            Common.CURRENT_USER = userData;
                                                        }

                                                        //Dismiss progress bar
                                                        progressLayout.setVisibility(View.GONE);

                                                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        System.out.println("ERROR: " + t);
                                                        Common.showErrorServerNotification(SignInActivity.this, "Không thể đăng nhập tài khoản! Vui lòng thử lại sau!");
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(SignInActivity.this, "Error when taking token!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Thông tin đăng nhập không đúng! Vui lòng kiểm tra và thử lại!",
                                    Toast.LENGTH_SHORT).show();

                            //Dismiss progress bar
                            progressLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initComponent() {
        textInput_email = findViewById(R.id.textInput_email);
        textInput_password = findViewById(R.id.textInput_password);
        signUp_textView = findViewById(R.id.sign_up_textView);
        forgotPassword_textView = findViewById(R.id.forgotPassword_textView);
        back_image = findViewById(R.id.back_image);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);

        signInButton = findViewById(R.id.sign_in_button);

        progressLayout = findViewById(R.id.progress_layout);

    }
    private void setFontUI() {
        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
    }

    // this event will enable the back
    // function to the button on press
}