package ca.mcgill.ecse321.boardr.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Represents an event with a date, time, location, description, and associated board game instance and organizer.
 * 
 * @author Eric, David Zhou, Junho, Jione, David Vo
 * @version 1.1
 * @since 2023-10-05
 */
@Entity
public class Event {
    @Id
    @GeneratedValue /* EventId is irrelevant to user, but valuable for table management */
    private int eventId;
    private int eventDate;
    private int eventTime;
    private String location;
    private String description;
    private int maxParticipants;

    @ManyToOne
    @JoinColumn(name = "board_game_instance_id")
    private BoardGameInstance boardGameInstance;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private UserAccount organizer;

    @OneToMany(mappedBy = "registrationKey.event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations = new ArrayList<>(); // Initialize here

    protected Event() {}

    public Event(int eventDate, int eventTime, String location, String description, int maxParticipants, BoardGameInstance boardGameInstance, UserAccount organizer) {
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.boardGameInstance = boardGameInstance;
        this.organizer = organizer;
    }

    public int getEventId() {
        return this.eventId;
    }

    public int getEventDate() {
        return this.eventDate;
    }

    public int getEventTime() {
        return this.eventTime;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDescription() {
        return this.description;
    }

    public int getmaxParticipants() {
        return this.maxParticipants;
    }

    public BoardGameInstance getboardGameInstance() {
        return this.boardGameInstance;
    }
    
    public UserAccount getOrganizer() {
        return this.organizer;
    }

    public List<Registration> getRegistrations() {
        return this.registrations;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}