package com.scott.allsup.rxlocation.di.Module;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.scott.allsup.rxlocation.di.Module.Location.AndroidGSMLocation;
import com.scott.allsup.rxlocation.di.Module.Location.RxFusedLocationService;
import com.scott.allsup.rxlocation.di.Qualifier.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @ApplicationContext
    private final Context mContext;

    public LocationModule(@ApplicationContext Context context) {
        mContext = context;
    }

    @Provides
    FusedLocationProviderClient provideLocation(){
        return LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Provides
    RxFusedLocationService getLocationService(FusedLocationProviderClient fusedLocationProviderClient){
        return new RxFusedLocationService(fusedLocationProviderClient);
    }

    @Provides
    LocationManager provideLocationManager(){
        return getSystemService(mContext, LocationManager.class);
    }

    @Provides
    AndroidGSMLocation getAndroidLocationService(){
        return new AndroidGSMLocation (provideLocationManager());
    }
}
