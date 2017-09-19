package ru.romanbrazhnikov.simplebookkeeping.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import ru.romanbrazhnikov.simplebookkeeping.R;
import ru.romanbrazhnikov.simplebookkeeping.dagger.MyApp;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;

public class MainActivity extends AppCompatActivity {

    @Inject
    BoxStore mBoxStore;
    private RecyclerView rvRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MyApp)getApplication()).getBookkeepingComponent().inject(this);

        rvRecords = findViewById(R.id.rv_records);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));


        Box<MoneyFlowRecord> MFRBox = mBoxStore.boxFor(MoneyFlowRecord.class);

        MoneyFlowRecord inRecord = new MoneyFlowRecord(new BigDecimal("333"), "Test income");
        MFRBox.put(inRecord);

        MoneyFlowRecord outRecord = new MoneyFlowRecord(new BigDecimal("-432"), "Test consumption");
        MFRBox.put(outRecord);


        List<MoneyFlowRecord> allRecords = MFRBox.getAll();
        for (MoneyFlowRecord currentRecord :
                allRecords) {
            Log.d("MAIN", currentRecord.getValue() + " " + currentRecord.getDescription());
        }
    }
}
