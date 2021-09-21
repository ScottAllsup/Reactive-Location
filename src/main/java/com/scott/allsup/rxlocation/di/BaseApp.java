package com.scott.allsup.rxlocation.di;

import android.app.Activity;
import android.app.Application;

import com.scott.allsup.rxlocation.di.Module.ApplicationModule;
import com.scott.allsup.rxlocation.di.Module.ContextModule;
import com.scott.allsup.rxlocation.di.Module.LocationModule;

import dagger.Module;

@Module
public class BaseApp extends Application {

    private static ApplicationComponent applicationComponent;

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        dependencyInjection();
    }

    void dependencyInjection() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .contextModule(new ContextModule(this))
                .locationModule(new LocationModule(this))
                .build();
        applicationComponent.injectApplication(this);
    }

    public static BaseApp get(Activity activity) {
        return (BaseApp) activity.getApplication();
    }
}
