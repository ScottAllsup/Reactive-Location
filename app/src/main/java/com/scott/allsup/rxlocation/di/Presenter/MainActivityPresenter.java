package com.scott.allsup.rxlocation.di.Presenter;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.scott.allsup.rxlocation.di.Contract.MainContract;
import com.scott.allsup.rxlocation.di.Module.LocationApi;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivityPresenter implements MainContract.Presenter {

    private final MainContract.View mView;

    @Inject
    LocationApi mLocationManager;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainActivityPresenter(MainContract.View view, LocationApi locationManager) {
        this.mView = view;
        this.mLocationManager = locationManager;
    }

    void isObservingLocation() {
        mView.ObservingLocation(compositeDisposable.size() > 0);
    }

    @Override
    public void RequestLocation() {
        mLocationManager.singleLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> mView.ShowError(throwable.getMessage()))
                .doOnSuccess(location -> {
                    mView.UpdateLongitude(location.getLongitude());
                    mView.UpdateLatitude(location.getLatitude());
                    mView.UpdateAltitude(location.getAltitude());
                    mView.UpdateBearing(location.getBearing());
                    mView.UpdateAddress(location.getLongitude(), location.getLatitude());
                })
                .subscribe();
    }

    @Override
    public void ObserveLocation() {
        if(compositeDisposable.isDisposed()){
            compositeDisposable = new CompositeDisposable();
        }
        mLocationManager.observeLocationChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> mView.ShowError(throwable.getMessage()))
                .doOnEach(new Observer<Location>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Location location) {
                        isObservingLocation();
                        mView.UpdateLongitude(location.getLongitude());
                        mView.UpdateLatitude(location.getLatitude());
                        mView.UpdateAltitude(location.getAltitude());
                        mView.UpdateBearing(location.getBearing());
                        mView.UpdateAddress(location.getLongitude(), location.getLatitude());

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                })
                .subscribe();
    }

    @Override
    public void StopObservingLocation() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        isObservingLocation();
    }

    @Override
    public void SearchLocation(Geocoder geocoder, CharSequence text) {

        mView.UpdateAddress(0,0);

        try {
            List<Address> address = geocoder.getFromLocationName(text.toString(), 1);
            if (address.size() < 1) {
                mView.ShowError("No Address Found");
                return;
            }

            mView.UpdateAddress(address.get(0));

        } catch (IOException exception) {
            mView.ShowError("No Result Found");
        }
    }
}
