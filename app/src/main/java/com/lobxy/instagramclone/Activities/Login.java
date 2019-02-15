package com.lobxy.instagramclone.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lobxy.instagramclone.R;
import com.lobxy.instagramclone.Utils.Connectivity;
import com.lobxy.instagramclone.Utils.ShowPopUps;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login Activity";
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private EditText et_password, et_email;

    ProgressDialog dialog;

    private boolean signUpMode = false;

    Button mode_login, mode_register;

    ShowPopUps popUps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        popUps = new ShowPopUps(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setInverseBackgroundForced(true);
        dialog.setCancelable(false);

        et_password = findViewById(R.id.login_password);
        et_email = findViewById(R.id.login_email);

        final Button submit = findViewById(R.id.login_submit);

        mode_login = findViewById(R.id.login_mode_login);
        mode_register = findViewById(R.id.login_mode_register);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        mode_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpMode = true;
                submit.setText("Sign Up");
            }
        });
        mode_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpMode = false;
                submit.setText("Login");
            }
        });
    }

    private void validate() {

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            et_email.requestFocus();
            et_email.setError("Field is empty");

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            et_email.requestFocus();
            et_email.setError("Invalid email address");

        } else if (TextUtils.isEmpty(password)) {

            et_password.requestFocus();
            et_password.setError("Field is empty");

        } else if (password.length() < 6) {

            et_password.requestFocus();
            et_password.setError("Password is less than 6 characters");

        } else {
            Connectivity connectivity = new Connectivity(this);

            if (connectivity.check()) {
                if (signUpMode) {
                    //register the user.
                    dialog.show();
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Log.i(TAG, "Sign Up successful");
                                startActivity(new Intent(Login.this, Register.class));
                                finish();
                            } else {
                                dialog.dismiss();
                                Log.i(TAG, "Login Unsuccessful Error: " + task.getException().getLocalizedMessage());
                                popUps.showAlertDialog("Error", task.getException().getLocalizedMessage());

                            }
                        }
                    });
                } else {
                    //login the user
                    dialog.show();
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Log.i(TAG, "onComplete: Login successful");
                                //check for data if you want.

                                startActivity(new Intent(Login.this, Home.class));
                                finish();
                            } else {
                                dialog.dismiss();
                                popUps.showAlertDialog("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(this, "Can't reach to internet", Toast.LENGTH_LONG).show();
            }
        }

    }


}
