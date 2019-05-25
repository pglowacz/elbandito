package pl.app.one.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.app.one.config.BanditSimulationProperties;
import pl.app.one.constant.Constants;
import pl.app.one.dao.BanditDaoImpl;
import pl.app.one.domain.Game;
import pl.app.one.dto.ResponseDTO;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.dto.enums.ResponseStatus;
import pl.app.one.util.BanditUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
@DisplayName("BanditService tests")
public class BanditServiceImplTest {

    @Mock
    private BanditDaoImpl banditDao;

    @Mock
    private BanditSimulationProperties banditSimulationProperties;

    @Mock
    private BanditUtils banditUtils;

    private int rno = 0;

    private int gameId = 0;

    private long timestamp = 0L;

    private BanditServiceImpl banditService;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.banditService = new BanditServiceImpl(banditDao, banditSimulationProperties, banditUtils);

        this.timestamp = Date.from(Instant.now()).getTime();
        this.rno = new Random().nextInt(BanditUtils.MAX_RNO);
        this.gameId = new Random().nextInt(BanditUtils.MAX_GAME_ID);
    }

    @Test
    public void startGameTest() {
        Game game = createGame(GameStatus.ACTIVE);
        ResponseDTO responseDTOActual = createResponseDTO(ResponseStatus.OK,null);
        Mockito.when(banditDao.checkActiveGameLimit()).thenReturn(false);
        Mockito.when(banditDao.addGame(game)).thenReturn(game);
        Mockito.when(banditUtils.timeStamp()).thenReturn(this.timestamp);
        Mockito.when(banditUtils.generateRno()).thenReturn(this.rno);
        Mockito.when(banditUtils.generateGameId()).thenReturn(this.gameId);

        ResponseDTO expected = banditService.startGame();

        Assertions.assertEquals(responseDTOActual,expected);
    }

    @Test
    public void startGameGameLimitExceedTest() {
        ResponseDTO responseDTOActual = createResponseDTO(ResponseStatus.ERROR,Constants.GAME_LIMIT_EXCEED);
        Mockito.when(banditDao.checkActiveGameLimit()).thenReturn(true);
        Mockito.when(banditUtils.timeStamp()).thenReturn(this.timestamp);
        Mockito.when(banditUtils.generateRno()).thenReturn(this.rno);
        Mockito.when(banditUtils.generateGameId()).thenReturn(this.gameId);

        ResponseDTO expected = banditService.startGame();

        Assertions.assertEquals(responseDTOActual,expected);
    }

    @Test
    public void spinTest() {

    }

    @Test
    public void endGameTest() {
        Game game = createGame(GameStatus.ACTIVE);
        ResponseDTO actualResponseDTO = createResponseDTO(ResponseStatus.OK,null);
        Mockito.when(banditDao.getGame(this.gameId)).thenReturn(game);
        Mockito.when(banditDao.updateGame(game)).thenReturn(game);

        ResponseDTO expectedDTO = banditService.endGame(this.gameId);

        Assertions.assertEquals(expectedDTO,actualResponseDTO);
    }

    @Test
    public void endGameWrongStatusTest(){
        Game game = createGame(GameStatus.ABANDONED);
        ResponseDTO actualResponseDTO = createResponseDTO(ResponseStatus.ERROR, Constants.WRONG_GAME_STATUS_TO_DELETE_BY_USER);
        Mockito.when(banditDao.getGame(this.gameId)).thenReturn(game);

        ResponseDTO expectedDTO = banditService.endGame(this.gameId);

        Assertions.assertEquals(expectedDTO,actualResponseDTO);
    }

    @Test
    public void endGameNotExistsTest(){
        ResponseDTO actualResponseDTO = createResponseDTO(ResponseStatus.ERROR, Constants.NO_GAME_EXISTS);
        actualResponseDTO.setRno(null);
        Mockito.when(banditDao.getGame(this.gameId)).thenReturn(null);
        ResponseDTO expectedDTO = banditService.endGame(this.gameId);

        Assertions.assertEquals(expectedDTO,actualResponseDTO);
    }

    private ResponseDTO createResponseDTO(ResponseStatus responseStatus, String message){
        return ResponseDTO.builder()
                .gameId(this.gameId)
                .responseStatus(responseStatus)
                .rno(this.rno)
                .message(message).build();
    }

    private Game createGame(GameStatus gameStatus) {
        return Game.builder()
                .win(0)
                .symbolList(new ArrayList<>())
                .rno(this.rno)
                .gameId(this.gameId)
                .gameStatus(gameStatus)
                .lastActualGameTime(this.timestamp).build();
    }
}
