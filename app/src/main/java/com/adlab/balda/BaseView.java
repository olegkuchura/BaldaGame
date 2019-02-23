package com.adlab.balda;

import androidx.annotation.NonNull;

public interface BaseView<T> {

    void setPresenter(@NonNull T presenter);

}
