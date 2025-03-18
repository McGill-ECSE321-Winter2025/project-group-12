package ca.mcgill.ecse321.boardr.dto.BorrowRequest;

import ca.mcgill.ecse321.boardr.model.BorrowRequest.RequestStatus;
import jakarta.validation.constraints.NotNull;

public class BorrowRequestStatusUpdateDTO {

    @NotNull(message = "Request status is required")
    private RequestStatus requestStatus;

    public BorrowRequestStatusUpdateDTO() {
    }

    public BorrowRequestStatusUpdateDTO(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
