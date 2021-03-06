package pl.app.one.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.app.one.config.BanditSimulationProperties;
import pl.app.one.dao.BanditDao;
import pl.app.one.domain.Game;
import pl.app.one.dto.ResponseDTO;
import pl.app.one.dto.SpinRequestDTO;
import pl.app.one.dto.SpinResponseDTO;
import pl.app.one.dto.enums.GameStatus;
import pl.app.one.dto.enums.ResponseStatus;
import pl.app.one.mapper.ResponseDTOMapper;
import pl.app.one.util.BanditUtils;

import java.util.*;

import static pl.app.one.constant.Constants.*;

/**
 * Klasa odpowiedzialna za obsługę rozpoczęcia gry, kręcenia walcami, zamknięcia gry
 *
 * @author Pawel Glowacz
 */
@Service
@Slf4j
public class BanditServiceImpl implements BanditService {

    private final BanditDao banditDao;
    private final BanditSimulationProperties banditSimulationProperties;
    private final BanditUtils banditUtils;

    public BanditServiceImpl(BanditDao banditDao,
                             BanditSimulationProperties banditSimulationProperties,
                             BanditUtils banditUtils) {
        this.banditDao = banditDao;
        this.banditSimulationProperties = banditSimulationProperties;
        this.banditUtils = banditUtils;
    }

    /**
     * Rozpoczyna grę.
     *
     * @return informację na temat rozpoczęcia gry
     */
    public ResponseDTO startGame() {
        Game game = Game.builder()
                .gameStatus(GameStatus.ACTIVE)
                .gameId(banditUtils.generateGameId())
                .rno(banditUtils.generateRno())
                .lastActualGameTime(banditUtils.timeStamp())
                .symbolList(new ArrayList<>())
                .win(0)
                .build();

        ResponseDTO responseDTO = ResponseDTOMapper.INSTANCE.gameToResponseDTO(game);;
        if(!banditDao.checkActiveGameLimit()){
            banditDao.addGame(game);
            responseDTO.setResponseStatus(ResponseStatus.OK);
        } else {
            log.error(GAME_LIMIT_EXCEED);
            responseDTO.setResponseStatus(ResponseStatus.ERROR);
            responseDTO.setMessage(GAME_LIMIT_EXCEED);
        }

        return responseDTO;
    }

    /**
     * Symuluje "kręcenie" walców
     *
     * @param spinRequestDTO informacje jakiej gry dotyczy oraz zakładu
     *
     * @return informacje o przeprowadzonym "kręceniu"
     */
    public SpinResponseDTO spin(SpinRequestDTO spinRequestDTO) {
        Game game = banditDao.getGame(spinRequestDTO.getGameId());
        if(game != null){
            SpinResponseDTO.SpinResponseDTOBuilder spinResponseDTOBuilder = SpinResponseDTO.builder();
            ResponseDTO responseDTO = ResponseDTOMapper.INSTANCE.gameToResponseDTO(game);
            if(game.getGameStatus() == GameStatus.ACTIVE) {
                int win = wheelBandit(game, spinRequestDTO.getBet());
                spinResponseDTOBuilder.symbols(game.getSymbolList().get(game.getSymbolList().size() - 1));
                spinResponseDTOBuilder.win(win);
                responseDTO.setResponseStatus(ResponseStatus.OK);
                banditDao.updateGame(game);
            } else {
                log.info("Game id: {} "+WRONG_GAME_STATUS_TO_SPIN_BY_USER,game.getGameId());
                responseDTO.setResponseStatus(ResponseStatus.ERROR);
                responseDTO.setMessage(WRONG_GAME_STATUS_TO_SPIN_BY_USER);
            }
            return spinResponseDTOBuilder.responseDTO(responseDTO).build();
        }
        return createSpinResponseGameNotExists(spinRequestDTO.getGameId());
    }

    /**
     * Główna część "kręcenia" walców
     * @param game pobrana gra z sesji
     * @param bet zakład
     *
     * @return wygrana
     */
    private int wheelBandit(Game game, Integer bet) {
        int actualRno = game.getRno()+1;

        List<int[]> actualSymbols = shiftReels(actualRno);

        log.info("Aktualne Symbole: ");
        actualSymbols.forEach(item->log.info(Arrays.toString(item)));

        int win = checkWinnings(actualSymbols, bet);

        game.setRno(actualRno);
        game.getSymbolList().add(actualSymbols);
        game.setWin(game.getWin()+win);
        return win;
    }

    /**
     * Sprawdza czy wystąpiła wygrana.
     *
     * @param actualSymbols aktualne symbole, które wyświetliły się na jednorękim bandycie
     * @param bet zakład
     *
     * @return wygrana
     */
    private int checkWinnings(List<int[]> actualSymbols, Integer bet) {
        int[] winnings = Arrays.stream(banditSimulationProperties.getWinnings()).toArray();

        List<int[]> winningLineList = new ArrayList<>();
        banditSimulationProperties.getLinesWinnings().forEach(item -> {
            winningLineList.add(Arrays.stream(item).toArray());
        });

        //Spłaszczamy strukturę symboli do jednoelementowej tablicy.
        int[] symbols = actualSymbols.stream().flatMapToInt(Arrays::stream).toArray();

        List<Integer> wonSymbolList = new ArrayList<>();

        //Sprawdzamy czy są takie same symbole w określonych liniach.
        for(int[] lineW: winningLineList){
            Set<Integer> sameNumbersInLine = new HashSet<>();
            Arrays.stream(lineW).forEach(symbolPosition -> sameNumbersInLine.add(symbols[symbolPosition]));
            if(sameNumbersInLine.size()==1){
                log.info("Pozycja linii: {}  posiada 3 takie same symbole: {} ",Arrays.toString(lineW), sameNumbersInLine.toArray()[0]);
                wonSymbolList.addAll(sameNumbersInLine);
            }
            sameNumbersInLine.clear();
        }

        int win = 0;
        if(!wonSymbolList.isEmpty()){
            for(Integer symbol: wonSymbolList){
                win =+ bet*winnings[symbol];
            }
        }
        log.info("Wygrana {} ",win);
        return win;
    }

    /**
     * Ustawiamy pozycje walców i "kręcimy".
     *
     * @param actualRno ilość pozycji, o którą należy przesunać walce
     *
     * @return aktualne wyświetlone symbole
     */
    private List<int[]> shiftReels(int actualRno) {
        int[] spins = Arrays.stream(banditSimulationProperties.getSpin()).toArray();

        List<int[]> reels = new ArrayList<>();
        banditSimulationProperties.getReels().forEach(item ->{
            reels.add(Arrays.stream(item).toArray());
        });

        List<int[]> actualSymbols = new ArrayList<>();
        for(int i = 0; i < spins.length; i++) {
            final int spin = spins[i];
            final int[] reel = reels.get(i);
            log.info("Before "+ Arrays.toString(reel));

            //Określamy położenie walców za pomocą rno
            for (int a = 0; a < actualRno; a++) {
                for (int j = reel.length - 1; j > 0; j--) {
                    int temp = reel[j];
                    reel[j] = reel[j - 1];
                    reel[j - 1] = temp;
                }
            }
            log.info("After "+Arrays.toString(reel));

            //Potem kręcimy walcami o N spinów zdefiniowanych w pliku i pobieramy wyświetlone symbole na maszynce
            actualSymbols.add(Arrays.stream(reel, spin - 3, spin)
                    .toArray());
        }
        return actualSymbols;
    }

    /**
     * Kończy grę.
     *
     * @param gameId id gry
     *
     * @return odpowiedni komunikat
     */
    public ResponseDTO endGame(Integer gameId) {
        Game game = banditDao.getGame(gameId);
        if(game != null){
            ResponseDTO responseDTO = ResponseDTOMapper.INSTANCE.gameToResponseDTO(game);

            if(game.getGameStatus() == GameStatus.ACTIVE){
                log.info("Ending game id {} ",gameId);
                game.setGameStatus(GameStatus.END);
                banditDao.updateGame(game);
                responseDTO.setResponseStatus(ResponseStatus.OK);
            } else {
                log.info("Game id: {} "+WRONG_GAME_STATUS_TO_DELETE_BY_USER,gameId);
                responseDTO.setResponseStatus(ResponseStatus.ERROR);
                responseDTO.setMessage(WRONG_GAME_STATUS_TO_DELETE_BY_USER);
            }
            return responseDTO;
        }
        return createGeneralResponseGameNotExists(gameId);
    }

    /**
     * Wrapper - Tworzy odpowiedni komunikat jeśli gra nie istnieje.
     * @param gameId id gry
     *
     * @return komunikat
     */
    private SpinResponseDTO createSpinResponseGameNotExists(Integer gameId){
        return SpinResponseDTO.builder().responseDTO(createGeneralResponseGameNotExists(gameId)).build();
    }

    /**
     * Tworzy odpowiedni komunikat jeśli gra nie istnieje.
     * @param gameId id gry
     *
     * @return komunikat
     */
    private ResponseDTO createGeneralResponseGameNotExists(Integer gameId) {
        log.error("Game id: {} "+NO_GAME_EXISTS,gameId);
        return ResponseDTO.builder()
                .gameId(gameId)
                .responseStatus(ResponseStatus.ERROR)
                .message(NO_GAME_EXISTS)
                .build();
    }
}
