package de.dondevelops.badminton_backend.services;

import com.example.model.GameDateInfo;
import de.dondevelops.badminton_backend.dtos.GameDateDto;
import de.dondevelops.badminton_backend.dtos.GameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalenderService {

    private final GameService gameService;
    private final GameDateService gameDateService;


    public String generateCalendar(int teamId) {
        List<String> lines = new LinkedList<>();

        lines.add("BEGIN:VCALENDAR");
        lines.add("VERSION:2.0");
        lines.add("PRODID:TG Bochum");
        lines.add("CALSCALE:GREGORIAN");

        List<GameDto> games = gameService.getGamesByTeam(teamId);

        List<GameDateDto> changedDates = games.stream()
                .map( g -> gameDateService.getGameDateChangesForGameId(g.getId()).orElse(null))
                .toList();

        for (int i = 0; i < games.size(); i++) {

            GameDto game = games.get(i);

            Instant startTime = game.getGameTime();


            if (changedDates.size() > i && changedDates.get(i) != null) {

                startTime = changedDates.get(i).getGameTime();
            }

            lines.add("BEGIN:VEVENT");

            lines.add("UID:" + md5(game.getMatchLink()) + "@tg.dondevelops.de");

            lines.add("DTSTAMP:" + formatICSDate(game.getOriginalGameTime()));

            lines.add("DTSTART:" + formatICSDate(startTime));

            lines.add("DTEND:" + formatICSDate(startTime.plus(Duration.ofHours(2))));

            lines.add("LAST-MODIFIED:" + formatICSDate(Instant.now()));

            lines.add("SUMMARY:" + escapeICS(game.getHost() + " vs. " + game.getGuest()));

            lines.add("DESCRIPTION:" + game.getHost() + " (HEIM) gegen " + game.getGuest() + " (GAST). Spielort: " + game.getLocation() + ".");

            lines.add("END:VEVENT");
        }

        lines.add("END:VCALENDAR");

        return String.join("\r\n", lines);
    }


    private String formatICSDate(Instant date) {
        return DateTimeFormatter
                .ofPattern("yyyyMMdd'T'HHmmss'Z'")
                .withZone(java.time.ZoneOffset.UTC)
                .format(date);
    }

    private String escapeICS(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }

    private String md5(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 not available", e);
        }
    }


}
