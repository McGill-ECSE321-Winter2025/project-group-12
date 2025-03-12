package ca.mcgill.ecse321.boardr.dto.Registration;

public class RegistrationDTO {
    private int userId;
    private int eventId;

    public RegistrationDTO() {}

    public RegistrationDTO(int userId, int eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}