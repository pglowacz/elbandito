package pl.app.one.dao;

import org.springframework.stereotype.Repository;
import pl.app.one.domain.Game;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.util.BanditUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BanditDaoImpl implements BanditDao {
    private Map<Integer,Game> sessionGames = new ConcurrentHashMap<>();

    @Override
    public Game addGame(Game game) {
        return sessionGames.put(game.getGameId(),game);
    }

    @Override
    public Game updateGame(Game game) {
        game.setLastActualGameTime(BanditUtils.timeStamp());
        return sessionGames.replace(game.getGameId(),game);
    }

    @Override
    public void removeGame(Integer gameId) {
        sessionGames.remove(gameId);
    }

    @Override
    public boolean checkActiveGameLimit() {
        return sessionGames.values().stream().filter(
                game -> GameStatus.ACTIVE.name().equals(game.getGameStatus().name())).count() >= 20;
    }

    @Override
    public List<Game> allGames() {
        return new ArrayList<>(sessionGames.values());
    }

    @Override
    public Game getGame(Integer gameId) {
        return sessionGames.get(gameId);
    }
}
