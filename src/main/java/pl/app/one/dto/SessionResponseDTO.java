package pl.app.one.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SessionResponseDTO implements Serializable {
    List<Integer> activeGameList;
    List<Integer> endGameList;
    List<Integer> abandonedGameList;
}
