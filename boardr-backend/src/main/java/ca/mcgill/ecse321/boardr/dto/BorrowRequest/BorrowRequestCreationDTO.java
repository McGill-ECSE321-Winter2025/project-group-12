package ca.mcgill.ecse321.boardr.dto.BorrowRequest;

import jakarta.validation.constraints.NotNull;
import java.sql.Date;

public class BorrowRequestCreationDTO {

    @NotNull(message = "User account ID is required")
    private Integer userAccountId;

    @NotNull(message = "Board game ID is required")
    private Integer boardGameInstanceId;

    @NotNull(message = "Request date is required")
    private Date requestDate;

    @NotNull(message = "Return date is required")
    private Date returnDate;

    public BorrowRequestCreationDTO(Integer userAccountId, Integer boardGameId, Date requestDate, Date returnDate) {
        this.userAccountId = userAccountId;
        this.boardGameInstanceId = boardGameId;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
    }

    public Integer getUserAccountId() {
        return userAccountId;
    }

    public Integer getBoardGameInstanceId() {
        return boardGameInstanceId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public Date getReturnDate() {
        return requestDate;
    }
}
