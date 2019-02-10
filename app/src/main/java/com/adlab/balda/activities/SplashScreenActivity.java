package com.adlab.balda.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.adlab.balda.R;
import com.adlab.balda.database.BaldaDataBase;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        new CreateDatabaseTask().execute();
    }


    class CreateDatabaseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new BaldaDataBase(SplashScreenActivity.this).getReadableDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        }

    }
}
