package ca.mcgill.ecse321.boardr.dto.BoardGame;

import ca.mcgill.ecse321.boardr.model.BoardGame;

/**
 * DTO for returning BoardGame details as a response. ResponseDTO is for the output.
 * Used when retrieving data from the backend such as getters.
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-11
 */
public class BoardGameResponseDTO {

    private int gameId;
    private String name;
    private String description;

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
