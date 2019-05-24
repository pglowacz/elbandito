package pl.app.one.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SpinResponseDTO implements Serializable {
    private List<int[]> symbols;
    private Integer win;
    private ResponseDTO responseDTO;
}
