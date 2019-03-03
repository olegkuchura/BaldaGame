package com.adlab.balda.utils;

import android.content.Context;

import com.adlab.balda.database.BaldaDataBase;
import com.adlab.balda.database.WordsRepository;

import androidx.annotation.NonNull;

public class Injection {

    private static BaldaDataBase mDatabase;

    public static synchronized WordsRepository provideWordsRepository(@NonNull Context context) {
        if (mDatabase == null) {
            mDatabase = new BaldaDataBase(context.getApplicationContext());
        }
        return WordsRepository.getInstance(new AppExecutors(), mDatabase);
    }

}
