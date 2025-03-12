package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

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
