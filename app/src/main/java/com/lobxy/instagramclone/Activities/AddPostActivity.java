package com.lobxy.instagramclone.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lobxy.instagramclone.Model.AddPost;
import com.lobxy.instagramclone.R;
import com.lobxy.instagramclone.Utils.ShowPopUps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "Add post";

    private ImageView image_addImage;
    private EditText et_caption;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private StorageReference mStorageReference;

    private String mImageCaption, mImageDownloadUrl, mPostId, mTime, mUid;

    public static final int IMAGE_CAPTURE_CODE = 0;
    public static final int GALLERY_IMPORT_CODE = 1;

    private ProgressDialog dialog;

    Uri imageURI = null;

    private ShowPopUps popUps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        popUps = new ShowPopUps(this);

        setContentView(R.layout.activity_add_post);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...\nDon't close the application!");
        dialog.setInverseBackgroundForced(true);
        dialog.setCancelable(false);

        LinearLayout linearLayout = findViewById(R.id.layout);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference("Posts");
        mStorageReference = FirebaseStorage.getInstance().getReference("Posts");

        mPostId = mReference.push().getKey();

        et_caption = findViewById(R.id.ap_caption);
        image_addImage = findViewById(R.id.ap_addImage);

        Button submitPostButton = findViewById(R.id.ap_postButton);

        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mImageCaption = et_caption.getText().toString().trim();

                if (mImageCaption.isEmpty()) {
                    et_caption.requestFocus();
                    et_caption.setError("Field is empty");
                    return;
                }

                if (connectivity() && imageURI != null) uploadImage(imageURI);

                else popUps.showAlertDialog("Alert", "Not connected to internet.");

            }
        });

        image_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importImage();
            }
        });

    }

    private void importImage() {

        //show alert box to choose import from either camera or gallery.

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

    private void uploadImage(Uri imageUri) {
        dialog.show();

        //assign the user id as the name for the profile pic.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final StorageReference filepath = mStorageReference.child(user.getUid()).child(mPostId);

        //Todo:maybe check for uid being null
        Log.i(TAG, "uploadImage: mPostId: " + mPostId);

        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "onSuccess: Upload success");

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dialog.dismiss();
                        mImageDownloadUrl = uri.toString();
                        Log.i(TAG, "Image Added " + mImageDownloadUrl);

                        getUserData();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        popUps.showAlertDialog("Error", e.getLocalizedMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                popUps.showAlertDialog("Error: ", e.getLocalizedMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;
        if (requestCode == GALLERY_IMPORT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            image_addImage.setImageURI(imageUri);
            imageURI = imageUri;

        } else if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK && data.getExtras() != null) {
            imageUri = data.getData();
            image_addImage.setImageURI(imageUri);
            imageURI = imageUri;
        }

    }

    private void getUserData() {

        if (mImageDownloadUrl == null) {
            Toast.makeText(AddPostActivity.this, "Image not ready.", Toast.LENGTH_LONG).show();
        } else {
            dialog.show();

            //Get current mTime and date.
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            mTime = currentDate + " @ " + currentTime;

            mUid = mAuth.getCurrentUser().getUid();

            //Get user data.
            DatabaseReference userDataReference = FirebaseDatabase.getInstance().getReference("Users").child(mUid);

            userDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userProfilePictureURL, userFullName;

                    if (dataSnapshot.exists()) {
                        userProfilePictureURL = dataSnapshot.child("profileUrl").getValue(String.class);
                        userFullName = dataSnapshot.child("fullName").getValue(String.class);

                        dialog.dismiss();
                        submitPostData(userProfilePictureURL, userFullName);

                    } else {
                        dialog.dismiss();
                        //data doesn't exists. Get the data from the user again.

                        startActivity(new Intent(AddPostActivity.this, Register.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    dialog.dismiss();
                    popUps.showAlertDialog("Error", databaseError.getMessage());
                }
            });

        }


    }

    private void submitPostData(String userProfilePictureURL, String userFullName) {
        dialog.show();
        AddPost addPost = new AddPost(mTime, mImageDownloadUrl, mImageCaption, mPostId, userFullName, userProfilePictureURL, mUid);

        mReference.child(mPostId).setValue(addPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                dialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(AddPostActivity.this, "Post added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddPostActivity.this, Home.class));
                    finish();
                } else popUps.showAlertDialog("Error", task.getException().getMessage());

            }
        });
    }

    public boolean connectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
