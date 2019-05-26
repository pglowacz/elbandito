package pl.app.one.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.app.one.dao.BanditDao;
import pl.app.one.dao.BanditDaoImpl;
import pl.app.one.domain.Game;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.util.BanditUtils;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("SchedulerService tests")
public class SchedulerServiceTest {

    @Mock
    private BanditDaoImpl banditDao;

    @Mock
    private BanditUtils banditUtils;

    private SchedulerService schedulerService;

    private long timestampAbandoned = 0L;

    @BeforeEach
    public void before() {
        MockitoAnnotations.initMocks(this);

        schedulerService = new SchedulerService(banditDao, banditUtils);
        timestampAbandoned = Date.from(Instant.now()).getTime() - 4 * 60 * 1000;
    }

    @Test
    public void checkGamesForActivityTest() {
        List<Game> gameList = allGames();

        Mockito.when(banditDao.allGames()).thenReturn(gameList);
        Mockito.when(banditUtils.gameAbandonedPredicate()).thenReturn(game -> GameStatus.ABANDONED.equals(game.getGameStatus()));
        Mockito.when(banditUtils.gameEndPredicate()).thenReturn(game -> GameStatus.END.equals(game.getGameStatus()));
        Mockito.when(banditUtils.checkGameIfAbandoned(timestampAbandoned)).thenReturn(true);

        schedulerService.checkGamesForActivity();

        Mockito.verify(banditDao, Mockito.times(1)).updateGame(gameList.get(0));
    }

    private List<Game> allGames() {
        List<Game> gameList = new ArrayList<>();
        gameList.add(createGame());
        return  gameList;
    }

    private Game createGame() {
        return Game.builder()
                .win(0)
                .symbolList(new ArrayList<>())
                .rno(1)
                .gameId(1)
                .gameStatus(GameStatus.ACTIVE)
                .lastActualGameTime(timestampAbandoned).build();
    }
}
