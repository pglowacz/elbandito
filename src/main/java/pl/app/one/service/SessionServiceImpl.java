package pl.app.one.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.app.one.dao.BanditDao;
import pl.app.one.domain.Game;
import pl.app.one.dto.SessionResponseDTO;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.util.BanditUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa odpowiedzialna za operacje na sesji jednorękich bandytów.
 *
 * @author Pawel Glowacz
 */
@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final BanditDao banditDao;

    public SessionServiceImpl(BanditDao banditDao) {
        this.banditDao = banditDao;
    }

    /**
     * Wyświetla wszystkie aktualne sesje.
     *
     * @return komunikat sesyjny
     */
    @Override
    public SessionResponseDTO allSession() {
        List<Game> gameList = banditDao.allGames();

        List<Integer> activeGames = gameList.stream().filter(
                BanditUtils.gameActivePredicate()).map(Game::getGameId).collect(Collectors.toList());

        List<Integer> abandonedGames = gameList.stream().filter(
                BanditUtils.gameAbandonedPredicate()).map(Game::getGameId).collect(Collectors.toList());

        List<Integer> endGames = gameList.stream().filter(
                BanditUtils.gameEndPredicate()).map(Game::getGameId).collect(Collectors.toList());

        return SessionResponseDTO.builder()
                .activeGameList(activeGames)
                .abandonedGameList(abandonedGames)
                .endGameList(endGames).build();
    }

    /**
     * Kończy wszystkie porzucone gry.
     */
    @Override
    public void endAbandonedGames() {
        banditDao.allGames().stream().filter(BanditUtils.gameAbandonedPredicate()
        ).forEach(game -> {
            log.info("End game id {} with abandoned status", game.getGameId());
            game.setGameStatus(GameStatus.END);
            banditDao.updateGame(game);
        });
    }

    /**
     * Usuwa wszystkie zakończone gry z sesji
     */
    @Override
    public void removeFinishedGames() {
        banditDao.allGames().stream().filter(BanditUtils.gameEndPredicate()
        ).forEach(game -> {
            log.info("Remove game id {} with end status from session", game.getGameId());
            banditDao.removeGame(game.getGameId());
        });
    }
}
