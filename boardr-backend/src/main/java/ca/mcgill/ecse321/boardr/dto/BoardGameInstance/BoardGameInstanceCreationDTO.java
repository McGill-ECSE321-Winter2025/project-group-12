package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a new BoardGameInstance entity. Also used for updating board game instances.
 * Ensures the required fields are provided when a new instance is created.
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-11
 */

public class BoardGameInstanceCreationDTO {

    @NotBlank(message = "Condition is required")
    private String condition;

    @NotBlank(message = "BoardGame ID is required")
    private Integer boardGameId;

    @NotBlank(message = "GameOwner ID is required")
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
