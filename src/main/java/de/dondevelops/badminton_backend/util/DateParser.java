package de.dondevelops.badminton_backend.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class DateParser {

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b(0?[1-9]|[12]\\d|3[01])\\.(0?[1-9]|1[0-2])(?:\\.(\\d{2}|\\d{4}))?\\b"
    );

    private static final Pattern TIME_PATTERN = Pattern.compile(
            "\\b(?:([01]?\\d|2[0-3]):([0-5]\\d)|([01]?\\d|2[0-3])\\.([0-5]\\d)\\s*[Uu][Hh][Rr])|(?:([01]?\\d|2[0-3])\\s*[Uu][Hh][Rr])\\b"
    );

    public static Instant stringToDate(String input) {

        Matcher dateMatcher = DATE_PATTERN.matcher(input);

        String latestDate = null;

        // Letztes gefundenes Datum ermitteln
        while (dateMatcher.find()) {
            latestDate = dateMatcher.group();
        }

        if (latestDate == null) {
            return null;
        }

        // Datum aufteilen
        String[] dateParts = latestDate.split("\\.");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);

        int year;

        if (dateParts.length < 3) {
            year = LocalDate.now().getYear();
        } else {
            year = Integer.parseInt(dateParts[2]);

            if (year < 100) {
                year += 2000;
            }
        }

        // Zeit ermitteln
        Matcher timeMatcher = TIME_PATTERN.matcher(input);

        String latestTime = null;

        while (timeMatcher.find()) {
            latestTime = timeMatcher.group();
        }

        int hour = 0;
        int minute = 0;

        if (latestTime != null) {

            latestTime = latestTime
                    .toLowerCase()
                    .replace("uhr", "")
                    .trim();

            if (latestTime.contains(".")) {
                String[] parts = latestTime.split("\\.");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);

            } else if (latestTime.contains(":")) {
                String[] parts = latestTime.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);

            } else {
                hour = Integer.parseInt(latestTime);
            }
        }

        return LocalDateTime.of(
                year,
                month,
                day,
                hour,
                minute
        ).atZone(ZoneId.of("Europe/Berlin")).toInstant();
    }

    public String instanceToISO(Instant date) {
        return date.toString();
    }
}