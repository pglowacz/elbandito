package pl.app.one.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.app.one.dto.enums.GameStatus;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@EqualsAndHashCode
@ToString
public class Game {
    private Integer gameId;
    private Integer rno;
    private List<List<int[]>> symbolList = new ArrayList<>();
    private GameStatus gameStatus;
    private Integer win;
    private long lastActualGameTime;
}
