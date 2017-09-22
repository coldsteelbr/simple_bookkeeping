package ru.romanbrazhnikov.simplebookkeeping.entities;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.converter.PropertyConverter;

/**
 * Created by roman on 18.09.17.
 */

@Entity
public class MoneyFlowRecord {
    @Id
    private long id;
    @Convert(converter = ValueConverter.class, dbType = String.class)
    private BigDecimal value = BigDecimal.ZERO;
    private String description;
    private Date date = new Date();
    @Convert(converter = FlowConverter.class, dbType = Integer.class)
    private FlowDirection flowDirection;

    @Transient
    SimpleDateFormat mSdf = new SimpleDateFormat("dd.MM.yyyy");

    public enum FlowDirection{
        INCOME(0),
        EXPENSE(1);

        private final int mValue;

        FlowDirection(int value){
            mValue = value;
        }

        public int getValue(){
            return mValue;
        }
    }

    public String getFormattedDate() {
        return mSdf.format(date);
    }


    /** Converts BigDecimal to String and back to be saved in and read of the ObjectBox
     * */
    public static class ValueConverter implements PropertyConverter<BigDecimal, String>{

        @Override
        public BigDecimal convertToEntityProperty(String databaseValue) {
            return new BigDecimal(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(BigDecimal entityProperty) {
            return entityProperty.toString();
        }
    }

    public static class FlowConverter implements PropertyConverter<FlowDirection, Integer>{

        @Override
        public FlowDirection convertToEntityProperty(Integer databaseValue) {
            return FlowDirection.values()[databaseValue];
        }

        @Override
        public Integer convertToDatabaseValue(FlowDirection entityProperty) {
            return entityProperty.getValue();
        }
    }

    // Constructors
    public MoneyFlowRecord() {
    }

    public MoneyFlowRecord(BigDecimal value, String description) {
        this.value = value;
        this.description = description;
    }

    // Custom properties
    public String getValueAsString(){
        return value.toString();
    }

    // Properties
    public long getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FlowDirection getFlowDirection() {
        return flowDirection;
    }

    public void setFlowDirection(FlowDirection flowDirection) {
        this.flowDirection = flowDirection;
    }
}
