package com.ruizhou.blueterminal.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ruizhou.blueterminal.R;

public class userAuthActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);
        UISetup();
        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String pwdString = password.getText().toString();
                if(!emailString.equals("") && !pwdString.equals("")){
                    mAuth.signInWithEmailAndPassword(emailString, pwdString)
                            .addOnCompleteListener(userAuthActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(userAuthActivity.this, "Failed Sign in", Toast.LENGTH_SHORT);
                                    }
                                    else{
                                        Intent intent = new Intent(userAuthActivity.this, MainActivity.class);
                                        userAuthActivity.this.startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });

    }

    private void UISetup(){
        email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        password = (EditText) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.LoginButton);

    }

}