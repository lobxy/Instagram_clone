package com.lobxy.instagramclone.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lobxy.instagramclone.R;

public class Home extends AppCompatActivity {

    FirebaseAuth auth;
    private String TAG = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton fab = findViewById(R.id.home_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, AddPostActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");

        progress.show();
        auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progress.dismiss();
                if (!dataSnapshot.exists()) {
                    startActivity(new Intent(Home.this, Register.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progress.dismiss();
                Log.i(TAG, "Error: " + databaseError.getMessage());
            }
        });

    }
}
