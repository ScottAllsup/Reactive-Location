package com.scott.allsup.rxlocation.di.Module;

import android.location.Location;

import com.scott.allsup.rxlocation.di.Module.Location.AndroidGSMLocation;
import com.scott.allsup.rxlocation.di.Module.Location.LocationService;
import com.scott.allsup.rxlocation.di.Module.Location.RxFusedLocationService;
import com.scott.allsup.rxlocation.di.Qualifier.ApplicationContext;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocationApi {

    @ApplicationContext
    private final LocationService fusedLocationProviderClient;
    private final LocationService androidLocation;
    private final Observable<Location> sharedLocationObservable;

    @Inject
    public LocationApi(RxFusedLocationService fusedLocationProviderClient, AndroidGSMLocation androidLocation) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.androidLocation = androidLocation;
        sharedLocationObservable = createLocationObservable();
    }

    public Single<Location> singleLocation() {
        return sharedLocationObservable.firstOrError().timeout(TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS);
    }

    public Observable<Location> observeLocationChanges() {
        return sharedLocationObservable;
    }

    private Observable<Location> createLocationObservable() {
        return fusedLocationProviderClient.requestLocationUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(throwable -> androidLocation.requestLocationUpdates())
                .replay(1)
                .refCount();
    }
}
