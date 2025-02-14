package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

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
    @JoinColumn(name = "review_id")
    private Set<Review> review;
    
    protected BoardGame() {}

    public BoardGame(String name, String description) {
        this.name = name;
        this.description = description;
        this.review = null;
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

    public Set<Review> getReview() {
        return this.review;
    }
}
