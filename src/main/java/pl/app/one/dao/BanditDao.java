package pl.app.one.dao;

import pl.app.one.domain.Game;

import java.util.List;

public interface BanditDao {
    public Game addGame(Game game);
    public Game updateGame(Game game);
    public void removeGame(Integer gameId);
    public boolean checkActiveGameLimit();
    public List<Game> allGames();
    public Game getGame(Integer gameId);
}
