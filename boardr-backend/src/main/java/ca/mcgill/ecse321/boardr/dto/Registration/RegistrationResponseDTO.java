package ca.mcgill.ecse321.boardr.dto.Registration;

import java.sql.Date;

import ca.mcgill.ecse321.boardr.model.Registration;

/**
 * DTO for returning Registration details as a response. ResponseDTO is for the output.
 * Used when retrieving data from the backend such as getters.
 * 
 * @author David Vo
 * @version 1.0
 * @since 2025-03-12
 */
public class RegistrationResponseDTO {

    private int userId;
    private int eventId;
    private Date registrationDate;

    @SuppressWarnings("unused")
    private RegistrationResponseDTO() {}

    public RegistrationResponseDTO(Registration registration) {
        this.userId = registration.getRegistrationKey().getRegistrant().getUserAccountId();
        this.eventId = registration.getRegistrationKey().getEvent().getEventId();
        this.registrationDate = registration.getRegistrationDate();
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }
}