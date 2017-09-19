package ru.romanbrazhnikov.simplebookkeeping.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
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
    private Box<MoneyFlowRecord> mMoneyFlowBox;
    private RecyclerView rvRecords;
    private MoneyFlowAdapter mRecordAdapter;

    //WIDGETS
    private Button bNewFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: to a base activity
        ((MyApp) getApplication()).getBookkeepingComponent().inject(this);

        initWidgets();

        mMoneyFlowBox = mBoxStore.boxFor(MoneyFlowRecord.class);
    }

    private void initWidgets() {
        // Recyclerview
        rvRecords = findViewById(R.id.rv_records);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        // Buttons
        bNewFlow = findViewById(R.id.b_new_flow);
        bNewFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoneyFlowEditorActivity.showActivity(MainActivity.this, null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        // getting notes and setting adapter
        List<MoneyFlowRecord> notes = mMoneyFlowBox.getAll();
        if (mRecordAdapter == null) {
            mRecordAdapter = new MoneyFlowAdapter(notes);
            rvRecords.setAdapter(mRecordAdapter);
        } else {
            mRecordAdapter.updateData(notes);
            mRecordAdapter.notifyDataSetChanged();
        }

    }

    class MoneyFlowViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView tvValue;
        private TextView tvDescription;

        public MoneyFlowViewHolder(View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        public void bindRecord(MoneyFlowRecord record) {
            tvValue.setText(record.getValueAsString());
            tvDescription.setText(record.getDescription());
        }

        @Override
        public void onClick(View view) {

        }
    }

    class MoneyFlowAdapter extends RecyclerView.Adapter<MoneyFlowViewHolder> {
        private List<MoneyFlowRecord> mRecords = new ArrayList<>();

        public MoneyFlowAdapter(List<MoneyFlowRecord> records) {
            mRecords = records;
        }

        @Override
        public MoneyFlowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = getLayoutInflater();
            View view = li.inflate(R.layout.item_record, parent, false);
            return new MoneyFlowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MoneyFlowViewHolder holder, int position) {
            MoneyFlowRecord record = mRecords.get(position);
            holder.bindRecord(record);
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        public void updateData(List<MoneyFlowRecord> newList) {
            mRecords.clear();
            mRecords.addAll(newList);
            this.notifyDataSetChanged();
        }
    }
}
