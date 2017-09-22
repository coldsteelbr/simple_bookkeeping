package ru.romanbrazhnikov.simplebookkeeping.views;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import ru.romanbrazhnikov.simplebookkeeping.R;
import ru.romanbrazhnikov.simplebookkeeping.dagger.MyApp;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;

public class MoneyFlowEditorActivity extends AppCompatActivity
        implements SelectedDateInterface {

    private static final String DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG";
    private static String EXTRAS_ID = "EXTRAS_ID";

    MoneyFlowEditorActivity mSelf = this;
    @Inject
    BoxStore mStore;
    private Box<MoneyFlowRecord> mBox;
    // TODO: shouldn't be null
    private MoneyFlowRecord mRecord = null;

    // Widgets
    private Button bSave;
    private Button bCancel;
    private EditText etValue;
    private EditText etDescription;
    private EditText etDate;
    private RadioGroup rgFlows;
    private long mDateInMilliseconds;

    private DateFormat mDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static void showActivity(Context context, Long id) {
        Intent intent = new Intent(context, MoneyFlowEditorActivity.class);

        if (id != null) {
            intent.putExtra(EXTRAS_ID, id);
        }

        context.startActivity(intent);
    }

    /**
     * Initializes Widgets & layouts & events
     */
    private void initWidgets() {
        // Text fields
        etValue = findViewById(R.id.et_value);
        etDescription = findViewById(R.id.et_description);
        if (mRecord != null) {
            etValue.setText(mRecord.getValueAsString());
            etDescription.setText(mRecord.getDescription());
        }

        // Buttons
        bSave = findViewById(R.id.b_save);
        bSave.setOnClickListener(new SaveButtonClickListener());
        bCancel = findViewById(R.id.b_cancel);
        bCancel.setOnClickListener(new CancelButtonClickListener());

        // Date text
        etDate = findViewById(R.id.tv_date_picker);
        etDate.setText(mDateFormat.format(mRecord.getDate()));
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Date date = null;
                try {
                    date = mDateFormat.parse(etDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date == null) {
                    date = new Date();
                }
                DatePickerFragment dialog = DatePickerFragment.getInstance(date);
                dialog.show(fm, DATE_PICKER_DIALOG);
            }
        });


        // radio group flows
        rgFlows = findViewById(R.id.rg_flows);
    }

    @Override
    public void onDateSet(Date date) {
        mDateInMilliseconds = date.getTime();
        etDate.setText(mDateFormat.format(date));
    }


    //
    //  EVENT LISTENERS
    //

    // ON SAVE
    class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mRecord == null) {
                mRecord = new MoneyFlowRecord(
                        new BigDecimal(etValue.getText().toString()),
                        etDescription.getText().toString());
            } else {
                mRecord.setValue(new BigDecimal(etValue.getText().toString()));
                mRecord.setDescription(etDescription.getText().toString());
            }

            switch (rgFlows.getCheckedRadioButtonId()){
                case R.id.rb_income:
                    break;
                case R.id.rb_expense:
                    mRecord.setValue(mRecord.getValue().negate());
                    break;
            }

            // TODO: working with date
            mRecord.setDate(new Date(mDateInMilliseconds));
            mBox.put(mRecord);
            mSelf.finish();
        }
    }

    // ON CANCEL
    class CancelButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mSelf.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_flow_editor);

        // TODO: to a base activity
        ((MyApp) getApplication()).getBookkeepingComponent().inject(this);
        // TODO: Inject?
        mBox = mStore.boxFor(MoneyFlowRecord.class);

        long id = 0;
        if (getIntent().hasExtra(EXTRAS_ID)) {
            id = getIntent().getLongExtra(EXTRAS_ID, 0);
        }

        // TODO: mRecord shouldn't be null
        if (id > 0) {
            mRecord = mBox.get(id);
        } else {
            mRecord = new MoneyFlowRecord();
        }


        initWidgets();
    }

}
