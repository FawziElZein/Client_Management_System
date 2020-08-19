package com.clientsinfo;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.parse.Parse;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String appIdPref = sharedPreferences.getString(getString(R.string.app_id_pref), null);
        String clientKeyPref = sharedPreferences.getString(getString(R.string.client_key_pref), null);


        if (appIdPref != null && clientKeyPref != null) {

            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(appIdPref)
                    // if defined
                    .clientKey(clientKeyPref)
                    .server(getString(R.string.back4app_server_url))
                    .enableLocalDataStore()
                    .build()
            );
        }


    }
}
