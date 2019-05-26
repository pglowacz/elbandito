package pl.app.one.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpinRequestDTO implements Serializable {
    private Integer gameId;
    private Integer bet;
}
