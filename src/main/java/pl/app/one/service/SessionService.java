package pl.app.one.service;

import pl.app.one.dto.SessionResponseDTO;

public interface SessionService {
    SessionResponseDTO allSession();
    void endAbandonedGames();
    void removeFinishedGames();
}
