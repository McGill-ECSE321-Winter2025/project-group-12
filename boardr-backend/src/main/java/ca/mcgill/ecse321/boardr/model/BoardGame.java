package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;



@Entity
public class BoardGame {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String description;
    
    protected BoardGame() {}

    public BoardGame(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public int getId() {
		return this.id;
	}

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

}
