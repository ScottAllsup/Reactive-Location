package com.scott.allsup.rxlocation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scott.allsup.rxlocation.di.ApplicationComponent;
import com.scott.allsup.rxlocation.di.BaseApp;

public class BaseActivity extends AppCompatActivity {

    ApplicationComponent applicationComponent;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent = BaseApp.getApplicationComponent();
    }
}
