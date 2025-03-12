package ca.mcgill.ecse321.boardr.dto.BoardGame;

public class BoardGameDTO {
    private int gameId;
    private String name;
    private String description;

    public BoardGameDTO() {}

    public BoardGameDTO(int gameId, String name, String description) {
        this.gameId = gameId;
        this.name = name;
        this.description = description;
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

    //setters to update the DTO (already existing board game)
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
