package pl.app.one.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.app.one.dao.BanditDao;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.util.BanditUtils;

/**
 * Klasa odpowiedzialna za sprawdzanie czy gra nie została opuszczona przez gracza.
 *
 * @author Pawel Glowacz
 */
@Service
@EnableScheduling
@Slf4j
public class SchedulerService {

    private final BanditDao banditDao;

    public SchedulerService(BanditDao banditDao) {
        this.banditDao = banditDao;
    }

    /**
     * Sprawdza czy odpowiednie gry nie zostały opuszczone. Sprawdzanie następuje co 30 sekund.
     */
    @Scheduled(cron = "*/30 * * * * *")
    public void checkGamesForActivity(){
        log.info("Checking game if abandoned");
        banditDao.allGames().stream().filter(
                game -> BanditUtils.checkGameIfAbandoned(game.getLastActualGameTime())
        ).filter(BanditUtils.gameAbandonedPredicate().negate()
        ).filter(BanditUtils.gameEndPredicate().negate()
        ).forEach(game -> {
            log.info("Game with id: {} is abandoned",game.getGameId());
            game.setGameStatus(GameStatus.ABANDONED);
            banditDao.updateGame(game);
        });
    }
}
