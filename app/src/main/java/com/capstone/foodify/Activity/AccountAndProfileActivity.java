package com.capstone.foodify.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAndProfileActivity extends AppCompatActivity {

    private static final String TAG = "AccountAndProfileActivity";
    private static final String FOLDER_DIRECTORY = "UserImages/";
    private TextInputLayout textInput_email, textInput_phone, textInput_fullName, textInput_birthDay;
    private TextView txt_countdown, txt_resend_code, txt_verify_email;
    private EditText edt_birthday, edt_email, edt_phone, edt_fullName;
    private final Calendar myCalendar= Calendar.getInstance();
    private LinearLayout change_password, countdown_layout;
    private Button update_button, update_image_button;
    private ImageView back_image;
    private RoundedImageView profile_avatar;
    private Uri imageUri;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ConstraintLayout progressLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private final CountDownTimer count = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            txt_countdown.setText(millisUntilFinished / 1000 + " giây");
        }

        @Override
        public void onFinish() {
            countdown_layout.setVisibility(View.GONE);

            txt_resend_code.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_profile);

        initComponent();
        setFontUI();

        if(getConnectionType() == 0){
            Common.showErrorInternetConnectionNotification(this);
        }

        //Init data
        initData();

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Show notification about email verify for user
        showNotificationEmailVerify();

        chooseDateOfBirth();

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountAndProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLayout.setVisibility(View.VISIBLE);
                if(imageUri != null)
                    uploadToFirebase(imageUri);
                else
                    updateUser(null);
            }
        });

        //Activity choose image
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            profile_avatar.setImageURI(imageUri);

                            Toast.makeText(AccountAndProfileActivity.this, "Hãy bấm nút cập nhật phía dưới để thay đổi có hiệu lực!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountAndProfileActivity.this, "Không ảnh nào được chọn!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Set event on button upload image avatar
        update_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        //Set event resend_code button
        txt_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailVerification();

                countdown_layout.setVisibility(View.VISIBLE);

                txt_resend_code.setVisibility(View.GONE);

                count.start();
            }
        });

    }
    private void uploadToFirebase(Uri uri){

        //Name of image user
        String imageName = Common.CURRENT_USER.getFullName().replace(" " , "_") + "_" + Common.CURRENT_USER.getDateOfBirth().replace("-", "_")
                + "_" + System.currentTimeMillis();

        final StorageReference imageReference = storageReference.child(FOLDER_DIRECTORY + imageName + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(!Common.CURRENT_USER.getImageUrl().isEmpty())
                            deleteOldImage();

                        updateUser(uri.toString());
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AccountAndProfileActivity.this, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteOldImage() {
        //Check avatar user if image default, don't delete it!
        if(!Common.CURRENT_USER.getImageUrl().equals(Common.DEFAULT_IMAGE_URL)){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Common.CURRENT_USER.getImageUrl());

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountAndProfileActivity.this, "Can't delete old image!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    private void initData() {
        edt_email.setText(Common.CURRENT_USER.getEmail());
        edt_phone.setText(Common.CURRENT_USER.getPhoneNumber());
        edt_fullName.setText(Common.CURRENT_USER.getFullName());
        edt_birthday.setText(Common.CURRENT_USER.getDateOfBirth());

        //Set image for user
        if(Common.CURRENT_USER.getImageUrl().isEmpty()){
            profile_avatar.setImageResource(R.drawable.profile_avatar);
        } else {
            Picasso.get().load(Common.CURRENT_USER.getImageUrl()).into(profile_avatar);
        }
    }

    private void sendEmailVerification(){
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AccountAndProfileActivity.this, "Email đã được gửi đi, vui lòng kiểm tra hộp thư của bạn!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showNotificationEmailVerify(){
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseUser = mAuth.getCurrentUser();

        //Reload firebase user data
        firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressLayout.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    if(firebaseUser != null){
                        
                        //If user don't verify email yet
                        if(!firebaseUser.isEmailVerified()){
                            showDialogCaution();
                            txt_verify_email.setVisibility(View.VISIBLE);
                            txt_verify_email.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //When user click on the text

                                    //Count down resend code
                                    countdown_layout.setVisibility(View.VISIBLE);
                                    count.start();

                                    sendEmailVerification();
                                    txt_verify_email.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            txt_verify_email.setVisibility(View.GONE);
                        }
                    }   
                } else {
                    Common.showErrorServerNotification(AccountAndProfileActivity.this, "Đã có lỗi từ hệ thống! Vui lòng thử lại sau!");
                }
            }
        });
    }

    private void showDialogCaution() {
        Common.notificationDialog(this, DialogStyle.FLAT, DialogType.WARNING, "Email chưa được xác thực!", "Vui lòng xác thực email để bảo vệ tài khoản của bạn!");
    }

    private void updateUser(String imageUrl) {
        //Get data from form
        String fullName = edt_fullName.getText().toString();
        String dateOfBirth = edt_birthday.getText().toString();

        //Create object User
        Common.CURRENT_USER.setFullName(fullName);
        Common.CURRENT_USER.setDateOfBirth(dateOfBirth);
        if(imageUrl != null)
            Common.CURRENT_USER.setImageUrl(imageUrl);

        FoodApiToken.apiService.updateUser(Common.CURRENT_USER.getId(), Common.CURRENT_USER).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(AccountAndProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.toString());
                progressLayout.setVisibility(View.GONE);
            }
        });
    }
    private void initComponent() {
        //Init Component
        textInput_email= findViewById(R.id.textInput_email);
        textInput_phone = findViewById(R.id.textInput_phone);
        textInput_fullName = findViewById(R.id.textInput_fullName);
        textInput_birthDay = findViewById(R.id.textInput_birthDay);
        change_password = findViewById(R.id.change_password);

        update_button = findViewById(R.id.updated_button);
        update_image_button = findViewById(R.id.change_image_button);

        back_image = findViewById(R.id.back_image);

        edt_birthday = findViewById(R.id.edt_birthDay);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_fullName = findViewById(R.id.edt_fullName);

        profile_avatar = findViewById(R.id.profile_avatar);

        progressLayout = findViewById(R.id.progress_layout);
        countdown_layout = findViewById(R.id.countdown_layout);

        txt_countdown = findViewById(R.id.textview_countdown);
        txt_resend_code = findViewById(R.id.textview_resendCode_button);
        txt_verify_email = findViewById(R.id.textview_verify_email);
    }
    private void setFontUI() {
        textInput_email.setTypeface(Common.setFontKohoBold(getAssets()));
        textInput_phone.setTypeface(Common.setFontKohoBold(getAssets()));
        textInput_fullName.setTypeface(Common.setFontKohoBold(getAssets()));
        textInput_birthDay.setTypeface(Common.setFontKohoBold(getAssets()));

        edt_birthday.setTypeface(Common.setFontKohoBold(getAssets()));
    }
    private void chooseDateOfBirth() {
        //Get date, month, year from user
        String [] dateParts = edt_birthday.getText().toString().split("-");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        //Calendar date of birth
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        }, Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        edt_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }
    private void updateLabel(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(Common.FORMAT_DATE, Locale.US);
        edt_birthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    @IntRange(from = 0, to = 3)
    public int getConnectionType() {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;
    }
}