package com.adlab.balda.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adlab.balda.R;
import com.adlab.balda.database.BaldaDataBase;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        new CreateDabaseTask().execute();
    }


    class CreateDabaseTask extends AsyncTask<Void, Void, Void> {

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
