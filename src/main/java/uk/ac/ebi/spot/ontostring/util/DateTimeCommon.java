package uk.ac.ebi.spot.ontostring.util;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeCommon {

    private static DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

    private static DateTimeFormatter ISO_DATE_FORMATTER = ISODateTimeFormat.date();

    public static DateTimeFormatter getIsoDateTimeFormatter() {
        return ISO_DATE_TIME_FORMATTER;
    }

    public static DateTimeFormatter getIsoDateFormatter() {
        return ISO_DATE_FORMATTER;
    }
}
