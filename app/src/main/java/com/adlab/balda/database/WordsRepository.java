package com.adlab.balda.database;

import com.adlab.balda.utils.AppExecutors;

import androidx.annotation.NonNull;

public class WordsRepository implements WordsDataSource {

    private static WordsRepository INSTANCE;

    private BaldaDataBase mDatabase;

    private AppExecutors mAppExecutors;

    private WordsRepository(@NonNull AppExecutors appExecutors, @NonNull BaldaDataBase database) {
        mAppExecutors = appExecutors;
        mDatabase = database;
    }

    public static WordsRepository getInstance(@NonNull AppExecutors appExecutors, @NonNull BaldaDataBase database) {
        synchronized (WordsRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new WordsRepository(appExecutors, database);
            }
        }
        return INSTANCE;
    }

    @Override
    public void isWordExist(@NonNull final String word, @NonNull final CheckWordCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final boolean isWordExist = mDatabase.isWordExist(word);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onWordChecked(isWordExist);
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getRandomWord(final int length, @NonNull final GetWordCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String word = mDatabase.getRandomWord(length);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (word != null) {
                            callback.onWordLoaded(word);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }
}
