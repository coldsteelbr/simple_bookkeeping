package ru.romanbrazhnikov.simplebookkeeping.screenmodels;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;

import ru.romanbrazhnikov.simplebookkeeping.entities.MoneyFlowRecord;

/**
 * Created by roman on 23.09.17.
 */

public class FlowEditorScreenModel {

    public enum Flows {
        INCOME,
        EXPENSE
    }

    private MoneyFlowRecord mRecord;
    private DateFormat mDateFormat;// = new SimpleDateFormat("dd.MM.yyyy");
    private DecimalFormat mValueFormat;// = new DecimalFormat("#0.##");
    private Flows mFlow;


    public FlowEditorScreenModel(@NonNull MoneyFlowRecord record,
                                 @NonNull DateFormat dateFormat,
                                 @NonNull DecimalFormat decimalFormat) {
        mRecord = record;
        // if value <= 0 - set as Expense, 'cause it's more often an expense than income
        if (mRecord.getValue().compareTo(BigDecimal.ZERO) < 1) {
            mFlow = Flows.EXPENSE;
        } else {
            mFlow = Flows.INCOME;
        }

        // Formats
        mDateFormat = dateFormat;
        mValueFormat = decimalFormat;
    }

    public MoneyFlowRecord getRecord() {
        return mRecord;
    }

    //
    // Reading properties
    //
    public long getId() {
        return mRecord.getId();
    }

    /**
     * Formatted date
     */
    public String getDate() {
        return mDateFormat.format(mRecord.getDate());
    }

    /**
     * Formatted value without the sign
     */
    public String getValue() {
        BigDecimal value = mRecord.getValue();
        // if value < 0
        if (value.compareTo(BigDecimal.ZERO) == -1) {
            value = value.negate();
        }
        return mValueFormat.format(value);
    }

    /**
     * The flow type: expense or income
     */
    public Flows getFlow() {
        return mFlow;
    }

    /**
     * Description
     */
    public String getDescription() {
        return mRecord.getDescription();
    }

    //
    // Writing properties
    //

    /**
     * The flow type: expense or income
     */
    public void setFlow(Flows flow) {
        mFlow = flow;
    }

    /**
     * Sets value on an unsigned string value and flow type
     */
    public void setValue(String valueAsString) throws ParseException {
        // parsing the value
        // TODO: Decimal parsing
        //BigDecimal value = (BigDecimal) mValueFormat.parse(valueAsString);
        BigDecimal value = new BigDecimal(valueAsString);

        // saving value, negating if expense
        switch (mFlow) {
            case INCOME:
                mRecord.setValue(value);
                break;
            case EXPENSE:
                mRecord.setValue(value.negate());
                break;
        }
    }

    /**
     * Sets real date based on a formatted string date value
     */
    public void setDate(String date) throws ParseException {
        mRecord.setDate(mDateFormat.parse(date));
    }

    /**
     * Sets description
     */
    public void setDescription(String description) {
        mRecord.setDescription(description);
    }
}
