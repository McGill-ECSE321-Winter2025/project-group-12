package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;



@Entity
public class Event {
    @Id
    @GeneratedValue
    private int id;

    private int eventDate;
    private int eventTime;
    private String location;
    private String description;
    private int maxParticipants;

    @OneToOne
    @JoinColumn(name = "board_game_id")
    private BoardGame boardGame;

    @OneToOne
    @JoinColumn(name = "organizer_id")
    private UserAccount organizer;

    
    protected Event() {}

    public Event(int eventDate, int eventTime, String location, String description, int maxParticipants, BoardGame boardGame, UserAccount organizer) {
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.boardGame = boardGame;
        this.organizer = organizer;

    }

    public int getId() {
		return this.id;
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
    public BoardGame getboardGame() {
        return this.boardGame;
    }
    public UserAccount getOrganizer() {
        return this.organizer;
    }

}
