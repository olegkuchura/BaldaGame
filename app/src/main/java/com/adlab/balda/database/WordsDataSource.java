package com.adlab.balda.database;


import androidx.annotation.NonNull;

import java.util.List;

public interface WordsDataSource {

    interface CheckWordCallback {
        void onWordChecked(boolean isWordExist);
    }

    interface GetWordCallback {
        void onWordLoaded(@NonNull String word);
        void onDataNotAvailable();
    }

    interface GetAllWordsCallback {
        void onWordsLoaded(@NonNull List<String> words);
    }

    void isWordExist(@NonNull String word, @NonNull CheckWordCallback callback);

    void getRandomWord(int length, @NonNull GetWordCallback callback);

    void getAllWords(@NonNull GetAllWordsCallback callback);

}
