package com.capstone.foodify.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAndProfileActivity extends AppCompatActivity {

    private static final String FOLDER_DIRECTORY = "UserImages/";
    TextInputLayout textInput_email, textInput_phone, textInput_fullName, textInput_birthDay;
    EditText edt_birthday, edt_email, edt_phone, edt_fullName;
    final Calendar myCalendar= Calendar.getInstance();
    LinearLayout change_password;
    Button update_button, update_image_button;
    ImageView back_image;
    RoundedImageView profile_avatar;
    private Uri imageUri;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    ConstraintLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_profile);

        initComponent();
        setFontUI();

        //Init data
        initData();

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

                        Toast.makeText(AccountAndProfileActivity.this, "Upload ảnh thành công!", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(AccountAndProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteOldImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Common.CURRENT_USER.getImageUrl());

        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AccountAndProfileActivity.this, "Delete old image successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountAndProfileActivity.this, "Can't delete old image!", Toast.LENGTH_SHORT).show();
            }
        });
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
    }

    private void setFontUI() {
        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
        textInput_phone.setTypeface(Common.setFontBebas(getAssets()));
        textInput_fullName.setTypeface(Common.setFontBebas(getAssets()));
        textInput_birthDay.setTypeface(Common.setFontBebas(getAssets()));

        edt_birthday.setTypeface(Common.setFontBebas(getAssets()));
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
                Common.showErrorServerNotification(AccountAndProfileActivity.this);
                progressLayout.setVisibility(View.GONE);
            }
        });
    }
}