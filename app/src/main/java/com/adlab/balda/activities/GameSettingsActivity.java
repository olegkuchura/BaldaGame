package com.adlab.balda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.adlab.balda.adapters.PlayersAdapter;
import com.adlab.balda.contracts.GameSettingsContract;
import com.adlab.balda.R;
import com.adlab.balda.databinding.ActivityGameSettingsBinding;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.enums.FieldType;
import com.adlab.balda.enums.GameType;
import com.adlab.balda.utils.PresenterManager;
import com.adlab.balda.utils.UtilsKt;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GameSettingsActivity extends AppCompatActivity
        implements GameSettingsContract.View, View.OnClickListener{

    @NotNull
    public static Intent createIntent(Context context, @NotNull GameType gameType) {
        Intent intent = new Intent(context, GameSettingsActivity.class);
        intent.putExtra("gameType", gameType.toString());
        return intent;
    }

    private GameSettingsContract.Presenter mPresenter;

    private ActivityGameSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_settings);

        String param = getIntent().getStringExtra("gameType");
        GameType gameType = GameType.SINGLE;
        if (param != null) {
            gameType = GameType.valueOf(param);
        }
        if (gameType == GameType.SINGLE)
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.settings_for_one_man_game);
        else
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.settings_for_multiplayer_game);

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
        binding.ibAddPlayer.setOnClickListener(this);

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

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        updateStartGameButtonState();
                    }
                });

        //todo remove context in params
        PresenterManager.provideGameSettingsPresenter(this, this);

        binding.rvPlayers.setAdapter(new PlayersAdapter(mPresenter));

        binding.svGameSettingsRoot.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                updateStartGameButtonState();
            }
        });

        mPresenter.start(gameType);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStartGameButtonState();
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
    public void showPlayerNamesBlock(boolean show) {
        int visibility;
        if (show) visibility = View.VISIBLE;
        else visibility = View.GONE;
        binding.cvPlayers.setVisibility(visibility);
    }

    @Override
    public void playerAdded() {
        PlayersAdapter adapter = (PlayersAdapter) binding.rvPlayers.getAdapter();
        if (adapter == null) return;
        int playersCount = adapter.getPlayersCount() + 1;
        adapter.setPlayersCount(playersCount);
        adapter.notifyItemInserted(playersCount - 1);
    }

    @Override
    public void playerDeleted(int position) {
        PlayersAdapter adapter = (PlayersAdapter) binding.rvPlayers.getAdapter();
        if (adapter == null) return;
        int playersCount = adapter.getPlayersCount() - 1;
        adapter.setPlayersCount(playersCount);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void setPlayerEdit(String playerName) {
        binding.etPlayerName.setText(playerName);
    }

    @Override
    public void showNicknameError() {
        Snackbar.make(binding.svGameSettingsRoot, R.string.player_name_error, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showPlayersCountErrorMax(int playersCountLimit) {
        Snackbar.make(binding.svGameSettingsRoot,
                getString(R.string.player_name_count_limit_max, playersCountLimit), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showPlayersCountErrorMin(int playersCountLimit) {
        Snackbar.make(binding.svGameSettingsRoot,
                getString(R.string.player_name_count_limit_min, playersCountLimit), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showAlreadyUsedNicknameError() {
        Snackbar.make(binding.svGameSettingsRoot,
                getString(R.string.player_name_already_used), Snackbar.LENGTH_LONG)
                .show();
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
        binding.tvFieldSizeValue.setText(getStringValueOfFieldSize(sizeType));
    }

    private String getStringValueOfFieldSize(@NotNull FieldSizeType sizeType) {
        switch (sizeType) {
            case SMALL: return getString(R.string.field_small);
            case MEDIUM: return getString(R.string.field_medium);
            case LARGE: return getString(R.string.field_large);
            case EXTRA_LARGE: return getString(R.string.field_extra_large);
        }
        throw new IllegalArgumentException("Unknown field size");
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
    public void navigateToGameScreen(GameType gameType) {
        startActivity(gameType == GameType.SINGLE ?
                GameActivity.createIntent(this) :
                MultiplayerGameActivity.createIntent(this));
        finish();
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
                mPresenter.startGameClicked();
                break;
            case R.id.ib_add_player:
                mPresenter.onAddPlayerClicked(Objects.requireNonNull(binding.etPlayerName.getText()).toString());
                break;
        }
    }

    private void updateStartGameButtonState() {
        View view = binding.svGameSettingsRoot.getChildAt(binding.svGameSettingsRoot.getChildCount() - 1);
        int diff = (view.getBottom() - (binding.svGameSettingsRoot.getHeight() + binding.svGameSettingsRoot.getScrollY()));

        if (diff <= 0) {
            binding.bStartGame.extend();
        } else {
            binding.bStartGame.shrink();
        }
    }

}
