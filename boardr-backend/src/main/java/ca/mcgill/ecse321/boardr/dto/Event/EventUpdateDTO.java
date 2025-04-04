package ca.mcgill.ecse321.boardr.dto.Event;

public class EventUpdateDTO {
    private int eventDate;
    private int eventTime;
    private String location;
    private String description;

    public EventUpdateDTO() {}

    public EventUpdateDTO(int eventDate, int eventTime, String location, String description) {
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.description = description;
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
}
