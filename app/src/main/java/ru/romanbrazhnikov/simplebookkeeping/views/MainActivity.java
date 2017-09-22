package ru.romanbrazhnikov.simplebookkeeping.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import ru.romanbrazhnikov.simplebookkeeping.R;
import ru.romanbrazhnikov.simplebookkeeping.dagger.MyApp;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord_;
import ru.romanbrazhnikov.simplebookkeeping.structs.DateRange;


public class MainActivity extends AppCompatActivity {

    @Inject
    BoxStore mBoxStore;
    private Box<MoneyFlowRecord> mMoneyFlowBox;
    private RecyclerView rvRecords;
    private MoneyFlowAdapter mRecordAdapter;

    //WIDGETS
    private Button bNewFlow;
    private TextView tvBalance;
    private RadioGroup rbgFilters;
    private RadioButton rbToday;
    private RadioButton rbMonth;
    private RadioButton rbAllTime;
    private DateRange mDateRange = new DateRange(DateRange.DateType.DAY);

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

        // Balance
        tvBalance = findViewById(R.id.tv_balance);

        // Filter
        rbgFilters = findViewById(R.id.rbg_filters);
        rbToday = findViewById(R.id.rb_today);
        rbMonth = findViewById(R.id.rb_month);
        rbAllTime = findViewById(R.id.rb_all_time);

        FilterButtonClicked filterClickListener = new FilterButtonClicked();
        rbToday.setOnClickListener(filterClickListener);
        rbMonth.setOnClickListener(filterClickListener);
        rbAllTime.setOnClickListener(filterClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        // LIST
        // getting records and setting adapter
        Query<MoneyFlowRecord> queryByDate
                = mMoneyFlowBox.query()
                .between(MoneyFlowRecord_.date, mDateRange.getFromDate(), mDateRange.getToDate()).build();

        List<MoneyFlowRecord> filteredRecords = queryByDate.find();
        if (mRecordAdapter == null) {
            mRecordAdapter = new MoneyFlowAdapter(filteredRecords);
            rvRecords.setAdapter(mRecordAdapter);
        } else {
            mRecordAdapter.updateData(filteredRecords);
            mRecordAdapter.notifyDataSetChanged();
        }

        // BALANCE
        BigDecimal balanceResult = BigDecimal.ZERO;

        // calculate the balance (sum)
        for (MoneyFlowRecord currentRecord :
                filteredRecords) {
            balanceResult = balanceResult.add(currentRecord.getValue());
        }
        tvBalance.setText(balanceResult.toString());
    }

    class MoneyFlowViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnLongClickListener {
        private long mId = 0;
        private TextView tvValue;
        private TextView tvDescription;
        private TextView tvDate;

        public MoneyFlowViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bindRecord(MoneyFlowRecord record) {
            mId = record.getId();
            tvValue.setText(record.getValueAsString());
            tvDescription.setText(record.getDescription());
            tvDate.setText(record.getFormattedDate());
        }

        @Override
        public void onClick(View view) {
            MoneyFlowEditorActivity.showActivity(MainActivity.this, mId);
        }

        @Override
        public boolean onLongClick(View view) {
            // showing DeleteDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Delete the record?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            // don't call "onClick"
            return true;
        }

        // Delete dialog listener
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Toast.makeText(MainActivity.this, "Deleting.", Toast.LENGTH_SHORT).show();
                        // DELETING the note
                        mMoneyFlowBox.remove(mId);
                        // Refreshing UI
                        updateUI();
                        // Closing the dialog
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No clicked. Just closing the dialog
                        dialog.dismiss();
                        break;
                }
            }
        };
    }

    class MoneyFlowAdapter extends RecyclerView.Adapter<MoneyFlowViewHolder> {
        private List<MoneyFlowRecord> mRecords = new ArrayList<>();

        public MoneyFlowAdapter(List<MoneyFlowRecord> records) {
            updateData(records);
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


    class FilterButtonClicked implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // not a radiobutton
            if (!(view instanceof RadioButton)) {
                return;
            }

            switch (rbgFilters.getCheckedRadioButtonId()) {
                // TODO: create the range before
                case R.id.rb_today:
                    mDateRange = new DateRange(DateRange.DateType.DAY);
                    break;
                case R.id.rb_month:
                    mDateRange = new DateRange(DateRange.DateType.MONTH);
                    break;
                case R.id.rb_all_time:
                    mDateRange = new DateRange(DateRange.DateType.ALL_TIME);
                    break;
            }
            updateUI();
        }
    }

}
