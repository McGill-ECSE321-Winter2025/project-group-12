package ca.mcgill.ecse321.boardr.dto.BorrowRequest;

import java.sql.Date;

import ca.mcgill.ecse321.boardr.model.BorrowRequest;

public class BorrowRequestResponseDTO {

    private int id;
    private int userAccountId;
    private int boardGameInstanceId;
    private Date requestDate;
    private Date returnDate;
    private BorrowRequest.RequestStatus requestStatus;

    @SuppressWarnings("unused")
    private BorrowRequestResponseDTO() {
    }

    public BorrowRequestResponseDTO(BorrowRequest borrowRequest) {
        this.id = borrowRequest.getBorrowRequestId();
        this.userAccountId = borrowRequest.getUserAccount().getUserAccountId();
        this.boardGameInstanceId = borrowRequest.getBoardGameInstance().getindividualGameId();
        this.requestDate = borrowRequest.getRequestDate();
        this.returnDate = borrowRequest.getReturnDate();
        this.requestStatus = borrowRequest.getRequestStatus();
    }

    public int getId() {
        return id;
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public int getBoardGameInstanceId() {
        return boardGameInstanceId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public BorrowRequest.RequestStatus getRequestStatus() {
        return requestStatus;
    }
}
