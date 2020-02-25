package com.example.sapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PRegCode=1;
    static int REQUESCODE=1;
    Uri pickedImgUri;


    private EditText userEmail, userPassword,userPassword2,userName;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail=findViewById(R.id.editText4);
        userPassword=findViewById(R.id.editText7);
        userPassword2=findViewById(R.id.editText8);
        userName=findViewById(R.id.editText3);
        regBtn=findViewById(R.id.button);

        mAuth=FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email=userEmail.getText().toString();
                final String password=userPassword.getText().toString();
                final String password2=userPassword2.getText().toString();
                final String name=userName.getText().toString();

                if(email.isEmpty() || name.isEmpty() || password.isEmpty() || password2.isEmpty() || !password.equals(password2)) {
                    showMessage("Please fill all the fields");
                    regBtn.setVisibility(View.VISIBLE);

                }
                else{
                    //Toast.makeText(getApplicationContext(),"step1",Toast.LENGTH_LONG).show();
                    CreateUserAccount(email,name,password);

                }


            }
        });




        ImgUserPhoto =findViewById(R.id.img);
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT>=22){

                    checkAndRequestForpermission();
                }
                else
                {
                    openGallery();
                }

            }
        });


    }

    private void CreateUserAccount(String email, final String name, String password) {



        //Toast.makeText(getApplicationContext(),"step2",Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Account Created");
                            updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());

                        }
                        else{
                            showMessage("Account creation failed");
                        }
                    }
                });


    }


    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser){

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath= mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    showMessage("Register Completed");
                                    updateUI();
                                }

                            }
                        });

                    }
                });
            }
        });

    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(),Homedrawer.class);
        startActivity(homeActivity);
    }


    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    private void openGallery(){

        Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);



    }

    private void checkAndRequestForpermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PRegCode);
            }
        else
        {
            openGallery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            ImgUserPhoto.setImageURI(pickedImgUri);


        }


    }
}
