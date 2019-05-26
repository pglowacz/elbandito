package pl.app.one.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.app.one.dao.BanditDaoImpl;
import pl.app.one.domain.Game;
import pl.app.one.dto.SessionResponseDTO;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.util.BanditUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService tests")
public class SessionServiceImplTest {

    @Mock
    private BanditDaoImpl banditDao;

    @Mock
    private BanditUtils banditUtils;

    private SessionServiceImpl sessionService;

    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);

        sessionService = new SessionServiceImpl(banditDao, banditUtils);
    }

    @Test
    public void allSessionTest() {
        List<Game> gameList = allGames();
        Mockito.when(banditDao.allGames()).thenReturn(gameList);
        Mockito.when(banditUtils.gameActivePredicate()).thenReturn(game -> GameStatus.ACTIVE.equals(game.getGameStatus()));
        Mockito.when(banditUtils.gameAbandonedPredicate()).thenReturn(game -> GameStatus.ABANDONED.equals(game.getGameStatus()));
        Mockito.when(banditUtils.gameEndPredicate()).thenReturn(game -> GameStatus.END.equals(game.getGameStatus()));
        SessionResponseDTO actualResponseDTO = createSessionResponseDTO();

        SessionResponseDTO expectedResponseDTO = sessionService.allSession();

        Assertions.assertEquals(expectedResponseDTO,actualResponseDTO);
    }

    @Test
    public void endAbandonedGamesTest(){
        List<Game> gameList = allGames();
        Mockito.when(banditDao.allGames()).thenReturn(gameList);
        Mockito.when(banditUtils.gameAbandonedPredicate()).thenReturn(game -> GameStatus.ABANDONED.equals(game.getGameStatus()));
        Mockito.when(banditDao.updateGame(gameList.get(1))).thenReturn(gameList.get(1));

        sessionService.endAbandonedGames();

        Mockito.verify(banditDao,Mockito.times(1)).updateGame(gameList.get(1));
    }

    @Test
    public void removeFinishedGamesTest(){
        List<Game> gameList = allGames();
        Mockito.when(banditDao.allGames()).thenReturn(gameList);
        Mockito.when(banditUtils.gameEndPredicate()).thenReturn(game -> GameStatus.END.equals(game.getGameStatus()));
        Mockito.doNothing().when(banditDao).removeGame(3);

        sessionService.removeFinishedGames();

        Mockito.verify(banditDao,Mockito.times(1)).removeGame(3);
    }

    private SessionResponseDTO createSessionResponseDTO() {
        return SessionResponseDTO.builder()
                .abandonedGameList(Collections.singletonList(2))
                .activeGameList(Collections.singletonList(1))
                .endGameList(Collections.singletonList(3))
                .build();
    }

    private List<Game> allGames() {
        List<Game> gameList = new ArrayList<>();
        gameList.add(createGame(GameStatus.ACTIVE,1));
        gameList.add(createGame(GameStatus.ABANDONED,2));
        gameList.add(createGame(GameStatus.END,3));
        return  gameList;
    }

    private Game createGame(GameStatus gameStatus, Integer gameId) {
        return Game.builder()
                .win(0)
                .symbolList(new ArrayList<>())
                .rno(1)
                .gameId(gameId)
                .gameStatus(gameStatus)
                .lastActualGameTime(1L).build();
    }
}
