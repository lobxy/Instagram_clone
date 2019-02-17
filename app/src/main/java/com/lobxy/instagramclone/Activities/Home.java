package com.lobxy.instagramclone.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lobxy.instagramclone.Adapters.PostHolder;
import com.lobxy.instagramclone.Model.Post;
import com.lobxy.instagramclone.R;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    private RecyclerView mRecyclerView;

    private String TAG = "Home";

    private ProgressBar mProgressBar;

    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference("Posts");

        mProgressBar = findViewById(R.id.home_progressBar);

        mRecyclerView = findViewById(R.id.home_recyclerView);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton fab = findViewById(R.id.home_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, AddPostActivity.class));
            }
        });

      //  checkUserData();

        setData();

    }

    private void setData() {
        //get posts data and show it to the user.

        mProgressBar.setVisibility(View.VISIBLE);

        Query query = mReference.limitToFirst(10);

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        adapter = new FirebaseRecyclerAdapter<Post, PostHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull Post model) {
                holder.setImage_post(model.getImageDownloadUrl());
                holder.setImage_userProfile(model.getProfilePicImageUrl());

                holder.setText_caption(model.getCaption());
                holder.setText_time(model.getTime());
                holder.setText_userName(model.getFullName());

            }

            @NonNull
            @Override
            public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);
                return new PostHolder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);

        mProgressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void checkUserData() {
        //check user data, if doesn't exists, make him register again.

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");

        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if (dataSnapshot.exists()) {
                    setData();
                } else {
                    startActivity(new Intent(Home.this, Register.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Log.i(TAG, "Error: " + databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
