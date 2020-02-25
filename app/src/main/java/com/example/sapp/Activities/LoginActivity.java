package com.example.sapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    ImageView backbtn;
    Button reg;
    private EditText userMail,userPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backbtn=findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userMail = findViewById(R.id.editText);
        userPassword = findViewById(R.id.editText2);
        btnLogin = findViewById(R.id.loginBtn);
        mAuth=FirebaseAuth.getInstance();
        HomeActivity=new Intent(this, com.example.sapp.Activities.Homedrawer.class);

        reg=findViewById(R.id.regb);
        reg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent =  new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail= userMail.getText().toString();
                final String password= userPassword.getText().toString();


                if (mail.isEmpty() || password.isEmpty()){
                    showMessage("Please fill all the fields");
                }
                else{
                    signIn(mail,password);
                }
            }
        });

    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    updateUI();
                }
                else{
                    showMessage(task.getException().getMessage());

                }
            }
        });
    }

    private void updateUI() {
    startActivity(HomeActivity);
    finish();
    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            updateUI();
        }
    }
}
