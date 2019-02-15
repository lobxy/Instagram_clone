package com.lobxy.instagramclone.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lobxy.instagramclone.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";
    CircleImageView profileImage;
    TextView tv_fullName, tv_description;

    FirebaseAuth auth;
    DatabaseReference userDataRef;
    String uid, fullName, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        tv_description = findViewById(R.id.profile_description);
        tv_fullName = findViewById(R.id.profile_name);
        profileImage = findViewById(R.id.profile_image);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        userDataRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if (dataSnapshot.exists()) {
                    fullName = dataSnapshot.child("fullName").getValue(String.class);
                    description = dataSnapshot.child("description").getValue(String.class);

                    String imageUrl = dataSnapshot.child("profileUrl").getValue(String.class);
                    Log.i(TAG, "onDataChange: url " + imageUrl);

                    tv_description.setText(description);
                    tv_fullName.setText(fullName);

                    Picasso.get().load(imageUrl).placeholder(R.drawable.add_image).into(profileImage);

                } else {
                    //data doesn't exists.
                    Toast.makeText(Profile.this, "Shit happened", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Profile.this, Register.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();

                Log.i(TAG, "SetData: " + databaseError.getMessage());
            }
        });

    }
}
