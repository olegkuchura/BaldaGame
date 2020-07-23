package com.adlab.balda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.R;
import com.adlab.balda.databinding.ActivityGameSettingsBinding;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.model.GameLab;
import com.adlab.balda.model.GamePlayer;
import com.adlab.balda.utils.PresenterManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.DataBindingUtil;

public class GameSettingsActivity extends AppCompatActivity implements GameSettingsContract.View, View.OnClickListener{

    private GameSettingsContract.Presenter mPresenter;

    private ActivityGameSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_settings);

        getSupportActionBar().setTitle(R.string.settings_for_one_man_game);

        binding.etInitWord.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.etInitWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString().toLowerCase();
                mPresenter.initWordChanged(word);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        binding.bIncreaseFieldSize.setOnClickListener(this);
        binding.bReduceFieldSize.setOnClickListener(this);
        binding.bStartGame.setOnClickListener(this);
        binding.ibGenerateWord.setOnClickListener(this);

        binding.etInitWord.requestFocus();

        binding.tvFieldSizeValue.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(GameSettingsActivity.this);
                textView.setTextSize(24);
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(ResourcesCompat.getFont(GameSettingsActivity.this, R.font.stix_two_text));
                return textView;
            }
        });

        binding.rbFieldType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb1) {
                    mPresenter.fieldTypeChanged(FieldType.SQUARE);
                } else {
                    mPresenter.fieldTypeChanged(FieldType.HEXAGON);
                }
            }
        });

        PresenterManager.provideGameSettingsPresenter(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull GameSettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setInitWord(String word) {
        binding.etInitWord.setText(word);
        binding.etInitWord.setSelection(word.length());
    }

    @Override
    public void showNonExistentWordError() {
        binding.til.setError(getString(R.string.non_existent_word));
    }

    @Override
    public void showEmptyWordError() {
        binding.til.setError(getString(R.string.need_enter_word));
    }

    @Override
    public void showNonAppropriateWordLength(int correctLength) {
        binding.tvNotAppropriateWordLengthError.setText(getString(R.string.non_appropriate_word_length, correctLength));
        binding.tvNotAppropriateWordLengthError.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNonAppropriateWordLength() {
        binding.tvNotAppropriateWordLengthError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideInitWordError() {
        binding.til.setError(null);
    }

    @Override
    public void updateFiledSize(FieldSizeType sizeType, boolean withAnim, boolean biggerValue) {
        if (withAnim) {
            if (biggerValue) {
                binding.tvFieldSizeValue.setInAnimation(this, R.anim.slide_in_right);
                binding.tvFieldSizeValue.setOutAnimation(this, R.anim.slide_out_left);
            } else {
                binding.tvFieldSizeValue.setInAnimation(this, R.anim.slide_in_left);
                binding.tvFieldSizeValue.setOutAnimation(this, R.anim.slide_out_right);
            }
        } else {
            binding.tvFieldSizeValue.setInAnimation(null);
            binding.tvFieldSizeValue.setOutAnimation(null);
        }
        binding.tvFieldSizeValue.setText(sizeType.stringValue);
    }

    @Override
    public void setStartGameEnabled(boolean enabled) {
        binding.bStartGame.setEnabled(enabled);
        binding.bStartGame.setClickable(enabled);
    }

    @Override
    public void setIncreaseFieldSizeEnabled(boolean enabled) {
        binding.bIncreaseFieldSize.setEnabled(enabled);
        binding.bIncreaseFieldSize.setClickable(enabled);
    }

    @Override
    public void setReduceFieldSizeEnabled(boolean enabled) {
        binding.bReduceFieldSize.setEnabled(enabled);
        binding.bReduceFieldSize.setClickable(enabled);
    }

    @Override
    public void navigateToGameScreen(String word, FieldSizeType fieldSize, FieldType fieldType) {
        GameLab gameLab = GameLab.getInstance();
        GamePlayer player =  new GamePlayer();
        gameLab.createGame(word, fieldSize, fieldType, player, getApplicationContext());
        startActivity(GameActivity.createIntent(this));
        finish();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, GameSettingsActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.resetView();
        if (isFinishing()) {
            PresenterManager.resetGameSettingsPresenter();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_increase_field_size:
                mPresenter.increaseFieldSize();
                break;
            case R.id.b_reduce_field_size:
                mPresenter.reduceFieldSize();
                break;
            case R.id.ib_generate_word:
                mPresenter.generateRandomWord();
                break;
            case R.id.b_start_game:
                mPresenter.startGame();
                break;
        }
    }

}
