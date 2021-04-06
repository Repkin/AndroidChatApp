package com.example.engineer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private Button RegisterButton;
    private EditText UserEmail, UserPassword;
    private TextView AlreadyHaveAccount;

    private FirebaseAuth myAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RootRef = FirebaseDatabase.getInstance().getReference();

        myAuth = FirebaseAuth.getInstance();

        InitializerFields();

        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }


        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please, enter Your email ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please, enter Your password ", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    String currentUserID = myAuth.getCurrentUser().getUid();
                    RootRef.child("Users").child(currentUserID).setValue("");
                    SendUserToLoginActivity();
                Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                }
                else
                {
                    String message = task.getException().toString();
                    Toast.makeText(RegisterActivity.this, "Error: " + message + " :(", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }
        });
        }

    }



    private void InitializerFields() {
        RegisterButton = (Button) findViewById(R.id.buttonSignUp);
        UserEmail = (EditText) findViewById(R.id.registerEmail);
        UserPassword = (EditText) findViewById(R.id.registerPassword);
        AlreadyHaveAccount = (TextView) findViewById(R.id.loginText);
        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent toLoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(toLoginIntent);
        finish();
    }
}