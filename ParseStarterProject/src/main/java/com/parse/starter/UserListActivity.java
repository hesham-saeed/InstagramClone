package com.parse.starter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Intent.ACTION_PICK;

public class UserListActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 444;
    private static final String TAG = "UserListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        final List<String> users = new ArrayList<>();

        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);

        ListView usersListView = findViewById(R.id.users_list_view);

        usersListView.setAdapter(listAdapter);


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {

                    for (ParseUser user : objects) {
                        users.add(user.getUsername());
                    }
                    listAdapter.notifyDataSetChanged();

                }
            }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent userSelectedIntent = new Intent(UserListActivity.this, UserFeedActivity.class);
                userSelectedIntent.putExtra("username", users.get(position));
                startActivity(userSelectedIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_share:
                dispatchPickIntent();
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                Intent loginIntent = new Intent(UserListActivity.this, MainActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchPickIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 222);
            return;
        }
        startIntent();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 22 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startIntent();
            }
        }
    }

    private void startIntent() {
        Intent pickImageIntent = new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {

            Uri selectedImage = data.getData();
            try {
                final ProgressBar progressBar = new ProgressBar(this);
                progressBar.setVisibility(View.VISIBLE);


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                String dateTimeStamp = new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date());


                ParseFile file = new ParseFile("IMG_" + dateTimeStamp + ".jpg", outputStream.toByteArray());
                ParseObject object = new ParseObject("Images");
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("image", file);


                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(UserListActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserListActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, e.toString());
                            Log.i(TAG, e.getMessage());
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
