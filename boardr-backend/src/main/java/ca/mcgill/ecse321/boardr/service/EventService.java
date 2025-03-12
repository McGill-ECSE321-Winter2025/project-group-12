package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.Event.EventDTO;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing events in the Boardr application.
 * Provides methods for creating, deleting, and retrieving events.
 * This class interacts with the EventRepository, UserAccountRepository, and BoardGameInstanceRepository
 * to perform operations related to events.
 * @author David Vo
 * @version 1.0
 * @since 2025-03-12
 */

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
        UserAccount organizer = event.getOrganizer();
        if (organizer == null) {
            throw new IllegalArgumentException("Organizer must be provided.");
        }
        BoardGameInstance gameInstance = event.getboardGameInstance();
        if (gameInstance == null) {
            throw new IllegalArgumentException("A board game instance must be provided.");
        }
        // Minimal validation: ensure the game instance exists and is tied to some owner
        // We can't directly check ownership without getUserAccount(), so we assume the provided gameInstance is valid
        // If stricter validation is needed, it should be enforced elsewhere (e.g., database constraints or frontend)
        if (gameInstance.getGameOwner() == null) {
            throw new IllegalArgumentException("Board game instance must have an owner.");
        }
        if (event.getDescription() == null || event.getLocation() == null || event.getmaxParticipants() <= 0) {
            throw new IllegalArgumentException("All event fields must be provided and valid.");
        }
        return eventRepository.save(event);
    }

    // Helper method to create an Event from a DTO
    public Event createEventFromDTO(EventDTO eventDTO) {
        UserAccount organizer = userAccountRepository.findById(eventDTO.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found."));
        BoardGameInstance gameInstance = boardGameInstanceRepository.findById(eventDTO.getBoardGameInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Board game instance not found."));
        Event event = new Event(
            eventDTO.getEventDate(),
            eventDTO.getEventTime(),
            eventDTO.getLocation(),
            eventDTO.getDescription(),
            eventDTO.getMaxParticipants(),
            gameInstance,
            organizer
        );
        return createEvent(event);
    }

    // Use Case 2: Delete an Event
    public void deleteEvent(int eventId, int userId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();
        if (event.getOrganizer().getUserAccountId() != userId) {
            throw new IllegalArgumentException("Only the organizer can delete the event.");
        }
        eventRepository.delete(event);
    }

    // Use Case 6: Display All Available Events
    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}