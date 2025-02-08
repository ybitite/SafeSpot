package ch.y.bitite.safespot.model.room;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Converters {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @TypeConverter
    public static String fromDate(Date date) {
        return date == null ? null : formatter.format(date);
    }

    @TypeConverter
    public static Date toDate(String dateString) {
        try {
            return dateString == null ? null : formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}