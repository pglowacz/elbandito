package pl.app.one.dao;

import org.springframework.stereotype.Repository;
import pl.app.one.domain.Game;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.util.BanditUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dostęp do tzw. bazy danych
 *
 * @author Pawel Glowacz
 */
@Repository
public class BanditDaoImpl implements BanditDao {
    private final BanditUtils banditUtils;
    private Map<Integer,Game> sessionGames = new ConcurrentHashMap<>();

    public BanditDaoImpl(BanditUtils banditUtils) {
        this.banditUtils = banditUtils;
    }

    /**
     * Dodaje grę do db.
     *
     * @param game gra
     *
     * @return gre
     */
    @Override
    public Game addGame(Game game) {
        return sessionGames.put(game.getGameId(),game);
    }

    /**
     * Aktualizuje grę w db.
     *
     * @param game gra
     *
     * @return gre
     */
    @Override
    public Game updateGame(Game game) {
        game.setLastActualGameTime(banditUtils.timeStamp());
        return sessionGames.replace(game.getGameId(),game);
    }

    /**
     * Usuwa grę z db
     *
     * @param gameId id gry
     */
    @Override
    public void removeGame(Integer gameId) {
        sessionGames.remove(gameId);
    }

    /**
     * Sprawdza limit gier aktywnych
     *
     * @return true jeśli przekroczony
     */
    @Override
    public boolean checkActiveGameLimit() {
        return sessionGames.values().stream().filter(
                game -> GameStatus.ACTIVE.name().equals(game.getGameStatus().name())).count() >= 20;
    }

    /**
     * Zwraca wszystkie gry z db.
     *
     * @return lista gier
     */
    @Override
    public List<Game> allGames() {
        return new ArrayList<>(sessionGames.values());
    }

    /**
     * Pobiera grę z db.
     *
     * @param gameId id gry
     * @return gre
     */
    @Override
    public Game getGame(Integer gameId) {
        return sessionGames.get(gameId);
    }
}
