package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

import java.sql.Date;

/**
 * Represents a borrow request for a board game, including request and return dates, status, and associated user and game instance.
 * @author Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
public class BorrowRequest {
    @Id
    @GeneratedValue /* RequestId is irrelevant to user, but valuable for table management */
    private int borrowRequestId;
    private Date requestDate;
    private Date returnDate;
    private RequestStatus status;

    public enum RequestStatus { /* Expired Requests will default to Declined */
        Pending,
        Accepted,
        Declined;
    }

    @ManyToOne
    @JoinColumn(name = "board_game_instance_id")
    private BoardGameInstance boardGameInstance;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private UserAccount userAccount;


    public BorrowRequest(){};

    public BorrowRequest(BoardGameInstance boardGameInstance, UserAccount userAccount, Date requestDate, Date returnDate) {
        this.boardGameInstance = boardGameInstance;
        this.userAccount = userAccount;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
        this.status = RequestStatus.Pending;
    }

    public int getBorrowRequestId() {
        return borrowRequestId;
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

    public RequestStatus getRequestStatus() {
        return status;
    }

    public void setRequestStatus(RequestStatus status) {
        this.status = status;
    }
}
