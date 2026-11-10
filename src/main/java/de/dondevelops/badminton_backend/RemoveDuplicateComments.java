package de.dondevelops.badminton_backend;

import com.example.model.AddPlayerRequest;
import de.dondevelops.badminton_backend.dtos.GameDateDto;
import de.dondevelops.badminton_backend.dtos.GameDto;
import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.services.GameDateService;
import de.dondevelops.badminton_backend.services.GameService;
import de.dondevelops.badminton_backend.services.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(4)
public class RemoveDuplicateComments implements CommandLineRunner {

    private final GameService gameService;

    @Override
    public void run(String... args) {
        if (true)
            return;
        gameService.removeDuplicateComments();
    }

}
