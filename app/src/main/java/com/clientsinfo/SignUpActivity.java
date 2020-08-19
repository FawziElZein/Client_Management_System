package com.clientsinfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {


    @BindView(R.id.username_text)
    EditText username;

    @BindView(R.id.password_text)
    EditText password;

    @BindView(R.id.password_again_text)
    EditText confirmPassword;

    @BindView(R.id.email_text)
    EditText email;

    private static final int SIGNUP_CANCELED = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_signup);

        ButterKnife.bind(this);

    }

    public void signUp(View view) {

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString();
        String email = this.email.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Please make sure your passwords match", Toast.LENGTH_LONG).show();
        } else {

            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            LoadingDialog loadingDialog = new LoadingDialog("Creating new user ...");
            loadingDialog.show(getSupportFragmentManager(), "dialog");
            loadingDialog.setCancelable(false);

            user.signUpInBackground(e -> {
                loadingDialog.dismiss();
                if (e == null) {
                    Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    SignUpActivity.this.startActivityForResult(intent, 1);
                } else {
                    if (e.getCode() == ParseException.CONNECTION_FAILED) {
                        new AlertDialog.Builder(SignUpActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Connection problem")
                                .setMessage("Couldn't connect to parse server. Please make sure you have an internet connection and try again.")
                                .setPositiveButton("Retry", (dialogInterface, i) -> signUp(view))
                                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                                }).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        setResult(SIGNUP_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED)
            finish();
        else if (resultCode == MainActivity.LOG_OUT) {
            setResult(MainActivity.LOG_OUT);
            finish();
        }
    }
}