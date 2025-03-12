package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

/**
 * Represents an instance of a board game with a unique ID, condition, and availability status.
 * Reduces redundancy for multiple instances of the same game 
 * @author Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
public class BoardGameInstance {
    @Id
    @GeneratedValue /* GameId is irrelevant to user, but valuable for table management */
    private int individualGameId;
    private String condition;  //Should we have condition? idr
    private boolean isAvailable;

    @ManyToOne 
    @JoinColumn(name = "user_id")
    private GameOwner gameOwner;

    @ManyToOne
    @JoinColumn(name = "board_game_id")
    private BoardGame boardGame; 

    BoardGameInstance() {}

    public BoardGameInstance(BoardGame boardGame, GameOwner gameOwner, String condition) {
        this.boardGame = boardGame;
        this.gameOwner = gameOwner;
        this.condition = condition;
        this.isAvailable = true;
    }

    public int getindividualGameId() {
        return individualGameId;
    }

    public BoardGame getBoardGame() {
        return boardGame;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean bool) {
        this.isAvailable = bool;
    }

    public GameOwner getGameOwner() {
        return gameOwner;
    }
}
