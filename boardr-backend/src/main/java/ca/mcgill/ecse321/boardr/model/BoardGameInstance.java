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
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    // TODO: Owner will need to be the GameOwner Class, not the User class
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    BoardGameInstance() {}

    BoardGameInstance(BoardGame boardGame, String condition) {
        this.boardGame = boardGame;
        // this.gameOwner = gameOwner;
        this.condition = condition;
        this.isAvailable = true; // I'm assuming available when added
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
}
