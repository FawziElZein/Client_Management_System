package com.clientsinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParseSetupActivity extends AppCompatActivity {

    @BindView(R.id.app_id_text)
    EditText appIdEditText;

    @BindView(R.id.client_key_text)
    EditText clientKeyEditText;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String appIdPref = sharedPreferences.getString(getString(R.string.app_id_pref), null);
        String clientKeyPref = sharedPreferences.getString(getString(R.string.client_key_pref), null);

        if (appIdPref != null && clientKeyPref != null) {

            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                // do stuff with the user
                Intent intent = new Intent(ParseSetupActivity.this, MainActivity.class);
                startActivityForResult(intent, 1);
            } else {
                // show the signup or login screen
                Intent intent = new Intent(ParseSetupActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        } else {
            setContentView(R.layout.activity_parse_setup);
            ButterKnife.bind(this);
        }
    }


    public void parseRegister(View view) {

        String appId = appIdEditText.getText().toString();
        String clientKey = clientKeyEditText.getText().toString();

        if (appId.isEmpty() || clientKey.isEmpty())
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
        else {

            Parse.destroy();
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(appId)
                    // if defined
                    .clientKey(clientKey)
                    .server(getString(R.string.back4app_server_url))
                    .enableLocalDataStore()
                    .build()
            );

            LoadingDialog loadingDialog = new LoadingDialog("Initializing parse server ...");
            loadingDialog.show(getSupportFragmentManager(), "dialog");
            loadingDialog.setCancelable(false);

            // Save the current Installation to Back4App
            ParseInstallation.getCurrentInstallation().saveInBackground(e -> {

                loadingDialog.dismiss();

                if (e != null) {
                    if (Objects.equals(e.getMessage(), "unauthorized")) {
                        Log.i("Info code number", "" + e.getCode());
                        Toast.makeText(ParseSetupActivity.this, "Unauthorized access", Toast.LENGTH_LONG).show();
                    }
                    if (e.getCode() == ParseException.CONNECTION_FAILED) {
                        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Connection problem")
                                .setMessage("Failed to initialize parse server. Please make sure you have an internet connection and try again.")
                                .setPositiveButton("Retry", (dialogInterface, i) -> parseRegister(view))
                                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                                }).show();
                    }
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.app_id_pref), appId);
                    editor.putString(getString(R.string.client_key_pref), clientKey);
                    editor.apply();

                    parseDataBaseSetup();

                    Intent intent = new Intent(ParseSetupActivity.this, LoginActivity.class);
                    ParseSetupActivity.this.startActivityForResult(intent, 1);
                }
            });

        }
    }

    private void parseDataBaseSetup() {

        ParseObject client = new ParseObject("Client");
        client.put("name", "ForCreatingClientTable");
        client.put("phone_number", "");
        client.put("address", "");

        ParseObject category = new ParseObject("Category");
        category.put("name", "ForCreatingCategoryTable");
        category.put("price", 0);
        category.put("color", 0);

        ParseObject purchase = new ParseObject("Purchase");
        purchase.put("client", client);
        purchase.put("category", category);
        purchase.put("weight", 0);
        purchase.put("date", new Date());
        purchase.put("cash", 0);
        purchase.put("debt", 0);
        purchase.put("check", 0);
        purchase.put("outlay", 0);
        purchase.put("note", "ForCreatingPurchaseTable");

        purchase.saveInBackground(e -> {
            ParseQuery.getQuery("Purchase").whereEqualTo("note", "ForCreatingPurchaseTable").getFirstInBackground()
                    .continueWithTask(task -> task.getResult().deleteInBackground(), Task.BACKGROUND_EXECUTOR);
            ParseQuery.getQuery("Client").whereEqualTo("name", "ForCreatingClientTable").getFirstInBackground()
                    .continueWithTask(task1 -> task1.getResult().deleteInBackground(), Task.BACKGROUND_EXECUTOR);
            ParseQuery.getQuery("Category").whereEqualTo("name", "ForCreatingCategoryTable").getFirstInBackground()
                    .continueWithTask(task2 -> task2.getResult().deleteInBackground(), Task.BACKGROUND_EXECUTOR);
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            finish();
        else if (resultCode == MainActivity.LOG_OUT) {
            Intent intent = new Intent(ParseSetupActivity.this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }
}