package de.dondevelops.badminton_backend;

import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.repositories.GameRepository;
import de.dondevelops.badminton_backend.services.GameService;
import de.dondevelops.badminton_backend.services.ScrapingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class CommentlineTestRunner implements CommandLineRunner {

    private final ScrapingService scrapingService;
    private final GameRepository gameRepo;
    private final GameService gameService;

    @Override
    public void run(String... args) {
        log.info("Test scraping :)");

        if(gameRepo.count() <= 0) {
            String cookie = scrapingService.getSessionCookie();
            for(TeamDto team : gameService.getAllTeams()) {
                log.info("Scraping for: {}", team.getTeamName());
                scrapingService.retrieveGames(team.getDbvLink(), cookie, team.getId());
            }
        } else{
            log.info("Actually no need!");
        }
    }
}