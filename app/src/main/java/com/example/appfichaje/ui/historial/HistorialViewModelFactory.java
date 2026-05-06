package com.example.appfichaje.ui.historial;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HistorialViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public HistorialViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HistorialViewModel.class)) {
            return (T) new HistorialViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
