package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class BorrowRequest {
    @Id
    @GeneratedValue
    private int borrowRequestId;
    private Date requestDate;
    private Date returnDate;
    private RequestStatus status;

    public enum RequestStatus {
        Pending,
        Accepted,
        Declined;
    }

    @ManyToOne
    @JoinColumn(name = "board_game_instance_id")
    private BoardGameInstance boardGameInstance;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User user;


    BorrowRequest(){};

    BorrowRequest(BoardGameInstance boardGameInstance, User user, Date requestDate, Date returnDate) {
        this.boardGameInstance = boardGameInstance;
        this.user = user;
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

    public User getUser() {
        return user;
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
}
