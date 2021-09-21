package com.scott.allsup.rxlocation.di.Context;

import android.content.Context;

import com.scott.allsup.rxlocation.MainActivity;
import com.scott.allsup.rxlocation.di.Qualifier.ActivityContext;
import com.scott.allsup.rxlocation.di.Scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityContextModule {

    private final MainActivity mainActivity;
    private final Context context;

    public MainActivityContextModule(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.context = mainActivity;
    }

    @Provides
    @ActivityScope
    public MainActivity providesMainActivity(){
        return mainActivity;
    }

    @Provides
    @ActivityScope
    @ActivityContext
    public Context providesContext(){
        return context;
    }

}
