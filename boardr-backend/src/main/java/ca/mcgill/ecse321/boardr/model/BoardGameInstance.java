package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;


@Entity
public class BoardGameInstance {
    @Id
    @GeneratedValue
    private int individualGameId;
    private String condition;  //Should we have condition? idr
    private boolean isAvailable;

    @ManyToOne
    private GameOwner gameOwner;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    BoardGameInstance() {}

    BoardGameInstance(BoardGame boardGame, GameOwner gameOwner, String condition) {
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

    public GameOwner getGameOwner() {
        return gameOwner;
    }
}
