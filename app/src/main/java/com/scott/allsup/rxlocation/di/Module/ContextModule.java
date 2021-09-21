package com.scott.allsup.rxlocation.di.Module;

import android.content.Context;

import com.scott.allsup.rxlocation.di.Qualifier.ApplicationContext;
import com.scott.allsup.rxlocation.di.Scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context mContext;

    public ContextModule(Context context){
        this.mContext = context;
    }

    @Provides
    @ApplicationScope
    @ApplicationContext
    public Context providesContext(){
        return mContext;
    }
}
