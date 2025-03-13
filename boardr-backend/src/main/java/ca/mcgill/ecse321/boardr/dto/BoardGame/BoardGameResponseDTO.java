package ca.mcgill.ecse321.boardr.dto.BoardGame;

import ca.mcgill.ecse321.boardr.model.BoardGame;

/**
 * DTO for returning BoardGame details as a response.
 * Used when retrieving data from the backend.
 */
public class BoardGameResponseDTO {

    private int gameId;
    private String name;
    private String description;

    // Jackson needs a no-args constructor, but it doesn't need to be public
    @SuppressWarnings("unused")
    private BoardGameResponseDTO() {
    }

    public BoardGameResponseDTO(BoardGame boardGame) {
        this.gameId = boardGame.getGameId();
        this.name = boardGame.getName();
        this.description = boardGame.getDescription();
    }

    public int getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
