package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;

/**
 * DTO for returning BoardGameInstance details as a response.
 * Includes an extra field for the game owner's name.
 * 
 * @author Jun Ho
 * @version 1.1
 * @since 2025-03-11
 */
public class BoardGameInstanceDTO {

    private int individualGameId;
    private String boardGameName;
    private String condition;
    private boolean isAvailable;
    private int boardGameId;
    private int gameOwnerId;
    private String gameOwnerName;  // New field for the owner's name

    // Default constructor (for serialization)
    @SuppressWarnings("unused")
    private BoardGameInstanceDTO() {}

    /**
     * Constructs a DTO from a BoardGameInstance.
     * This constructor does NOT set the gameOwnerName.
     */
    public BoardGameInstanceDTO(BoardGameInstance boardGameInstance) {
        this.individualGameId = boardGameInstance.getindividualGameId();
        this.boardGameName = boardGameInstance.getBoardGame().getName();
        this.condition = boardGameInstance.getCondition();
        this.isAvailable = boardGameInstance.isAvailable();
        this.boardGameId = boardGameInstance.getBoardGame().getGameId();
        this.gameOwnerId = boardGameInstance.getGameOwner().getId();
    }

    /**
     * Overloaded constructor that also sets the gameOwnerName.
     *
     * @param boardGameInstance the instance
     * @param gameOwnerName the name of the game owner (retrieved separately)
     */
    public BoardGameInstanceDTO(BoardGameInstance boardGameInstance, String gameOwnerName) {
        this(boardGameInstance);
        this.gameOwnerName = gameOwnerName;
    }

    public int getIndividualGameId() {
        return individualGameId;
    }

    public String getBoardGameName() {
        return boardGameName;
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

    public String getGameOwnerName() {
        return gameOwnerName;
    }

    public void setGameOwnerName(String gameOwnerName) {
        this.gameOwnerName = gameOwnerName;
    }
}
