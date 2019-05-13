package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    private RecyclerView feedRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        feedRecyclerView = findViewById(R.id.user_feed_recycler_view);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final List<String> userImagesURLs = new ArrayList<>();

        String username = getIntent().getStringExtra("username");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(username + "'s Feed");
        final ParseQuery<ParseObject> object = new ParseQuery<ParseObject>("Images");
        object.whereEqualTo("username", username);
        object.orderByDescending("createdAt");

        object.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject parseObject : objects) {
                    ParseFile file = parseObject.getParseFile("image");
                    userImagesURLs.add(file.getUrl());
                }
                feedRecyclerView.setAdapter(new FeedAdapter(UserFeedActivity.this, userImagesURLs));
            }
        });


    }

    class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

        List<String> userImagesURLs;
        Context context;

        public FeedAdapter(Context context, List<String> userImagesURLs) {
            this.userImagesURLs = userImagesURLs;
            this.context = context;
        }

        @NonNull
        @Override
        public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_feed_instance, viewGroup, false);

            return new FeedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int i) {
            Glide.with(context)
                    .load(userImagesURLs.get(i))
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(feedViewHolder.feedInstanceImageView);
        }

        @Override
        public int getItemCount() {
            return userImagesURLs.size();
        }
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {

        ImageView feedInstanceImageView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feedInstanceImageView = itemView.findViewById(R.id.list_item_feed_instance_image);
        }
    }
}
