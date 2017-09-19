package ru.romanbrazhnikov.simplebookkeeping.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;

import javax.inject.Inject;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import ru.romanbrazhnikov.simplebookkeeping.R;
import ru.romanbrazhnikov.simplebookkeeping.dagger.MyApp;
import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;

public class MoneyFlowEditorActivity extends AppCompatActivity {

    private static String EXTRAS_ID = "EXTRAS_ID";

    MoneyFlowEditorActivity mSelf = this;
    @Inject
    BoxStore mStore;
    private Box<MoneyFlowRecord> mBox;
    private MoneyFlowRecord mRecord = null;

    // Widgets
    private Button bSave;
    private Button bCancel;
    private EditText etValue;
    private EditText etDescription;


    public static void showActivity(Context context, Long id) {
        Intent intent = new Intent(context, MoneyFlowEditorActivity.class);

        if (id != null) {
            intent.putExtra(EXTRAS_ID, id);
        }

        context.startActivity(intent);
    }

    /** Initializes Widgets & layouts & events
     */
    private void initWidgets() {
        // Text fields
        etValue = findViewById(R.id.et_value);
        etDescription = findViewById(R.id.et_description);

        // Buttons
        bSave = findViewById(R.id.b_save);
        bSave.setOnClickListener(new SaveButtonClickListener());
        bCancel = findViewById(R.id.b_cancel);
        bCancel.setOnClickListener(new CancelButtonClickListener());
    }

    //
    //  EVENT LISTENERS
    //

    // ON SAVE
    class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(mRecord == null){
                mRecord = new MoneyFlowRecord(
                        new BigDecimal(etValue.getText().toString()),
                        etDescription.getText().toString());
            }else{
                mRecord.setValue(new BigDecimal(etValue.getText().toString()));
                mRecord.setDescription(etDescription.getText().toString());
            }

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
        if(getIntent().hasExtra(EXTRAS_ID)) {
            id = getIntent().getLongExtra(EXTRAS_ID, 0);
        }

        if(id > 0){
            mRecord = mBox.get(id);
        }


        initWidgets();
    }


}
