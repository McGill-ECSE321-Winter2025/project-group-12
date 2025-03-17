package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

/**
 * Represents a board game entity with a unique ID, name, and description for database management.
 * 
 * @author Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */

@Entity
public class BoardGame {
    @Id
    @GeneratedValue /* Game Id is irrelevant to user, but valuable for table management */
    private int gameId;
    private String name;
    private String description;

    public BoardGame() {}

    public BoardGame(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getGameId() {
		return this.gameId;
	}

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    /* Allows modification of the board game's details.*/
    public void setDescription(String description) {
        this.description = description;
    }
    
}
