package com.example.appfichaje.ui.admin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AdminViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public AdminViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminViewModel.class)) {
            return (T) new AdminViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
