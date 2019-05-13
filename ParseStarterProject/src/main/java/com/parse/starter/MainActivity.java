/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private TextView signUpLoginChangerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout relativeLayout = findViewById(R.id.container_relative_layout);
        relativeLayout.setOnClickListener(this);

        ImageView logoImageView = findViewById(R.id.logo_image_view);
        logoImageView.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null)
            moveToUsersActivity();

        userNameEditText = findViewById(R.id.id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_button);
        signUpLoginChangerTextView = findViewById(R.id.mode_changer_text_view);

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (signUpButton.getVisibility() == View.VISIBLE)
                        signUpButton.callOnClick();
                    else if (loginButton.getVisibility() == View.VISIBLE)
                        loginButton.callOnClick();
                }
                return false;
            }
        });

        signUpLoginChangerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpButton.getVisibility() == View.VISIBLE) {
                    signUpButton.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    signUpLoginChangerTextView.setText("Or,  Signup");
                } else {
                    signUpButton.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.INVISIBLE);
                    signUpLoginChangerTextView.setText("Or,  Login");
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "A username and password are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser newUser = new ParseUser();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Succesful");
                            moveToUsersActivity();
                        } else {
                            Log.i("Signup", "Failed");
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "A username and password are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("Login", "Successful");
                            moveToUsersActivity();
                        }
                    }
                });
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.container_relative_layout || v.getId() == R.id.logo_image_view) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void moveToUsersActivity(){
        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
        startActivity(intent);
        finish();
    }

}