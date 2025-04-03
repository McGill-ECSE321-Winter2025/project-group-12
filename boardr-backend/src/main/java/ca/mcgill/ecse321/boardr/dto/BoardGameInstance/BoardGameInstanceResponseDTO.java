package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;

/**
 * DTO for returning BoardGameInstance details as a response. Look at Response DTO as output.
 * Used when retrieving data from the backend.
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-11
 */

public class BoardGameInstanceResponseDTO {

    private int individualGameId;
    private String boardGameName;
    private String condition;
    private boolean isAvailable;
    private int boardGameId;
    private int gameOwnerId;

    @SuppressWarnings("unused")
    private BoardGameInstanceResponseDTO() {
    }

    public BoardGameInstanceResponseDTO(BoardGameInstance boardGameInstance) {
        this.individualGameId = boardGameInstance.getIndividualGameId();
        this.boardGameName = boardGameInstance.getBoardGame().getName();
        this.condition = boardGameInstance.getCondition();
        this.isAvailable = boardGameInstance.isAvailable();
        this.boardGameId = boardGameInstance.getBoardGame().getGameId();
        this.gameOwnerId = boardGameInstance.getGameOwner().getId();
    }

    public int getIndividualGameId() {
        return individualGameId;
    }

    public String getBoardGameName() {
        return boardGameName;
    }

    public void setBoardGameName(String boardGameName) {
        this.boardGameName = boardGameName;
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
