package ca.mcgill.ecse321.boardr.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardr.dto.Event.EventCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventResponseDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventUpdateDTO;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import java.util.stream.Collectors;
import java.util.List;

/**
 * Service class for managing events in the Boardr application.
 * Provides methods for creating, deleting, updating, and retrieving events.
 * This class interacts with the EventRepository, UserAccountRepository, and BoardGameInstanceRepository
 * to perform operations related to events.
 * @author David Vo
 * @version 2.3
 * @since 2025-03-18
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BorrowRequestService borrowRequestService;

    // Helper method to check if a user can create an event or is the event creator
    private void checkUserEventPermission(int userId, BoardGameInstance gameInstance, Event event) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Check if user is the event creator (if event exists)
        boolean isEventCreator = (event != null) && (event.getOrganizer().getUserAccountId() == userId);

        // Check if user is gameOwner or borrower
        Integer gameOwnerRoleId = user.getGameOwnerRoleId(); // Could be null if no GameOwner role
        boolean isGameOwner = gameOwnerRoleId != null && gameInstance.getGameOwner().getId() == gameOwnerRoleId;
        boolean isBorrower = borrowRequestService.getAllBorrowRequests().stream()
                .anyMatch(br -> br.getBoardGameInstance().getIndividualGameId() == gameInstance.getIndividualGameId() &&
                                br.getUserAccount().getUserAccountId() == userId &&
                                br.getRequestStatus() == BorrowRequest.RequestStatus.Accepted);

        // If creating a new event (event == null), user must be gameOwner or borrower
        // If updating an existing event (event != null), user must be the creator AND (gameOwner or borrower for new instance)
        if ((event == null && !isGameOwner && !isBorrower) || (event != null && !isEventCreator)) {
            if (!isGameOwner && !isBorrower) {
                throw new IllegalArgumentException("Organizer must be the owner of the board game instance.");
            }
            if (borrowRequestService.getAllBorrowRequests().stream()
                    .noneMatch(br -> br.getBoardGameInstance().getIndividualGameId() == gameInstance.getIndividualGameId())) {
                throw new IllegalArgumentException("No borrow requests exist for this board game instance.");
            }
            if (borrowRequestService.getAllBorrowRequests().stream()
                    .noneMatch(br -> br.getUserAccount().getUserAccountId() == userId)) {
                throw new IllegalArgumentException("Organizer has not made a borrow request for this board game instance.");
            }
            if (borrowRequestService.getAllBorrowRequests().stream()
                    .noneMatch(br -> br.getRequestStatus() == BorrowRequest.RequestStatus.Accepted)) {
                throw new IllegalArgumentException("Organizer's borrow request for this board game instance has not been approved.");
            }
        }
    }

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

        if (gameInstance.getGameOwner() == null) {
            throw new IllegalArgumentException("Board game instance must have an owner.");
        }

        // Check permissions for creating a new event (event is null)
        checkUserEventPermission(organizer.getUserAccountId(), gameInstance, null);

        if (event.getDescription() == null || event.getLocation() == null || event.getmaxParticipants() <= 0) {
            throw new IllegalArgumentException("All event fields must be provided and valid.");
        }
        return eventRepository.save(event);
    }

    // Helper method to create an Event from a DTO
    public EventResponseDTO createEventFromDTO(EventCreationDTO eventDTO) {
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
        Event savedEvent = createEvent(event);
        return new EventResponseDTO(savedEvent);
    }

    // Use Case 2: Delete an Event
    public void deleteEvent(int eventId, int userId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();
        // Check if user is the event creator
        checkUserEventPermission(userId, event.getboardGameInstance(), event);
        eventRepository.delete(event);
    }

    // Use Case 3: Update an Event
    public EventResponseDTO updateEvent(int eventId, int userId, EventCreationDTO eventDTO) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();

        // Check if user is the event creator
        checkUserEventPermission(userId, event.getboardGameInstance(), event);

        // Update fields from DTO
        event.setEventDate(eventDTO.getEventDate());
        event.setEventTime(eventDTO.getEventTime());
        event.setLocation(eventDTO.getLocation());
        event.setDescription(eventDTO.getDescription());
        event.setmaxParticipants(eventDTO.getMaxParticipants());

        // Update BoardGameInstance if changed
        if (event.getboardGameInstance().getIndividualGameId() != eventDTO.getBoardGameInstanceId()) {
            BoardGameInstance newGameInstance = boardGameInstanceRepository.findById(eventDTO.getBoardGameInstanceId())
                    .orElseThrow(() -> new IllegalArgumentException("Board game instance not found."));
            if (newGameInstance.getGameOwner() == null) {
                throw new IllegalArgumentException("Board game instance must have an owner.");
            }
            // Check permissions for the new game instance (event exists, so creator check applies)
            checkUserEventPermission(userId, newGameInstance, event);
            event.setboardGameInstance(newGameInstance);
        }

        // Validate updated event
        if (event.getDescription() == null || event.getLocation() == null || event.getmaxParticipants() <= 0) {
            throw new IllegalArgumentException("All event fields must be provided and valid.");
        }

        Event updatedEvent = eventRepository.save(event);
        return new EventResponseDTO(updatedEvent);
    }

    // Use Case 6: Display All Available Events
    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Get event by ID
    public EventResponseDTO getEventById(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        return new EventResponseDTO(event);
    }


    
public List<EventDTO> getEventsByBoardGameId(int boardGameId) {
    List<Event> events = eventRepository.findByboardGameInstance_BoardGame_GameId(boardGameId);
    if (events.isEmpty()) {
        throw new IllegalArgumentException("No events found for board game id: " + boardGameId);
    }
    return events.stream()
                 .map(EventDTO::new)
                 .collect(Collectors.toList());
}
public EventResponseDTO updateEventDetails(int eventId, EventUpdateDTO eventUpdateDTO) {
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found."));
    
    // Validate that the input strings are not empty
    if(eventUpdateDTO.getLocation() == null || eventUpdateDTO.getLocation().trim().isEmpty()) {
        throw new IllegalArgumentException("Location cannot be empty.");
    }
    if(eventUpdateDTO.getDescription() == null || eventUpdateDTO.getDescription().trim().isEmpty()) {
        throw new IllegalArgumentException("Description cannot be empty.");
    }
    
    // Update only the specified fields from the DTO
    event.setEventDate(eventUpdateDTO.getEventDate());
    event.setEventTime(eventUpdateDTO.getEventTime());
    event.setLocation(eventUpdateDTO.getLocation());
    event.setDescription(eventUpdateDTO.getDescription());
    
    Event updatedEvent = eventRepository.save(event);
    return new EventResponseDTO(updatedEvent);
}

}