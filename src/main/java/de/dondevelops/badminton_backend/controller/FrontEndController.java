package de.dondevelops.badminton_backend.controller;

import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.services.GameService;
import jakarta.servlet.http.HttpServletRequest;
import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FrontEndController {

    private final GameService gameService;

    @GetMapping("/")
    public ResponseEntity<String> getBaseWebside() {
        List<TeamDto> teams = gameService.getAllTeams();
        String links = StringUtil.join(teams.stream().map(t -> "<li><a href=\"" + t.getTeamUrl() + "\">" + t.getTeamName() + "</a></li>").toList(), "");
        String webside = "<h1>TG Bochum Teams:</h1><ul>" + links + "</ul>";
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(webside);
    }

    @GetMapping("/{team}")
    public String redirectToTeam(@PathVariable String team) {
        return "redirect:/" + team + "/";
    }

    @GetMapping("/{team}/")
    public ResponseEntity<Resource> index(@PathVariable String team) {

        Resource resource =
                new ClassPathResource("static/index.html");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @GetMapping("/{team}/assets/{filename}")
    public ResponseEntity<Resource> asset(
            @PathVariable String team,
            @PathVariable String filename) {

        Resource resource =
                new ClassPathResource("static/assets/" + filename);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaTypeFactory
                .getMediaType(filename)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
