package ca.mcgill.ecse321.boardr.dto.Event;

/**
* Data Transfer Object class for Event
* Contains all attributes required to create an Event
* Used to transfer information between the frontend and backend while maintaining separation of concerns
* @author David Vo
* @version 1.0
* @since 2025-03-12
*/

public class EventDTO {
    private int eventDate;
    private int eventTime;
    private String location;
    private String description;
    private int maxParticipants;
    private int boardGameInstanceId;
    private int organizerId;

    public EventDTO() {}

    public EventDTO(int eventDate, int eventTime, String location, String description, int maxParticipants, int boardGameInstanceId, int organizerId) {
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.boardGameInstanceId = boardGameInstanceId;
        this.organizerId = organizerId;
    }

    public int getEventDate() {
        return eventDate;
    }

    public void setEventDate(int eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventTime() {
        return eventTime;
    }

    public void setEventTime(int eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getBoardGameInstanceId() {
        return boardGameInstanceId;
    }

    public void setBoardGameInstanceId(int boardGameInstanceId) {
        this.boardGameInstanceId = boardGameInstanceId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }
}