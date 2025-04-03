package ca.mcgill.ecse321.boardr.dto.Event;

import ca.mcgill.ecse321.boardr.model.Event;

/**
 * DTO for returning Event details as a response.
 * Now includes the name of the board game.
 * Used when retrieving data from the backend.
 * 
 * @author 
 * @version 2.1
 * @since 2025-03-12
 */
public class EventDTO {

    private int eventId;
    private int eventDate;
    private int eventTime;
    private String location;
    private String description;
    private int maxParticipants;
    private int boardGameInstanceId;
    private int organizerId;
    private String gameName; // New field for the board game's name

    // Default constructor (for serialization)
    @SuppressWarnings("unused")
    private EventDTO() {}

    /**
     * Constructs a DTO from an Event.
     * 
     * @param event the Event model
     */
    public EventDTO(Event event) {
        this.eventId = event.getEventId();
        this.eventDate = event.getEventDate();
        this.eventTime = event.getEventTime();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.maxParticipants = event.getmaxParticipants();
        this.boardGameInstanceId = event.getboardGameInstance().getIndividualGameId();
        this.organizerId = event.getOrganizer().getUserAccountId();
        // Retrieve the board game's name from the boardGameInstance's associated BoardGame
        this.gameName = event.getboardGameInstance().getBoardGame().getName();
    }

    public int getEventId() {
        return eventId;
    }

    public int getEventDate() {
        return eventDate;
    }

    public int getEventTime() {
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

    public int getBoardGameInstanceId() {
        return boardGameInstanceId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public String getGameName() {
        return gameName;
    }
}
