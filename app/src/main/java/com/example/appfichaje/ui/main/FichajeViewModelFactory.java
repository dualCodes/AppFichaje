package com.example.appfichaje.ui.main;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FichajeViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public FichajeViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FichajeViewModel.class)) {
            return (T) new FichajeViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
