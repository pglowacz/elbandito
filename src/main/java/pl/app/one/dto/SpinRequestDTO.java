package pl.app.one.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SpinRequestDTO implements Serializable {
    private Integer gameId;
    private Integer rno;
    private Integer bet;
}
