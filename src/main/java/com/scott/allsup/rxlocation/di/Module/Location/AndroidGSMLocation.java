package com.scott.allsup.rxlocation.di.Module.Location;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class AndroidGSMLocation extends BroadcastReceiver implements LocationService {

    private Integer priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private final LocationManager locationManager;

    public AndroidGSMLocation(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @Override
    public Observable<Location> requestLocationUpdates() {
        return getLocationObservable();
    }

    private Observable<Location> getLocationObservable() {
        return Observable.create(emitter -> {
            BroadcastLocationListener rxLocationListener = new BroadcastLocationListener(emitter);
            List<String> providers = locationManager.getAllProviders();
            try {
                switch (priority) {
                    case LocationRequest.PRIORITY_HIGH_ACCURACY:
                        fetchLastKnowLocation(Arrays.asList(LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER), emitter);
                        requestLocationUpdates(LocationManager.GPS_PROVIDER, providers, rxLocationListener);
                        break;
                    case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                        fetchLastKnowLocation(Collections.singletonList(LocationManager.NETWORK_PROVIDER), emitter);
                        requestLocationUpdates(LocationManager.GPS_PROVIDER, providers, rxLocationListener);
                        requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, providers, rxLocationListener);
                        break;
                    case LocationRequest.PRIORITY_LOW_POWER:
                        fetchLastKnowLocation(Collections.singletonList(LocationManager.NETWORK_PROVIDER), emitter);
                        requestLocationUpdates(LocationManager.NETWORK_PROVIDER, providers, rxLocationListener);
                        break;
                    case LocationRequest.PRIORITY_NO_POWER:
                        fetchLastKnowLocation(Collections.singletonList(LocationManager.PASSIVE_PROVIDER), emitter);
                        requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, providers, rxLocationListener);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid Priority passed: " + priority);
                }
            }catch (Exception e){
                emitter.tryOnError(e);
            }
            emitter.setCancellable(() -> locationManager.removeUpdates(rxLocationListener));
        });
    }


    @SuppressLint("MissingPermission")
    private void requestLocationUpdates(String provider, List<String> providers, BroadcastLocationListener rxLocationListener) {
        if (providers.contains(provider)) {
            locationManager.requestLocationUpdates(
                    provider,
                    TimeUnit.SECONDS.toMillis(30),
                    500,
                    rxLocationListener,
                    Looper.myLooper());
        }
    }

    private void fetchLastKnowLocation(List<String> providers, ObservableEmitter<Location> emitter) {
        for (String string : providers) {
            @SuppressLint("MissingPermission") Location lastLocation = locationManager.getLastKnownLocation(string);
            if (!emitter.isDisposed() && lastLocation != null) {
                emitter.onNext(lastLocation);
            }
        }
    }

    public static class BroadcastLocationListener implements LocationListener {

        private final ObservableEmitter<Location> emitter;

        BroadcastLocationListener(ObservableEmitter<Location> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (!emitter.isDisposed()) {
                emitter.onNext(location);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action;
            if (intent == null) {
                return;
            }
            action = intent.getAction() != null ? intent.getAction() : "";

            switch (action) {
                case Intent.ACTION_BATTERY_LOW:
                    priority = LocationRequest.PRIORITY_LOW_POWER;
                    break;

                case Intent.ACTION_BATTERY_OKAY:
                    //Break missing to push to PRIORITY_BALANCED_POWER_ACCURACY
                case Intent.ACTION_POWER_DISCONNECTED:
                    //Break missing to push to PRIORITY_BALANCED_POWER_ACCURACY
                case Intent.EXTRA_DOCK_STATE:
                    priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                    break;

                case Intent.ACTION_POWER_CONNECTED:
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
