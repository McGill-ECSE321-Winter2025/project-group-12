package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;


@Entity
public class BoardGameInstance {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private UserAccount userAccount;

    private String condition;  //Should we have condition? idr
    private boolean isAvailable;

    BoardGameInstance() {}

    BoardGameInstance(BoardGame boardGame, UserAccount userAccount, String condition) {
        this.boardGame = boardGame;
        this.userAccount = userAccount;
        this.condition = condition;
        this.isAvailable = true; // I'm assuming available when added
    }

    public int getId() {
        return id;
    }

    public BoardGame getBoardGame() {
        return boardGame;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
