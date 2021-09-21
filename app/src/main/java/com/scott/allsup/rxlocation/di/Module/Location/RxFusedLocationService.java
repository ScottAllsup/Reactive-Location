package com.scott.allsup.rxlocation.di.Module.Location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.functions.Cancellable;

public class RxFusedLocationService implements LocationService {

    private final FusedLocationProviderClient fusedLocationProviderClient;

    public RxFusedLocationService(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    @Override
    public Observable<Location> requestLocationUpdates() {
        return getLocationObservable();
    }

    @SuppressLint("MissingPermission")
    private Observable<Location> getLocationObservable() {
        return Observable.create(emitter -> {
            LocationCallback listener = getLocationListener(emitter);
            OnCompleteListener<Void> completeListener = getOnCompleteListener(emitter);
            try {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (!emitter.isDisposed() && location != null) {
                        emitter.onNext(location);
                    }
                }).addOnFailureListener(e -> {
                });
                Task<Void> task = fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), listener, Looper.getMainLooper());
                task.addOnCompleteListener(completeListener);
            } catch (Exception e) {
                emitter.tryOnError(e);
            }
            emitter.setCancellable(getCancellable(listener));
        });
    }

    private OnCompleteListener<Void> getOnCompleteListener(ObservableEmitter<Location> emitter) {
        return task -> {
            if (!task.isSuccessful() && task.getException() != null) {
                emitter.tryOnError(task.getException());
            }
        };
    }

    private LocationCallback getLocationListener(ObservableEmitter<Location> emitter) {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (!emitter.isDisposed()) {
                    emitter.onNext(locationResult.getLastLocation());
                }
            }
        };
    }

    private Cancellable getCancellable(LocationCallback locationListener) {
        return () -> fusedLocationProviderClient.removeLocationUpdates(locationListener);
    }

    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(TimeUnit.SECONDS.toMillis(30))
                .setInterval(TimeUnit.SECONDS.toMillis(30));
    }
}
