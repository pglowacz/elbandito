package pl.app.one.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.app.one.dto.enums.ResponseStatus;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO implements Serializable {
    protected ResponseStatus responseStatus;
    protected Integer gameId;
    protected Integer rno;
    protected String message;
}
