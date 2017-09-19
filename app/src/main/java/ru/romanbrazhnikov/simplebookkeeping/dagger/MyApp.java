package ru.romanbrazhnikov.simplebookkeeping.dagger;

import android.app.Application;

/**
 * Created by roman on 19.09.17.
 */

public class MyApp extends Application {
    private SimpleBookkeepingComponent mBookkeepingComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mBookkeepingComponent = DaggerSimpleBookkeepingComponent.builder()
                .appModule(new AppModule(this))
                .objectBoxModule(new ObjectBoxModule(this))
                .build();
    }

    public SimpleBookkeepingComponent getBookkeepingComponent() {
        return mBookkeepingComponent;
    }
}
