package com.scott.allsup.rxlocation.di.Contract;


import android.location.Address;
import android.location.Geocoder;

public interface MainContract {

    interface View {
        void ShowError(String errorMessage);
        void UpdateBearing(Float bearing);
        void UpdateAltitude(Double altitude);
        void UpdateLongitude(Double longitude);
        void UpdateLatitude(Double latitude);
        void UpdateAddress(double longitude, double latitude);
        void UpdateAddress(Address address);
        void ObservingLocation(boolean bool);
    }

    interface Presenter{
        void RequestLocation();
        void ObserveLocation();
        void StopObservingLocation();
        void SearchLocation(Geocoder geocoder, CharSequence text);
    }
}
