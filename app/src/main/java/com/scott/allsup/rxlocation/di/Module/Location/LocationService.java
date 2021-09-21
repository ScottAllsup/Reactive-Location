package com.scott.allsup.rxlocation.di.Module.Location;

import android.location.Location;

import io.reactivex.rxjava3.core.Observable;

public interface LocationService {

    Observable<Location> requestLocationUpdates();
}
