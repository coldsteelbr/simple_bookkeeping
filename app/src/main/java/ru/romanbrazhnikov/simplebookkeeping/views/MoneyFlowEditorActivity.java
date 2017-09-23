package ru.romanbrazhnikov.simplebookkeeping.views;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import ru.romanbrazhnikov.simplebookkeeping.R;
import ru.romanbrazhnikov.simplebookkeeping.dagger.MyApp;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;
import ru.romanbrazhnikov.simplebookkeeping.screenmodels.FlowEditorScreenModel;

public class MoneyFlowEditorActivity extends AppCompatActivity
        implements SelectedDateInterface {

    private static final String DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG";
    private static String EXTRAS_ID = "EXTRAS_ID";

    MoneyFlowEditorActivity mSelf = this;
    @Inject
    BoxStore mStore;
    private Box<MoneyFlowRecord> mBox;

    private FlowEditorScreenModel mScreenModel;

    // Widgets // TODO: ButterKnife
    private Button bSave;
    private Button bCancel;
    private EditText etValue;
    private EditText etDescription;
    private EditText etDate;
    private RadioGroup rgFlows;
    private RadioButton rbIncome;
    private RadioButton rbExpense;


    // Formats
    private DateFormat mDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.##");


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

        etValue.setText(mScreenModel.getValue());
        etDescription.setText(mScreenModel.getDescription());

        etValue.addTextChangedListener(new TextWatcher() {
            // TODO: formatting
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Buttons
        bSave = findViewById(R.id.b_save);
        bSave.setOnClickListener(new SaveButtonClickListener());
        bCancel = findViewById(R.id.b_cancel);
        bCancel.setOnClickListener(new CancelButtonClickListener());

        // Date text
        etDate = findViewById(R.id.tv_date_picker);
        etDate.setText(mScreenModel.getDate());
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: send string and formatter, not Date
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
        rbIncome = findViewById(R.id.rb_income);
        rbExpense = findViewById(R.id.rb_expense);
        // setting checked an appropriate radio button
        switch (mScreenModel.getFlow()) {

            case INCOME:
                rbIncome.setChecked(true);
                break;

            case EXPENSE:
                rbExpense.setChecked(true);
                break;
        }
    }

    @Override
    public void onDateSet(Date date) {
        // TODO: bind the model
        try {
            mScreenModel.setDate(mDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        etDate.setText(mScreenModel.getDate());
    }


    //
    //  EVENT LISTENERS
    //

    // ON SAVE
    class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //
            // save all data to the screen model
            //

            // FIRST OF ALL SAVING THE FLOW TODO: check the sign in the model
            switch (rgFlows.getCheckedRadioButtonId()) {
                case R.id.rb_income:
                    mScreenModel.setFlow(FlowEditorScreenModel.Flows.INCOME);
                    break;
                case R.id.rb_expense:
                    mScreenModel.setFlow(FlowEditorScreenModel.Flows.EXPENSE);
                    break;
            }

            // saving value
            try {
                mScreenModel.setValue(etValue.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // saving date
            try {
                mScreenModel.setDate(etDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // saving description
            mScreenModel.setDescription(etDescription.getText().toString());

            //
            // get the record
            //

            MoneyFlowRecord recordToSave = mScreenModel.getRecord();

            //
            // save the record
            //

            mBox.put(recordToSave);
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

        // TODO: save id's state, and everything
        long id = 0;
        if (getIntent().hasExtra(EXTRAS_ID)) {
            id = getIntent().getLongExtra(EXTRAS_ID, 0);
        }

        // record shouldn't be null
        MoneyFlowRecord record = null;
        if (id > 0) {
            record = mBox.get(id);
        }
        if (record == null) {
            record = new MoneyFlowRecord();
        }

        // setting screen model
        mScreenModel = new FlowEditorScreenModel(record, mDateFormat, mDecimalFormat);

        initWidgets();
    }

}
