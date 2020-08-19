package com.clientsinfo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.username_text)
    EditText username;

    @BindView(R.id.password_text)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parse_login);
        ButterKnife.bind(this);

    }

    public void login(View view) {

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (username.isEmpty() || password.isEmpty())
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
        else {

            LoadingDialog loadingDialog = new LoadingDialog("Logging in ...");
            loadingDialog.show(getSupportFragmentManager(), "dialog");
            loadingDialog.setCancelable(false);

            ParseUser.logInInBackground(username, password, (user, e) -> {
                loadingDialog.dismiss();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    if (e.getCode() == ParseException.CONNECTION_FAILED) {
                        new AlertDialog.Builder(LoginActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Connection problem")
                                .setMessage("Failed to login. Please make sure you have an internet connection and try again.")
                                .setPositiveButton("Retry", (dialogInterface, i) -> login(view))
                                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                                }).show();
                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            finish();
    }

}