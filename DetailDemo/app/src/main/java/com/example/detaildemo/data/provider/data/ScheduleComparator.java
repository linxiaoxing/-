package com.example.detaildemo.data.provider.data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * ソート処理.
 */
public class ScheduleComparator implements Comparator<Date>, Serializable{
    private static final long serialVersionUID = -1L;

    @Override
    public int compare(final Date date1, final Date date2) {
        return date1.compareTo(date2);
    }
}