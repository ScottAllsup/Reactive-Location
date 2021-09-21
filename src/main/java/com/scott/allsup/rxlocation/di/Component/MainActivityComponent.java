package com.scott.allsup.rxlocation.di.Component;


import android.content.Context;

import com.scott.allsup.rxlocation.MainActivity;
import com.scott.allsup.rxlocation.di.ApplicationComponent;
import com.scott.allsup.rxlocation.di.Mvp.MainActivityMvpModule;
import com.scott.allsup.rxlocation.di.Context.MainActivityContextModule;
import com.scott.allsup.rxlocation.di.Qualifier.ActivityContext;
import com.scott.allsup.rxlocation.di.Scope.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(modules = { MainActivityContextModule.class, MainActivityMvpModule.class}, dependencies = ApplicationComponent.class)
public interface MainActivityComponent {

    @ActivityContext
    Context getContext();

    void injectLoginActivity(MainActivity mainActivity);

}
