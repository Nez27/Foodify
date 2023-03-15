package com.capstone.foodify.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
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
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView signUp_textView, forgotPassword_textView;
    TextInputLayout textInput_account, textInput_password;
    TextInputEditText email, password;
    MaterialButton signInButton;
    ImageView back_image;
    PopupDialog popupDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init firebase app
        FirebaseApp.initializeApp(SignInActivity.this);
        setContentView(R.layout.activity_sign_in);

        //Init Paper
        Paper.init(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Init Popup Dialog
        popupDialog = PopupDialog.getInstance(SignInActivity.this);

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

                //Show progress bar
                popupDialog.setStyle(Styles.PROGRESS).setProgressDialogTint(getResources().getColor(R.color.primaryColor, null))
                        .setCancelable(false).showDialog();

                mAuth.setLanguageCode("vi");

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
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

                                                        //Dismiss progress bar dialog
                                                        popupDialog.dismissDialog();

                                                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                                    } else {
                                                        Toast.makeText(SignInActivity.this, "Error when taking token!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignInActivity.this, "Thông tin đăng nhập không đúng! Vui lòng kiểm tra và thử lại!",
                                            Toast.LENGTH_SHORT).show();

                                    popupDialog.dismissDialog();
                                }
                            }
                        });
            }
        });
    }

    private void initComponent() {
        textInput_account = findViewById(R.id.textInput_account);
        textInput_password = findViewById(R.id.textInput_password);
        signUp_textView = findViewById(R.id.sign_up_textView);
        forgotPassword_textView = findViewById(R.id.forgotPassword_textView);
        back_image = findViewById(R.id.back_image);

        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);

        signInButton = findViewById(R.id.sign_in_button);

    }
    private void setFontUI() {
        textInput_account.setTypeface(Common.setFontBebas(getAssets()));
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
    }

    // this event will enable the back
    // function to the button on press
}