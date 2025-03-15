package ca.mcgill.ecse321.boardr.dto.Event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new Event entity. CreationDTO is the input to create/update events.
 * Ensures the required fields are provided when a new event is created/updated.
 * 
 * @author David Vo
 * @version 2.0
 * @since 2025-03-12
 */
public class EventCreationDTO {

    @NotNull(message = "Event date is required")
    private Integer eventDate;

    @NotNull(message = "Event time is required")
    private Integer eventTime;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Max participants must be at least 1")
    private int maxParticipants;

    @NotNull(message = "Board game instance ID is required")
    private Integer boardGameInstanceId;

    @NotNull(message = "Organizer ID is required")
    private Integer organizerId;

    public EventCreationDTO() {}

    public EventCreationDTO(int eventDate, int eventTime, String location, String description, int maxParticipants, int boardGameInstanceId, int organizerId) {
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.boardGameInstanceId = boardGameInstanceId;
        this.organizerId = organizerId;
    }

    public Integer getEventDate() {
        return eventDate;
    }

    public Integer getEventTime() {
        return eventTime;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public Integer getBoardGameInstanceId() {
        return boardGameInstanceId;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }
}