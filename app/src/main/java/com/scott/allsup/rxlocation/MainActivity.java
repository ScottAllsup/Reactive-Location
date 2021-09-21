package com.scott.allsup.rxlocation;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.scott.allsup.rxlocation.databinding.ActivityMainBinding;
import com.scott.allsup.rxlocation.di.Component.DaggerMainActivityComponent;
import com.scott.allsup.rxlocation.di.Component.MainActivityComponent;
import com.scott.allsup.rxlocation.di.Context.MainActivityContextModule;
import com.scott.allsup.rxlocation.di.Contract.MainContract;
import com.scott.allsup.rxlocation.di.Mvp.MainActivityMvpModule;
import com.scott.allsup.rxlocation.di.Presenter.MainActivityPresenter;
import com.scott.allsup.rxlocation.di.Qualifier.ActivityContext;
import com.scott.allsup.rxlocation.di.Qualifier.ApplicationContext;
import com.vanniktech.rxpermission.RealRxPermission;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends BaseActivity implements MainContract.View {


    private ActivityMainBinding binding;

    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    Geocoder geocoder;

    @Inject
    @ApplicationContext
    Context context;

    @Inject
    @ActivityContext
    Context activityContext;

    @Inject
    MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityComponent component = DaggerMainActivityComponent.builder()
                .mainActivityContextModule(new MainActivityContextModule(this))
                .mainActivityMvpModule(new MainActivityMvpModule(this))
                .applicationComponent(applicationComponent)
                .build();

        component.injectLoginActivity(this);

        binding.tlbApp.setTitle("Reactive Location Example");

        geocoder = new Geocoder(this);

        binding.btnReqLocation.setOnClickListener(v -> {
            mainActivityPresenter.StopObservingLocation();
            compositeDisposable.add(
                    RealRxPermission.getInstance(this)
                            .request(Manifest.permission.ACCESS_FINE_LOCATION)
                            .doOnSuccess(permission -> mainActivityPresenter.RequestLocation())
                            .doOnError(throwable -> Toast.makeText(activityContext, "Error Requesting Location Permission", Toast.LENGTH_LONG).show())
                            .subscribe()
            );
        });

        binding.btnObserveLocation.setOnClickListener(v -> compositeDisposable.add(
                RealRxPermission.getInstance(this)
                        .request(Manifest.permission.ACCESS_FINE_LOCATION)
                        .doOnSuccess(permission -> mainActivityPresenter.ObserveLocation())
                        .doOnError(throwable -> Toast.makeText(activityContext, "Error Requesting Location Permission", Toast.LENGTH_LONG).show())
                        .subscribe()
        ));

        binding.btnSearch.setOnClickListener(v -> {
            mainActivityPresenter.StopObservingLocation();
            mainActivityPresenter.SearchLocation(geocoder, binding.btnSearch.getText());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityPresenter.StopObservingLocation();
    }

    @Override
    public void ShowError(String errorMessage) {
        Toast.makeText(activityContext, errorMessage, Toast.LENGTH_LONG).show();

        binding.tvAddLine1.setText("");
        binding.tvAddLine2.setText("");
        binding.tvAddLine2.setText("");
        binding.tvAddLine3.setText("");
        binding.tvAddPostcode.setText("");
        binding.tvLatitude.setText("");
        binding.tvLongitude.setText("");
        binding.tvAltitude.setText("");
        binding.tvBearing.setText("");
    }

    @Override
    public void UpdateBearing(Float bearing) {
        binding.tvBearing.setText(
                bearing == 0.0F ? "No Bearing Returned" : String.format(Locale.getDefault(), "Bearing: %s", bearing)
        );
    }

    @Override
    public void UpdateAltitude(Double altitude) {
        binding.tvAltitude.setText(
                altitude == 0.0D ? "No Altitude Returned" : String.format(Locale.getDefault(), "Altitude: %s", altitude)
        );
    }

    @Override
    public void UpdateLongitude(Double longitude) {
        binding.tvLongitude.setText(String.format(Locale.getDefault(), "Longitude: %s", longitude));
    }

    @Override
    public void UpdateLatitude(Double latitude) {
        binding.tvLatitude.setText(String.format(Locale.getDefault(), "Latitude: %s", latitude));
    }

    @Override
    public void UpdateAddress(double longitude, double latitude) {

        try {
            List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
            if (address.size() < 1) {
                ShowError("No Address Found");
                return;
            }
            UpdateAddress(address.get(0));
        } catch (IOException exception) {
            ShowError("Error fetching address.");
        }
    }

    @Override
    public void UpdateAddress(Address address) {
            binding.tvAddLine1.setText(String.format(Locale.getDefault(), "%s %s",  address.getFeatureName(), address.getLocality()));
            binding.tvAddLine2.setText(address.getThoroughfare());
            binding.tvAddLine3.setText(address.getAdminArea());
            binding.tvAddPostcode.setText(address.getPostalCode());
    }

    @Override
    public void ObservingLocation(boolean bool){
        binding.tvObserved.setText(String.format(Locale.getDefault(), "Observing Location: %s", bool));
    }
}