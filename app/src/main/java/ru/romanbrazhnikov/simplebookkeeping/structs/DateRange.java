package ru.romanbrazhnikov.simplebookkeeping.structs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.romanbrazhnikov.simplebookkeeping.tools.CalendarDateTools;

/**
 * Created by roman on 21.09.17.
 */

public class DateRange {
    public enum DateType {
        DAY,
        WEEK,
        MONTH,
        YEAR,
        ALL_TIME
    }

    private Date mFromDate;
    private Date mToDate;
    private Calendar mCalendar = GregorianCalendar.getInstance();

    public DateRange(DateType type) {
        switch (type) {
            case DAY:
                setAsFrom(new Date());
                setAsTo(new Date());
                break;
            case WEEK:
                setRangeFor(Calendar.WEEK_OF_MONTH); // WEEK OF YEAR TODO: WHICH WEEK???
                break;
            case MONTH:
                setRangeFor(Calendar.DAY_OF_MONTH);
                break;
            case YEAR:
                setRangeFor(Calendar.YEAR);
                break;
            case ALL_TIME:
                mFromDate = new Date(Long.MIN_VALUE);
                setAsTo(new Date());
                break;
        }
    }

    public DateRange(Date from, Date to) {
        setAsFrom(from);
        setAsTo(to);
    }

    private void setRangeFor(int calendarRangeCode) {
        // FROM
        mCalendar.set(calendarRangeCode,
                mCalendar.getActualMinimum(calendarRangeCode));
        CalendarDateTools.setTimeToBeginningOfDay(mCalendar);
        setAsFrom(mCalendar.getTime());

        // TO
        mCalendar.set(calendarRangeCode,
                mCalendar.getActualMaximum(calendarRangeCode));
        CalendarDateTools.setTimeToBeginningOfDay(mCalendar);
        setAsTo(mToDate = mCalendar.getTime());
    }

    private void setAsFrom(Date date) {
        mCalendar.setTime(date);
        CalendarDateTools.setTimeToBeginningOfDay(mCalendar);
        mFromDate = mCalendar.getTime();
    }

    private void setAsTo(Date date) {
        mCalendar.setTime(date);
        CalendarDateTools.setTimeToEndingOfDay(mCalendar);
        mToDate = mCalendar.getTime();
    }

    //
    // Standard getters
    //
    public Date getFromDate() {
        return mFromDate;
    }

    public Date getToDate() {
        return mToDate;
    }
}