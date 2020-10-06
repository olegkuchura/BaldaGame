package com.adlab.balda.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 09.08.2018.
 */

public class BaldaDataBase extends SQLiteOpenHelper {
    private static final String FILE_PATH = "words_rus.sql";
    private static final String DB_NAME = "words_rus";
    private static final int VERSION = 1;

    private static final String TABLE_WORDS = "words";

    private static final String COLUMN_AUTO_ID = "AUTO_ID";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_WORD_LENGTH = "word_length";

    private Context mContext;

    public BaldaDataBase(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
    }

    public boolean isWordExist(String word) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORDS, new String[]{COLUMN_AUTO_ID, COLUMN_WORD}, "word = ?", new String[]{word},
                null, null, null);
        Log.i(BaldaDataBase.class.getSimpleName(), "count of rows: " + cursor.getCount());
        int count = cursor.getCount();
        cursor.close();
        return count != 0;
    }

    /*public String getRandomWord(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORDS, new String[]{COLUMN_WORD}, null, null,
                null, null, "RANDOM()", "" + 1);
        cursor.moveToFirst();
        String word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD));
        cursor.close();
        return word;
    }*/

    public String getRandomWord(int length) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORDS, new String[]{COLUMN_WORD}, COLUMN_WORD_LENGTH + " = ?", new String[]{"" + length},
                null, null, "RANDOM()", "" + 1);
        cursor.moveToFirst();
        String word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD));
        cursor.close();
        return word;
    }

    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORDS, new String[]{COLUMN_WORD}, null, null,
                null, null, null);

        cursor.moveToFirst();
        do {
            String word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD));
            result.add(word);
        }while (cursor.moveToNext());

        cursor.close();
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE [words] (\n" +
                "  [AUTO_ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "  [ID] INTEGER NOT NULL, \n" +
                "  [word] VARCHAR NOT NULL, \n" +
                "  [word_length] INTEGER, \n" +
                "  [code] INTEGER, \n" +
                "  [code_parent] INTEGER, \n" +
                "  [gender] VARCHAR(3), \n" +
                "  [wcase] VARCHAR, \n" +
                "  [soul] INTEGER);");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        mContext.getAssets().open(FILE_PATH)))){
            String insertStatement =
                    "INSERT INTO `words` (`ID`, `word`, `code`, `code_parent`, `gender`, `wcase`, `soul`) VALUES ";
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(line.length() - 1) != ';')
                    continue;
                db.execSQL(insertStatement + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.execSQL("UPDATE words \n" +
                "SET word_length = length(word);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

}
