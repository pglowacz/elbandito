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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.app.one.constant.Constants.*;

@Service
@Slf4j
public class BanditServiceImpl implements BanditService {

    private final BanditDao banditDao;
    private final BanditSimulationProperties banditSimulationProperties;

    public BanditServiceImpl(BanditDao banditDao, BanditSimulationProperties banditSimulationProperties) {
        this.banditDao = banditDao;
        this.banditSimulationProperties = banditSimulationProperties;
    }

    public ResponseDTO startGame() {
        Game game = Game.builder()
                .gameStatus(GameStatus.ACTIVE)
                .gameId(BanditUtils.generateGameId())
                .rno(BanditUtils.generateRno())
                .lastActualGameTime(BanditUtils.timeStamp())
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

    public SpinResponseDTO spin(SpinRequestDTO spinRequestDTO) {
        Game game = banditDao.getGame(spinRequestDTO.getGameId());
        if(game != null){
            SpinResponseDTO.SpinResponseDTOBuilder spinResponseDTOBuilder = SpinResponseDTO.builder();
            ResponseDTO responseDTO = ResponseDTOMapper.INSTANCE.gameToResponseDTO(game);
            if(game.getGameStatus() == GameStatus.ACTIVE) {
                int win = wheelBandit(game);
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

    private int wheelBandit(Game game) {
        int actualRno = game.getRno()+1;

        //Copy values from properties
        int[] spins = Arrays.stream(banditSimulationProperties.getSpin()).toArray();

        List<int[]> reels = new ArrayList<>();
        banditSimulationProperties.getReels().forEach(item ->{
           reels.add(Arrays.stream(item).toArray());
        });

        List<int[]> winningList = new ArrayList<>();
        banditSimulationProperties.getWinnings().forEach(item -> {
            winningList.add(Arrays.stream(item).toArray());
        });


        List<int[]> actualSymbols = new ArrayList<>();
        for(int i = 0; i < spins.length; i++) {
            final int spin = spins[i];
            final int[] reel = reels.get(i);
            System.out.println("Before "+Arrays.toString(reel));

            //Określamy położenie walców za pomocą rno
            for (int a = 0; a < actualRno; a++) {
                for (int j = reel.length - 1; j > 0; j--) {
                    int temp = reel[j];
                    reel[j] = reel[j - 1];
                    reel[j - 1] = temp;
                }
            }
            System.out.println("After "+Arrays.toString(reel));

            //Potem kręcimy walcami o N spinów zdefiniowanych w pliku i pobieramy wyświetlone symbole na maszynce
            actualSymbols.add(Arrays.stream(reel, spin - 3, spin)
                    .toArray());
        }
        System.out.println("Aktualne Symbole: ");
        actualSymbols.forEach(item->System.out.println(Arrays.toString(item)));

        //TODO: Check winnings
        game.setRno(actualRno);
        game.getSymbolList().add(actualSymbols);
        game.setWin(game.getWin()+0);
        return 0;
    }

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
                responseDTO.setResponseStatus(ResponseStatus.ERROR);
                responseDTO.setMessage(WRONG_GAME_STATUS_TO_DELETE_BY_USER);
            }
            return responseDTO;
        }
        return createGeneralResponseGameNotExists(gameId);
    }

    private SpinResponseDTO createSpinResponseGameNotExists(Integer gameId){
        return SpinResponseDTO.builder().responseDTO(createGeneralResponseGameNotExists(gameId)).build();
    }
    private ResponseDTO createGeneralResponseGameNotExists(Integer gameId) {
        log.error("Game id: {}, ",gameId);
        return ResponseDTO.builder()
                .gameId(gameId)
                .responseStatus(ResponseStatus.ERROR)
                .message(NO_GAME_EXISTS)
                .build();
    }
}
