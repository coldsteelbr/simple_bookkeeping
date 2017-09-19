package ru.romanbrazhnikov.simplebookkeeping.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.objectbox.BoxStore;
import ru.romanbrazhnikov.simplebookkeeping.entities.MyObjectBox;

/**
 * Created by roman on 19.09.17.
 */

@Module
public class ObjectBoxModule {
    private BoxStore mStore;

    public ObjectBoxModule(MyApp application) {
        mStore = MyObjectBox.builder().androidContext(application).build();
    }

    @Provides
    @Singleton
    BoxStore provideBoxStore() {
        return mStore;
    }
}
