package ru.romanbrazhnikov.simplebookkeeping.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.math.BigDecimal;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import ru.romanbrazhnikov.simplebookkeeping.R;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;
import ru.romanbrazhnikov.simplebookkeeping.entities.MyObjectBox;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRecords = findViewById(R.id.rv_records);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        BoxStore boxStore = MyObjectBox.builder().androidContext(this).build();
        Box<MoneyFlowRecord> MFRBox = boxStore.boxFor(MoneyFlowRecord.class);

        MoneyFlowRecord inRecord = new MoneyFlowRecord(new BigDecimal("777"), "Test income");
        MFRBox.put(inRecord);

        MoneyFlowRecord outRecord = new MoneyFlowRecord(new BigDecimal("-354"), "Test consumption");
        MFRBox.put(outRecord);


        List<MoneyFlowRecord> allRecords = MFRBox.getAll();
        for (MoneyFlowRecord currentRecord :
                allRecords) {
            Log.d("MAIN", currentRecord.getValue() + " " + currentRecord.getDescription());
        }
    }
}
