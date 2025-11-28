package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static LocalDate parse(String tgl) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(tgl, df);
    }
}
