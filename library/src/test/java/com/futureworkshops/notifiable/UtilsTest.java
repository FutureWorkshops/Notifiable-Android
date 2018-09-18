/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by stelian on 12/04/2018.
 */
public class UtilsTest {

    @Test
    public void testFormatDate() throws Exception {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.YEAR, 2018);
        calendar.set(Calendar.MONTH, 3); // 0 based
        calendar.set(Calendar.DAY_OF_MONTH, 6); // 1 based
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.SECOND, 27);


        // Expected string according to  "EEE, dd MMM yyyy HH:mm:ss" and the date set in the Calendar
        String expectedDateFormat = "Fri, 06 Apr 2018 13:13:27 GMT";

        // return the formatted date using the calendar GMT input
        String jodaDateFormat = Utils.formatDate(calendar.getTime());

        Assert.assertTrue(expectedDateFormat.equals(jodaDateFormat));

    }


    @Test
    public void testFormatDateWithWeirdLocale() throws Exception {
        Locale.setDefault(new Locale("ar"));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.YEAR, 2018);
        calendar.set(Calendar.MONTH, 3); // 0 based
        calendar.set(Calendar.DAY_OF_MONTH, 6); // 1 based
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.SECOND, 27);


        // Expected string according to  "EEE, dd MMM yyyy HH:mm:ss" and the date set in the Calendar
        String expectedDateFormat = "Fri, 06 Apr 2018 13:13:27 GMT";

        // return the formatted date using the calendar GMT input
        String jodaDateFormat = Utils.formatDate(calendar.getTime());

        Assert.assertTrue(expectedDateFormat.equals(jodaDateFormat));
    }


}