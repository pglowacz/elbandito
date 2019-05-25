package pl.app.one.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
@Builder
public class BanditSimulationProperties {
    private List<int[]> reels;
    private int[] spin;
    private int[] winnings;
    @JsonProperty("lines_winnings")
    private List<int[]> linesWinnings;
}
