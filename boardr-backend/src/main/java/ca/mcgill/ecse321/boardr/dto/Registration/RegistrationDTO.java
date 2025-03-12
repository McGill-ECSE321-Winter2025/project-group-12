package ca.mcgill.ecse321.boardr.dto.Registration;

/**
 * Data Transfer Object class for Registration
 * Contains all attributes required to create a Registration
 * Used to transfer information between the frontend and backend while maintaining separation of concerns
 * @author David Vo
 * @version 1.0
 * @since 2025-03-12
 */

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