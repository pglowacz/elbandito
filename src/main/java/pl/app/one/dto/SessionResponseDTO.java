package pl.app.one.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDTO implements Serializable {
    List<Integer> activeGameList;
    List<Integer> endGameList;
    List<Integer> abandonedGameList;
}
