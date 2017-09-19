package ru.romanbrazhnikov.simplebookkeeping.dagger;

import javax.inject.Singleton;

import dagger.Component;
import ru.romanbrazhnikov.simplebookkeeping.views.MainActivity;
import ru.romanbrazhnikov.simplebookkeeping.views.MoneyFlowEditorActivity;

/**
 * Created by roman on 19.09.17.
 */

@Singleton
@Component(
        modules = {
                AppModule.class,
                ObjectBoxModule.class
        })
public interface SimpleBookkeepingComponent {
    void inject(MainActivity activity);
    void inject(MoneyFlowEditorActivity activity);
}
