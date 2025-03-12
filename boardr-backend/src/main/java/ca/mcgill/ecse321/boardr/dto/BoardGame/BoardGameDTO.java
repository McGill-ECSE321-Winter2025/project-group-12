package ca.mcgill.ecse321.boardr.dto.BoardGame;

/**
* Data Transfer Object class for BoardGame
* Contains all attributes required to create and update a board game
* Used to transfer information between the frontend and backend while maintaining separation of concerns
* @author Jun Ho Oh
* @version 1.0
* @since 2025-03-11
*/

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
