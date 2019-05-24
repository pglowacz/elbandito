package pl.app.one.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.app.one.dto.ResponseDTO;
import pl.app.one.dto.SpinRequestDTO;
import pl.app.one.dto.SpinResponseDTO;
import pl.app.one.service.BanditService;

@RestController
@Slf4j
public class BanditController {

    private final BanditService banditService;

    public BanditController(BanditService banditService) {
        this.banditService = banditService;
    }

    @GetMapping("/startGame")
    public ResponseDTO startGame(){
        return banditService.startGame();
    }

    @PostMapping("/spin")
    public SpinResponseDTO spin(@RequestBody SpinRequestDTO spinRequestDTO){
        return banditService.spin(spinRequestDTO);
    }

    @GetMapping("/endGame")
    public ResponseDTO endGame(@RequestParam Integer gameId){
        return banditService.endGame(gameId);
    }
}
