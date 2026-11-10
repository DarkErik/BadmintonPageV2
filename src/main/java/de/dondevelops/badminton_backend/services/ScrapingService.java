package de.dondevelops.badminton_backend.services;

import com.example.model.Comment;
import de.dondevelops.badminton_backend.dtos.*;
import de.dondevelops.badminton_backend.util.DateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapingService {

    private final RestClient restClient;
    private final GameService gameService;
    private final GameDateService gameDateService;

    public String getSessionCookie() {
        String cookieUrl = "https://dbv.turnier.de/cookiewall/?returnurl=%2Fsport%2Fteammatch.aspx%3Fid%3D925D6245-1FA1-496D-9810-1439487E5801%26match%3D4925";

        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(cookieUrl)
                    .header(
                            HttpHeaders.USER_AGENT,
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/120.0.0.0 Safari/537.36"
                    )
                    .header(HttpHeaders.ACCEPT, "*/*")
                    .header(HttpHeaders.HOST, "dbv.turnier.de")
                    .retrieve()
                    .toEntity(String.class);

            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

            if (cookies == null || cookies.isEmpty()) {
                System.err.println("Keine Set-Cookie Header erhalten");
                return null;
            }


            // Nur den Teil vor dem ersten ';' jedes Cookies nehmen
            String cookieHeader = cookies.stream()
                    .map(cookie -> cookie.split(";", 2)[0])
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");

            cookieHeader = cookieHeader.replace("  ", " ");


//            getWebsiteDom("https://dbv.turnier.de/sport/teammatches.aspx?id=D6F7A756-39F3-4CB4-8F27-0E30E0421F4A&tid=361", cookieHeader + "&c=1&cp=23");

            log.debug("Created Session Cookie: {}", cookieHeader);
            return cookieHeader + "&c=1&cp=23";

        } catch (Exception e) {
            System.err.println("Failed to fetch Cookie:");
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Document getWebsiteDom(String dbvUrl, String cookie) {

        for (int tries = 0; tries < 2; tries++) {
            try {

                String website = restClient.get()
                        .uri(dbvUrl)
                        .header(
                                HttpHeaders.USER_AGENT,
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                        "Chrome/120.0.0.0 Safari/537.36"
                        )
                        .header(HttpHeaders.ACCEPT, "*/*")
                        .header(HttpHeaders.HOST, "dbv.turnier.de")
                        .header(HttpHeaders.COOKIE, cookie)
                        .header(
                                HttpHeaders.REFERER,
                                "https://dbv.turnier.de/cookiewall/?returnurl=%2Fsport%2Fteammatches.aspx%3Fid%3D925D6245-1FA1-496D-9810-1439487E5801%26tid%3D1040"
                        )
                        .retrieve()
                        .body(String.class);

                // Equivalent to cheerio.load(website)
                return Jsoup.parse(website);

            } catch (Exception e) {

                log.debug("Failed to fetch Website DOM: [ {} ] {}", tries, dbvUrl);
                log.error(e.getMessage());
            }
        }

        return null;
    }


    @Scheduled(initialDelay = 60, fixedDelay = 120, timeUnit = TimeUnit.MINUTES)
    public void scheduledScrape() throws InterruptedException {
        for(TeamDto team : gameService.getAllTeams()) {
            log.info("Scheduled scraping for: {}", team.getTeamName());
            String cookie = getSessionCookie();
            retrieveGames(team.getDbvLink(), cookie, team.getId());

            Thread.sleep(60 * 60 * 1000);
        }
    }


    public boolean retrieveGames(String matchUrl, String cookie, int teamId) {
        Document dom = getWebsiteDom(matchUrl, cookie);

        if (dom == null) {
            return false;
        }

        Elements gameRows = dom.select("table.ruler.matches > tbody > tr");

        int gameOrdinal = 0;
        for (Element gameRow : gameRows) {

            Elements cells = gameRow.select("> td");
            Element matchAnchor = cells.get(6).selectFirst("a");

            if (matchAnchor == null)
                return false;


            String matchLink = "https://dbv.turnier.de/sport/" + matchAnchor.attr("href");
            Document matchDom = getWebsiteDom(matchLink, cookie);

            if (matchDom == null)
                return false;

            GameDto game = gameService.findGameOrEmptyDefault(teamId, gameOrdinal);

            extractGameData(matchDom, cells, matchLink, game);
            game = gameService.saveGames(game); // Save game to generate UUID



            List<DetailScoreDto> detailScores = extractDetailScores(matchDom, game.getId());
            List<CommentDto> comments = extractComments(matchDom, game.getId());

            extractDatesFromComments(comments, game.getId());

            gameService.saveDetailScores(detailScores);
            gameService.saveComments(comments);

            gameOrdinal++;
        }

        return true;
    }

    private void extractDatesFromComments(List<CommentDto> comments, int gameId) {
        comments.sort(Comparator.comparing(CommentDto::getCreationDate));
        for(CommentDto c : comments) {
            Instant commentDate = DateParser.stringToDate(c.getText());

            if (commentDate != null)
                gameDateService.addChangedDateIfFeasible(
                        GameDateDto.builder()
                                .gameId(gameId)
                                .gameTime(commentDate)
                                .approved(false)
                                .created_at(c.getCreationDate())
                                .build()
                );
        }
    }

    public void extractGameData(Document matchDom, Elements cells, String matchLink, GameDto game) {
        Instant currentGameTime, originalGameTime;
        {
            Instant[] extractedTimings = extractGameTime(matchDom);
            currentGameTime = extractedTimings[0];
            originalGameTime = extractedTimings[1];
        }


        String host = cells.get(6).text();
        String guest = cells.get(8).text();

        String score = cells.get(9).text().isEmpty() ? "?" : cells.get(9).text();

        String location = cells.get(11).text();

        Element locationAnchor = cells.get(11).selectFirst("a");

        String locationLink = null;

        if (locationAnchor != null) {
            locationLink = "https://dbv.turnier.de/sport/" + locationAnchor.attr("href");
        }



        game.setHost(host);
        game.setGuest(guest);
        game.setScore(score);
        game.setGameTime(currentGameTime);
        game.setOriginalGameTime(originalGameTime);
        game.setLocation(location);
        game.setLocationLink(locationLink);
        game.setMatchLink(matchLink);
    }

    public List<DetailScoreDto> extractDetailScores(Document matchDom, Integer gameId) {


        List<DetailScoreDto> detailScore = new ArrayList<>();

        Elements matchRows =
                matchDom.select("table.ruler.matches > tbody > tr");

        int gameOrdinal = 0;
        for (Element matchRow : matchRows) {
            Elements matchCells = matchRow.select("> td");

            //Match not played yet OR forfeit
            if (matchCells.get(1).text().isEmpty() && matchCells.get(3).text().isEmpty()) {
                Element score = matchDom.selectFirst(".score");
                if (score == null)
                    return List.of();
                else
                    return List.of(new DetailScoreDto(
                            gameId,
                            gameOrdinal,
                            "-",
                            List.of("-"),
                            List.of("-"),
                            List.of("-")
                    ));
            }

            List<String> hostPlayers = matchCells.get(1)
                    .select("> table > tbody > tr")
                    .stream()
                    .map(Element::text)
                    .map(String::trim)
                    .toList();

            List<String> guestPlayers = matchCells.get(3)
                    .select("> table > tbody > tr")
                    .stream()
                    .map(Element::text)
                    .map(String::trim)
                    .toList();

            List<String> sets = Arrays.stream(
                            matchCells.get(4).text().split(" "))
                    .filter(s -> !s.isBlank())
                    .toList();

            DetailScoreDto match = new DetailScoreDto(
                    gameId,
                    gameOrdinal++,
                    matchCells.get(0).text(), // GameType as HD, MX, HE, DD, DE
                    hostPlayers,
                    guestPlayers,
                    sets
            );

            detailScore.add(match);
        }
        return detailScore;
    }

    public List<CommentDto> extractComments(Document matchDom, Integer gameId) {
        // Kommentare extrahieren
        List<CommentDto> comments = new ArrayList<>();

        List<Element> commentTables = matchDom.select("table.ruler")
                .stream()
                .filter(table -> {
                    Element caption = table.selectFirst("caption");

                    return caption != null
                            && caption.text()
                            .trim()
                            .equalsIgnoreCase("Kommentar");
                })
                .toList();

        for (Element commentTable : commentTables) {
            Elements commentLines =
                    commentTable.select("tbody > tr");

            for (Element commentLine : commentLines) {
                Elements commentLineTds =
                        commentLine.select("> td");

                if (commentLineTds.size() < 4) {
                    continue;
                }

                // TODO: Check order of text arguments!
                CommentDto comment = CommentDto.builder()
                        .gameId(gameId)
                        .text(commentLineTds.get(1).text())
                        .creationDate(DateParser.stringToDate(commentLineTds.get(3).text()))
                        .author(commentLineTds.get(2).text())
                        .build();

                comments.add(comment);
            }
        }

        return comments;
    }

    public Instant[] extractGameTime(Document matchDom) {
        Element firstContentTable =
                matchDom.select("#content table").first();

        String timeText = "";

        if (firstContentTable != null) {
            Element firstRow =
                    firstContentTable.select("tbody > tr").first();

            if (firstRow != null) {
                Element firstTd =
                        firstRow.select("> td").first();

                if (firstTd != null) {
                    timeText = firstTd.text();
                }
            }
        }

        String originalTime = timeText;

        if (timeText.contains("Verbandsansetzung")) {
            String[] parts = timeText.split("Verbandsansetzung", 2);

            timeText = parts[0];
            originalTime = parts.length > 1
                    ? parts[1]
                    : parts[0];
        }

        Instant parsedTime = DateParser.stringToDate(timeText);

        if (parsedTime == null) {
            parsedTime = LocalDateTime.of(1970, 1, 1, 0, 0).atZone(ZoneId.of("Europe/Berlin")).toInstant();
        }

        Instant parsedOriginalTime =
                DateParser.stringToDate(originalTime);

        return new Instant[] {parsedTime, parsedOriginalTime};
    }
}
