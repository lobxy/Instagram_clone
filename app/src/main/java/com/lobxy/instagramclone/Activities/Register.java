package com.lobxy.instagramclone.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lobxy.instagramclone.Model.UserRegister;
import com.lobxy.instagramclone.R;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register Activity";

    ImageView addImage;
    FirebaseAuth auth;
    DatabaseReference reference;
    EditText et_fullName, et_userName;
    ProgressDialog dialog;

    private StorageReference mStorageRef;

    public static final int IMAGE_CAPTURE_CODE = 0;
    public static final int GALLERY_IMPORT_CODE = 1;

    String uid, email, device_token, fullName, userName, profileUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...\nDon't close the application!");
        dialog.setInverseBackgroundForced(true);
        dialog.setCancelable(false);

        addImage = findViewById(R.id.reg_imageView);
        et_fullName = findViewById(R.id.reg_name);
        et_userName = findViewById(R.id.reg_username);


        Button submit = findViewById(R.id.reg_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importImage();
            }
        });

    }

    private void importImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert")
                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_IMPORT_CODE);

                    }
                }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMAGE_CAPTURE_CODE);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;
        if (requestCode == GALLERY_IMPORT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addImage.setImageURI(imageUri);
            uploadImage(imageUri);


        } else if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK && data.getExtras() != null) {
            imageUri = data.getData();
            addImage.setImageURI(imageUri);
            uploadImage(imageUri);
        }


    }

    private void uploadImage(Uri imageUri) {

        //assign the user id as the name for the profile pic.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final StorageReference filepath = mStorageRef.child("Profile_Pictures").child(user.getUid());
        //maybe check for uid being null

        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "onSuccess: Upload success");
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profileUrl = uri.toString();
                        Log.i(TAG, "download url: " + profileUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Download url error: " + e.getLocalizedMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showAlert("Upload Image Error: ", e.getLocalizedMessage());
            }
        });


    }


    private void validate() {
        userName = et_userName.getText().toString().trim();
        fullName = et_fullName.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            et_userName.requestFocus();
            et_userName.setError("Field is empty");
        } else if (TextUtils.isEmpty(fullName)) {
            et_fullName.requestFocus();
            et_fullName.setError("Field is empty");
        } else {
            submitData();
        }

    }


    private void submitData() {
        if (profileUrl == null) {
            Toast.makeText(this, "Profile pic not set yet.", Toast.LENGTH_LONG).show();
        } else {
            email = auth.getCurrentUser().getEmail();

            dialog.show();
            auth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    device_token = getTokenResult.getToken();
                    uid = auth.getCurrentUser().getUid();

                    UserRegister userRegister = new UserRegister(device_token, fullName, uid, userName, profileUrl);

                    reference.child(uid).setValue(userRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Log.i(TAG, "onComplete: Data submitted ");

                                startActivity(new Intent(Register.this, Home.class));
                                finish();

                            } else {
                                dialog.dismiss();

                                Log.i(TAG, "onFailure:Token Error: " + task.getException().getLocalizedMessage());
                                showAlert("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();

                    Log.i(TAG, "onFailure:Token Error: " + e.getLocalizedMessage());
                    Toast.makeText(Register.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }


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
