package com.adlab.balda.utils;

import com.adlab.balda.App;
import com.adlab.balda.database.AppDatabase;
import com.adlab.balda.database.AppRepository;
import com.adlab.balda.database.AppRepositoryImpl;

public class Injection {

    public static synchronized AppRepository provideAppRepository() {
        return new AppRepositoryImpl(AppDatabase.getInstance(App.appContext));
    }

}
