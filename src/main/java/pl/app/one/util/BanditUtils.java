package pl.app.one.util;

import pl.app.one.domain.Game;
import pl.app.one.dto.enums.GameStatus;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Klasa narzędziowa.
 *
 * @author Pawel Glowacz
 */
public class BanditUtils {

    private static final int MAX_RNO = 500;
    private static final int MAX_GAME_ID = 200000000;
    private static final int ABANDONED_GAME_TIME = 3 * 60 * 1000;//3 minutes

    /**
     * Generuje rno tzw. pozycje walców
     *
     * @return pozycje walców
     */
    public static int generateRno(){
        Random random = new Random();
        return random.nextInt(MAX_RNO);
    }

    /**
     * Generuje id gry.
     *
     * @return id gry
     */
    public static int generateGameId(){
        Random random = new Random();
        return random.nextInt(MAX_GAME_ID);
    }

    /**
     * Aktualny czas
     *
     * @return aktualny czas
     */
    public static long timeStamp() {
        return Date.from(Instant.now()).getTime();
    }

    /**
     * Sprawdza czy czas gry nie przekracza {@link #ABANDONED_GAME_TIME} minut
     * @param timestamp czas gry
     *
     * @return true jeśli przekroczony w przeciwnym wypadku false
     */
    public static boolean checkGameIfAbandoned(long timestamp) {
        long timestampNow = System.currentTimeMillis() - ABANDONED_GAME_TIME;
        return timestamp < timestampNow;
    }

    /**
     * Predykat gry aktywnej
     *
     * @return predykat
     */
    public static Predicate<Game> gameActivePredicate() {
        return game -> GameStatus.ACTIVE.equals(game.getGameStatus());
    }

    /**
     * Predykat gry przuconej
     *
     * @return predykat
     */
    public static Predicate<Game> gameAbandonedPredicate() {
        return game -> GameStatus.ABANDONED.equals(game.getGameStatus());
    }

    /**
     * Predykat gry zakończonej
     *
     * @return predykat
     */
    public static Predicate<Game> gameEndPredicate() {
        return game -> GameStatus.END.equals(game.getGameStatus());
    }
}
