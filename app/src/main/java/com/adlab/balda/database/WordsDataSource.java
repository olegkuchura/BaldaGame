package com.adlab.balda.database;


import androidx.annotation.NonNull;

public interface WordsDataSource {

    interface CheckWordCallback {

        void onWordChecked(boolean isWordExist);

    }

    interface GetWordCallback {

        void onWordLoaded(@NonNull String word);

        void onDataNotAvailable();
    }

    void isWordExist(@NonNull String word, @NonNull CheckWordCallback callback);

    void getRandomWord(int length, @NonNull GetWordCallback callback);

}
