package com.lobxy.instagramclone.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Login extends AppCompatActivity {

    private static final String TAG = "Login Activity";
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private EditText et_password, et_email;

    ProgressDialog dialog;

    private boolean signUpMode = false;

    Button mode_login, mode_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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

            if (connectivity()) {
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
                                showAlert("Error", task.getException().getLocalizedMessage());

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
                                Log.i(TAG, "onComplete: Login Unsuccessful \n Error: " + task.getException().getLocalizedMessage());
                                showAlert("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(this, "Can't reach to internet", Toast.LENGTH_LONG).show();
            }
        }

    }


    private boolean connectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
