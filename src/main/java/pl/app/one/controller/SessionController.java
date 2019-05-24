package pl.app.one.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.one.dto.SessionResponseDTO;
import pl.app.one.service.SessionService;

@RestController
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/sessions")
    public SessionResponseDTO sessions() {
        return sessionService.allSession();
    }

    @GetMapping("/finishAbandonedGames")
    public ResponseEntity<String> finishAbandonedGames(){
        sessionService.endAbandonedGames();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/finishEndGames")
    public ResponseEntity<String> finishEndGames() {
        sessionService.removeFinishedGames();
        return ResponseEntity.ok().build();
    }
}
