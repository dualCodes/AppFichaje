package com.example.appfichaje.ui.incidencias;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class IncidenciasViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public IncidenciasViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(IncidenciasViewModel.class)) {
            return (T) new IncidenciasViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
