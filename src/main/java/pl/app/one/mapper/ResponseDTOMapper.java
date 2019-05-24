package pl.app.one.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import pl.app.one.domain.Game;
import pl.app.one.dto.ResponseDTO;

@Mapper
public interface ResponseDTOMapper {
    ResponseDTOMapper INSTANCE = Mappers.getMapper(ResponseDTOMapper.class);

    @Mappings({
            @Mapping(source = "gameId", target = "gameId"),
            @Mapping(source = "rno", target = "rno")
    })
    ResponseDTO gameToResponseDTO(Game game);
}
