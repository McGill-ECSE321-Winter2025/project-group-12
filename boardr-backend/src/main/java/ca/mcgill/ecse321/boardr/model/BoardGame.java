package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;


@Entity
public class BoardGame {
    @Id
    @GeneratedValue
    private int gameId;
    private String name;
    private String description;

    @OneToMany
    private Set<BoardGameInstance> boardGameInstances;
    
    protected BoardGame() {}

    public BoardGame(String name, String description) {
        this.name = name;
        this.description = description;
        this.boardGameInstances = new HashSet<>();
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
}
