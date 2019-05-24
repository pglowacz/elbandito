package pl.app.one.util;

import pl.app.one.domain.Game;
import pl.app.one.dto.enums.GameStatus;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.function.Predicate;

public class BanditUtils {

    private static final int MAX_RNO = 500;
    private static final int MAX_GAME_ID = 200000000;
    private static final int ABANDONED_GAME_TIME = 60 * 1000;//2 minutes

    public static int generateRno(){
        Random random = new Random();
        return random.nextInt(MAX_RNO);
    }

    public static int generateGameId(){
        Random random = new Random();
        return random.nextInt(MAX_GAME_ID);
    }

    public static long timeStamp() {
        return Date.from(Instant.now()).getTime();
    }

    public static boolean checkGameIfAbandoned(long timestamp) {
        long timestampNow = System.currentTimeMillis() - ABANDONED_GAME_TIME;
        return timestamp < timestampNow;
    }

    public static Predicate<Game> gameActivePredicate() {
        return game -> GameStatus.ACTIVE.equals(game.getGameStatus());
    }

    public static Predicate<Game> gameAbandonedPredicate() {
        return game -> GameStatus.ABANDONED.equals(game.getGameStatus());
    }

    public static Predicate<Game> gameEndPredicate() {
        return game -> GameStatus.END.equals(game.getGameStatus());
    }
}
