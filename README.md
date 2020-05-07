# RxLocation Android Library

Simple reactive extension, that adds support to Dagger2 based projects and provides location updates.

This library is based on RxJava2.
Core
Description:


# Dependencies

implementation 'com.TDI.RxLocation:x.x.x'

Usage

//TODO and write Working sample is provided in app module.

# Initialization

Create RxLocation typically in ApplicationComponent (in our case, ApplicationComponent.java):

```
@ApplicationScope
@Component(modules = LocationModule.class)
public interface ApplicationComponent {

    LocationApi getLocationApi();

    @ApplicationContext
    Context getContext();

    void injectApplication(BaseApp application);
}
```

# Example Usage

```
 getLocationApi().observeLocationChanges()
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Location>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(Location location) {
                        setLatitude(location.getLatitude());
                        setLongitude(location.getLongitude());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        Log.d(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
```

