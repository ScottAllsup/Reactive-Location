package com.scott.allsup.rxlocation.di;

import android.content.Context;

import com.scott.allsup.rxlocation.di.Module.ApplicationModule;
import com.scott.allsup.rxlocation.di.Module.ContextModule;
import com.scott.allsup.rxlocation.di.Module.LocationApi;
import com.scott.allsup.rxlocation.di.Module.LocationModule;
import com.scott.allsup.rxlocation.di.Qualifier.ApplicationContext;
import com.scott.allsup.rxlocation.di.Scope.ApplicationScope;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@ApplicationScope
@Component(modules = { ApplicationModule.class, ContextModule.class, LocationModule.class})
public interface ApplicationComponent {

    LocationApi getLocationApi();

    @ApplicationContext
    Context getContext();

    void injectApplication(BaseApp application);
}
