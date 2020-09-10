package com.adlab.balda.utils;

import android.content.Context;

import com.adlab.balda.App;
import com.adlab.balda.database.BaldaDataBase;
import com.adlab.balda.database.WordsRepository;

import androidx.annotation.NonNull;

public class Injection {

    private static BaldaDataBase mDatabase;

    public static synchronized WordsRepository provideWordsRepository() {
        if (mDatabase == null) {
            mDatabase = new BaldaDataBase(App.appContext);
        }
        return WordsRepository.getInstance(new AppExecutors(), mDatabase);
    }

}
