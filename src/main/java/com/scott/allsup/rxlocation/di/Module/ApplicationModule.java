package com.scott.allsup.rxlocation.di.Module;

import android.app.Application;

import com.scott.allsup.rxlocation.di.Scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @ApplicationScope
    Application provideApplication(){
        return mApplication;
    }
}
