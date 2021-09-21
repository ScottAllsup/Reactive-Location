package com.scott.allsup.rxlocation.di.Mvp;

import com.scott.allsup.rxlocation.di.Contract.MainContract;
import com.scott.allsup.rxlocation.di.Module.LocationApi;
import com.scott.allsup.rxlocation.di.Presenter.MainActivityPresenter;
import com.scott.allsup.rxlocation.di.Scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityMvpModule {


    private final MainContract.View mView;

    public MainActivityMvpModule(MainContract.View mView){
        this.mView = mView;
    }

    @Provides
    @ActivityScope
    MainContract.View provideView(){
        return mView;
    }

    @Provides
    @ActivityScope
    MainActivityPresenter providePresenter(MainContract.View mView, LocationApi mLocationApi){
        return new MainActivityPresenter(mView, mLocationApi);
    }
}
