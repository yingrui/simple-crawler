package me.yingrui.simple.crawler.service.extractor;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class DatetimeExtractor implements Extractor {

    private Object datetime;
    private String[] dateFormats = {"yyyy年MM月dd日", "yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss"};

    public DatetimeExtractor() {

    }

    @Override
    public void setSource(Object source) {
        datetime = source;

    }

    @Override
    public Object extract() {
        if (datetime == null) {
            return null;
        }

        if (datetime instanceof String) {
            String datetimeString = (String) datetime;
            if (datetimeString.matches("\\d+")) {
                Long seconds = Long.parseLong(datetimeString);
                Date date = new Date(seconds);
                return format(date);
            } else {
                return extractDate(datetimeString);
            }
        }

        if (datetime instanceof Long) {
            return format(new Date((Long) datetime));
        }

        return null;
    }

    private String extractDate(String datetimeString) {
        try {
            return format(DateUtils.parseDate(datetimeString, dateFormats));
        } catch (ParseException e) {
            return null;
        }
    }

    private String format(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd'T'HH:mm:ssZZZ");
    }
}
