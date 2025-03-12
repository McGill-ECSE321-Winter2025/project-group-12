package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    // Use Case 1: Create an Event
    public Event createEvent(Event event) {
        // Pre-condition: User must be logged in (assumed handled by authentication)
        UserAccount organizer = event.getOrganizer();
        if (organizer == null) {
            throw new IllegalArgumentException("Organizer must be provided.");
        }

        // Pre-condition: Organizer must have access to the game
        BoardGameInstance gameInstance = event.getBoardGameInstance();
        if (gameInstance == null || (!organizer.getOwnedGames().contains(gameInstance) && !organizer.getBorrowedGames().contains(gameInstance))) {
            throw new IllegalArgumentException("Organizer must own or have borrowed the game.");
        }

        // Validate inputs
        if (event.getDescription() == null || event.getLocation() == null || event.getmaxParticipants() <= 0) {
            throw new IllegalArgumentException("All event fields must be provided and valid.");
        }

        // Save the event to the database
        return eventRepository.save(event);
    }

    // Use Case 2: Delete an Event
    public void deleteEvent(int eventId, int userId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();

        // Pre-condition: User must be the organizer
        if (event.getOrganizer().getUserAccountId() != userId) {
            throw new IllegalArgumentException("Only the organizer can delete the event.");
        }

        eventRepository.delete(event);
    }

    // Use Case 6: Display All Available Events
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}