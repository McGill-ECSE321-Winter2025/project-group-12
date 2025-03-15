package ca.mcgill.ecse321.boardr.dto.Registration;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new Registration entity. CreationDTO is the input to create registrations.
 * Ensures the required fields are provided when a new registration is created.
 * 
 * @author David Vo
 * @version 1.0
 * @since 2025-03-12
 */
public class RegistrationCreationDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Event ID is required")
    private Integer eventId;

    public RegistrationCreationDTO() {}

    public RegistrationCreationDTO(int userId, int eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getEventId() {
        return eventId;
    }
}