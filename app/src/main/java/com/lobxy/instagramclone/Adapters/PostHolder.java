package com.lobxy.instagramclone.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lobxy.instagramclone.R;
import com.squareup.picasso.Picasso;

public class PostHolder extends RecyclerView.ViewHolder {

    private ImageView image_userProfile;
    private ImageView image_post;

    private TextView text_userName;
    private TextView text_time;
    private TextView text_caption;

    public PostHolder(@NonNull View itemView) {
        super(itemView);

        image_post = itemView.findViewById(R.id.post_image);
        image_userProfile = itemView.findViewById(R.id.post_user_image);

        text_userName = itemView.findViewById(R.id.post_user_name);
        text_time = itemView.findViewById(R.id.post_time);
        text_caption = itemView.findViewById(R.id.post_caption);

    }

    public void setImage_userProfile(String profile_url) {
        Picasso.get().load(profile_url).into(image_userProfile);
    }

    public void setImage_post(String postUrl) {
        Picasso.get().load(postUrl).into(image_post);
    }

    public void setText_time(String time) {
        text_time.setText(time);
    }

    public void setText_userName(String name) {
        text_userName.setText(name);
    }

    public void setText_caption(String caption) {
        text_caption.setText(caption);
    }


}
