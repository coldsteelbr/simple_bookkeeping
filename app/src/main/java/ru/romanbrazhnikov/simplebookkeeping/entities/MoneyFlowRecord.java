package ru.romanbrazhnikov.simplebookkeeping.entities;

import java.math.BigDecimal;
import java.util.Date;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
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

/*  TODO: REMOVE THIS CODE AT ALL, OR INJECT THE DATEFORMAT DEPENDENCY
    @Transient
    SimpleDateFormat mSdf = new SimpleDateFormat("dd.MM.yyyy");


    public String getFormattedDate() {
        return mSdf.format(date);
    }
*/

    /**
     * Converts BigDecimal to String and back to be saved in and read of the ObjectBox
     */
    public static class ValueConverter implements PropertyConverter<BigDecimal, String> {

        @Override
        public BigDecimal convertToEntityProperty(String databaseValue) {
            return new BigDecimal(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(BigDecimal entityProperty) {
            return entityProperty.toString();
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
    public String getValueAsString() {
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

}
