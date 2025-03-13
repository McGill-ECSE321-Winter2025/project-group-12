package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new BoardGameInstance entity.
 * Ensures the required fields are provided when a new instance is created.
 */
public class BoardGameInstanceCreationDTO {

    @NotNull(message = "Condition is required")
    private String condition;

    @NotNull(message = "BoardGame ID is required")
    private Integer boardGameId;

    @NotNull(message = "GameOwner ID is required")
    private Integer gameOwnerId;

    public BoardGameInstanceCreationDTO(String condition, Integer boardGameId, Integer gameOwnerId) {
        this.condition = condition;
        this.boardGameId = boardGameId;
        this.gameOwnerId = gameOwnerId;
    }

    public String getCondition() {
        return condition;
    }

    public Integer getBoardGameId() {
        return boardGameId;
    }

    public Integer getGameOwnerId() {
        return gameOwnerId;
    }
}
