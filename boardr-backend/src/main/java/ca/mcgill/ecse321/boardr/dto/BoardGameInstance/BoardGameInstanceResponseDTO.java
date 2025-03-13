package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;

/**
 * DTO for returning BoardGameInstance details as a response.
 * Used when retrieving data from the backend.
 */
public class BoardGameInstanceResponseDTO {

    private int individualGameId;
    private String condition;
    private boolean isAvailable;
    private int boardGameId;
    private int gameOwnerId;

    // Jackson needs a no-args constructor, but it doesn't need to be public
    @SuppressWarnings("unused")
    private BoardGameInstanceResponseDTO() {
    }

    public BoardGameInstanceResponseDTO(BoardGameInstance boardGameInstance) {
        this.individualGameId = boardGameInstance.getindividualGameId();
        this.condition = boardGameInstance.getCondition();
        this.isAvailable = boardGameInstance.isAvailable();
        this.boardGameId = boardGameInstance.getBoardGame().getGameId();
        this.gameOwnerId = boardGameInstance.getGameOwner().getId();
    }

    public int getIndividualGameId() {
        return individualGameId;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getBoardGameId() {
        return boardGameId;
    }

    public int getGameOwnerId() {
        return gameOwnerId;
    }
}
