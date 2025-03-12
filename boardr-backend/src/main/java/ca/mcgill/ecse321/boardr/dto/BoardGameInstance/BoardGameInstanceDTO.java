package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

/**
* Data Transfer Object class for BoardGameInstance
* Contains all attributes required to create and update a board game instance, as well as sets condition
* Used to transfer information between the frontend and backend while maintaining separation of concerns
* @author Jun Ho Oh
* @version 1.0
* @since 2025-03-11
*/

public class BoardGameInstanceDTO {
    private int individualGameId;
    private String condition;
    private boolean isAvailable;
    private int gameOwnerId;
    private int boardGameId;

    public BoardGameInstanceDTO() {}

    public BoardGameInstanceDTO(int individualGameId, String condition, boolean isAvailable, int gameOwnerId, int boardGameId) {
        this.individualGameId = individualGameId;
        this.condition = condition;
        this.isAvailable = isAvailable;
        this.gameOwnerId = gameOwnerId;
        this.boardGameId = boardGameId;
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

    public int getGameOwnerId() {
        return gameOwnerId;
    }

    public int getBoardGameId() {
        return boardGameId;
    }

    //setters to modify the already existing board game instance DTO
    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setGameOwnerId(int gameOwnerId) {
        this.gameOwnerId = gameOwnerId;
    }

    public void setBoardGameId(int boardGameId) {
        this.boardGameId = boardGameId;
    }
}
