package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class BorrowRequest {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "board_game_instance_id")
    private BoardGameInstance boardGameInstance;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private UserAccount userAccount;

    private Date requestDate;
    private Date returnDate;
    private Status status;

    public enum Status {
        Pending,
        Accepted,
        Declined;
    }

    BorrowRequest(){};

    BorrowRequest(BoardGameInstance boardGameInstance, UserAccount userAccount, Date requestDate, Date returnDate) {
        this.boardGameInstance = boardGameInstance;
        this.userAccount = userAccount;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
        this.status = Status.Pending;
    }

    public int getId() {
        return id;
    }

    public BoardGameInstance getBoardGameInstance() {
        return boardGameInstance;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Status getStatus() {
        return status;
    }
}
