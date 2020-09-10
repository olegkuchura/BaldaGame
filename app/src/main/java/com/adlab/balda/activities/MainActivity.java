package com.adlab.balda.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adlab.balda.R;
import com.adlab.balda.enums.GameType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonStartOneManGame = findViewById(R.id.b_one_man_game);
        Button buttonStartGameWithAndroid = findViewById(R.id.b_game_with_android);
        Button buttonStartGameWithFriends = findViewById(R.id.b_game_with_friends);
        Button buttonSettings = findViewById(R.id.b_settings);

        buttonStartOneManGame.setOnClickListener(this);
        buttonStartGameWithAndroid.setOnClickListener(this);
        buttonStartGameWithFriends.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_one_man_game:
                startGameSettingsActivity(GameType.SINGLE);
                break;
            case R.id.b_game_with_android:
                Toast.makeText(this, "Sorry, but game with Android isn't available yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b_game_with_friends:
//                Toast.makeText(this, "Sorry, but game with friends isn't available yet", Toast.LENGTH_SHORT).show();
                startGameSettingsActivity(GameType.MULTIPLAYER);
                break;
            case R.id.b_settings:
                Toast.makeText(this, "Sorry, but settings aren't available yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void startGameSettingsActivity(GameType gameType) {
        startActivity(GameSettingsActivity.createIntent(this, gameType));
    }
}
