package pl.app.one.service;

import pl.app.one.dto.ResponseDTO;
import pl.app.one.dto.SpinRequestDTO;
import pl.app.one.dto.SpinResponseDTO;

public interface BanditService {
    ResponseDTO startGame();
    SpinResponseDTO spin(SpinRequestDTO spinRequestDTO);
    ResponseDTO endGame(Integer gameId);
}
