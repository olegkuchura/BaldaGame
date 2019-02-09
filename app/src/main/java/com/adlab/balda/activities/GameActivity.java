package com.adlab.balda.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adlab.balda.adapters.FieldRecyclerAdapter;
import com.adlab.balda.R;
import com.adlab.balda.model.ActivityForPlayer;
import com.adlab.balda.model.ActivityPlayer;
import com.adlab.balda.model.Game;
import com.adlab.balda.model.GameLab;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.LinkedList;

public class GameActivity extends AppCompatActivity implements FieldRecyclerAdapter.OnItemClickListener, ActivityForPlayer{
    private static final String SHARED_SETTINGS = "settings";
    private static final String GAME_LANGUAGE = "gameLanguage";
    private static final String DEFAULT_GAME_LANGUAGE= "ru";

    private static final String ENTERED_ITEM = "enteredItem";
    private static final String ENTERED_LETTER = "enteredLetter";
    private static final String ACTIVE_VIEW_NUMBERS = "activeViewNumbers";

    public static final String ROW_COUNT = "rowCount";
    public static final String COL_COUNT = "colCount";
    public static final String INIT_WORD = "initWord";
    public static final String PLAYER_ID = "playerId";

    private ActivityPlayer player;

    private InputMethodManager imm;

    private RecyclerView recyclerView;
    private TextView textViewScore;
    private EditText editTextFieldItem;
    private View viewContent;
    private TextView textViewScoreAnim;

    private ActionMode actionMode = null;

    private Field field;

    public static Intent createIntent(Context context, int playerId, int rowCount, int colCount) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(ROW_COUNT, rowCount);
        intent.putExtra(COL_COUNT, colCount);
        intent.putExtra(PLAYER_ID, playerId);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Single game");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        int rowCount = intent.getIntExtra(ROW_COUNT, 5);
        int colCount = intent.getIntExtra(COL_COUNT, 5);
        int playerId = intent.getIntExtra(PLAYER_ID, -1);
        if (playerId == -1) {
            finish();
            return;
        }

        Game game = GameLab.getInstance().getGame();
        player = (ActivityPlayer) game.getPlayer(playerId);
        if (player == null) {
            player = new ActivityPlayer(this, game);
        }

        viewContent = findViewById(R.id.content);
        recyclerView = findViewById(R.id.activity_game_recycler_view);
        textViewScore = findViewById(R.id.tv_score);
        editTextFieldItem = findViewById(R.id.et_input_field_item);
        textViewScoreAnim = findViewById(R.id.tv_score_anim);

        textViewScore.setText(getString(R.string.score, player.getScore()));

        int enteredViewNumber = -1;
        char enteredLetter = ' ';
        LinkedList<Integer> activeViewNumbers = null;

        if (savedInstanceState != null) {
            enteredViewNumber = savedInstanceState.getInt(ENTERED_ITEM, -1);
            enteredLetter = savedInstanceState.getChar(ENTERED_LETTER, ' ');
            activeViewNumbers = new LinkedList<>();
            int[] arr = savedInstanceState.getIntArray(ACTIVE_VIEW_NUMBERS);
            for (int i: arr) {
                activeViewNumbers.add(i);
            }
        } else {
            activeViewNumbers = new LinkedList<>();
        }

        field = new Field(game.getField(), rowCount, colCount, -1, enteredViewNumber, enteredLetter, activeViewNumbers);
        FieldRecyclerAdapter adapter = new FieldRecyclerAdapter(this, field);
        adapter.setOnItemClickListener(this);
        adapter.setHasStableIds(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, colCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            actionMode = startSupportActionMode(new ActionModeCallback());
            actionMode.setTitle(field.getEnteredLetterSequence().toUpperCase());
        }

        KeyboardVisibilityEvent.setEventListener(
                this, new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            recyclerView.scrollToPosition(field.getSelectedViewNumber());
                        } else {
                            int oldSelectedViewNumber = field.getSelectedViewNumber();
                            field.removeSelection();
                            recyclerView.getAdapter().notifyItemChanged(oldSelectedViewNumber);
                        }
                    }
                });

        String gameLanguage = getSharedPreferences(SHARED_SETTINGS, MODE_PRIVATE).getString(GAME_LANGUAGE, DEFAULT_GAME_LANGUAGE);
        editTextFieldItem.addTextChangedListener(new SingleLetterTextWatcher(gameLanguage));

        game.setPlayer(playerId, player);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ENTERED_ITEM, field.getEnteredViewNumber());
        outState.putChar(ENTERED_LETTER, field.getEnteredLetter());
        outState.putIntArray(ACTIVE_VIEW_NUMBERS, field.getActiveViewNumbers());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((FieldRecyclerAdapter)recyclerView.getAdapter()).setContext(null);
        player.setActivity(null);
    }

    @Override
    public void makeMove() {
        ((FieldRecyclerAdapter)recyclerView.getAdapter()).setOnItemClickListener(GameActivity.this);
    }

    @Override
    public void finishGame(boolean isWinner) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.congratulations)
                .setMessage(getString(R.string.filled_whole_field, player.getScore()))
                .setPositiveButton(R.string.back_to_menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.start_new_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(GameSettingsActivity.createIntent(GameActivity.this));
                        finish();
                    }
                })
                .create().show();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        if(actionMode == null) {
            int oldSelectedViewNumber = field.getSelectedViewNumber();
            boolean isSelected = field.setSelectedViewNumber(position);
            recyclerView.getAdapter().notifyItemChanged(oldSelectedViewNumber);
            if (isSelected) {
                recyclerView.getAdapter().notifyItemChanged(position);
                editTextFieldItem.requestFocus();
                imm.showSoftInput(editTextFieldItem, InputMethodManager.SHOW_FORCED);
            } else {
                editTextFieldItem.requestFocus();
                imm.hideSoftInputFromWindow(editTextFieldItem.getWindowToken(), 0);
            }
        } else {
            if(field.toggleActiveModeForView(position)){
                actionMode.setTitle(field.getEnteredLetterSequence().toUpperCase());
            } else {
                actionMode.finish();
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onItemLongClick(View itemView, int position) {
        editTextFieldItem.requestFocus();
        imm.hideSoftInputFromWindow(editTextFieldItem.getWindowToken(), 0);
        field.removeSelection();
        if (!field.isEnteredLetter()) {
            showSnackbar(R.string.enter_new_letter);
            return;
        }
        if(field.toggleActiveModeForView(position)){
            if (actionMode == null){
                actionMode = startSupportActionMode(new ActionModeCallback());
            }
            actionMode.setTitle(field.getEnteredLetterSequence().toUpperCase());
        } else {
            if (actionMode != null){
                actionMode.finish();
            }
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_game_message)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.back_to_the_game, null)
                .create().show();
    }

    private void showSnackbar(@StringRes int resId, Object... values) {
        Snackbar.make(viewContent, getString(resId, values), Snackbar.LENGTH_LONG)
                .show();
    }


    class SingleLetterTextWatcher implements TextWatcher {
        private String gameLanguage;

        SingleLetterTextWatcher(String gameLanguage) {
            this.gameLanguage = gameLanguage;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        @Override
        public void afterTextChanged(Editable editable) {
            int iLen = editable.length();
            if (iLen > 0 && !isCorrectChar(editable.charAt(iLen - 1))){
                editable.delete(iLen-1, iLen);
                Toast.makeText(GameActivity.this, "It is not correct symbol", Toast.LENGTH_SHORT).show();
                return;
            }
            if (iLen > 1){
                editable.delete(0, 1);
            }
            imm.restartInput(editTextFieldItem);

            int selectedViewNumber = field.getSelectedViewNumber();
            int oldEnteredViewNumber = field.getEnteredViewNumber();
            if (iLen != 0){
                field.setSelectedItem(editable.charAt(0));
            } else {
                field.setSelectedItem(Field.EMPTY_VALUE);
            }
            recyclerView.getAdapter().notifyItemChanged(selectedViewNumber);
            if (selectedViewNumber != oldEnteredViewNumber) {
                recyclerView.getAdapter().notifyItemChanged(oldEnteredViewNumber);
            }
        }
        private boolean isCorrectChar(char c) {
            switch (gameLanguage) {
                case "ru": return ((c >= 0x0430 && c<= 0x044F) || c == 0x0451);
                default:   return false;
            }
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_confirm) {
                if(field.isEnteredLetterInsideActiveWord()){
                    int[] activeViews = field.getActiveViewNumbers();
                    int enteredLetterCell = field.getEnteredViewNumber();
                    char enteredLetter = field.getEnteredLetter();
                    int oldScore = player.getScore();
                    ((FieldRecyclerAdapter)recyclerView.getAdapter()).setOnItemClickListener(null);
                    int res = player.finishMove(enteredLetterCell, enteredLetter, activeViews);
                    if(res == 0){
                        View viewAnimFrom = recyclerView.getChildAt(field.getEnteredViewNumber());
                        if (viewAnimFrom != null) {
                            int[] location = new int[2];
                            viewAnimFrom.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];

                            textViewScoreAnim.setText(getString(R.string.plus_points, player.getScore() - oldScore));
                            textViewScoreAnim.setVisibility(View.VISIBLE);
                            textViewScoreAnim.setX(x);
                            textViewScoreAnim.setY(y - 250);
                            Animation animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.score);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {  }
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    textViewScoreAnim.setVisibility(View.GONE);
                                }
                                @Override
                                public void onAnimationRepeat(Animation animation) {  }
                            });
                            textViewScoreAnim.startAnimation(animation);
                        }

                        field.resetState(player.getActualField());
                        recyclerView.getAdapter().notifyDataSetChanged();
                        textViewScore.setText(getString(R.string.score, player.getScore()));
                        mode.finish();
                    } else {
                        ((FieldRecyclerAdapter) recyclerView.getAdapter()).setOnItemClickListener(GameActivity.this);
                        if (res == -1) {
                            showSnackbar(R.string.no_such_word);
                        }
                        if (res == -2) {
                            showSnackbar(R.string.such_word_have_already_been_used);
                        }
                    }
                } else {
                    showSnackbar(R.string.must_contain_new_letter);
                }
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            field.finishActiveMode();
            recyclerView.getAdapter().notifyDataSetChanged();
            actionMode = null;
        }
    }
}
