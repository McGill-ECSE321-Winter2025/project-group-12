package ca.mcgill.ecse321.boardr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardr.dto.Event.EventDTO;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ca.mcgill.ecse321.boardr.repo.UserAccountRepository userAccountRepository;

    @Autowired
    private ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository boardGameInstanceRepository;

    // Use Case 1: Create an Event
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        // Fetch related entities first
        UserAccount organizer = userAccountRepository.findById(eventDTO.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found."));
        BoardGameInstance gameInstance = boardGameInstanceRepository.findById(eventDTO.getBoardGameInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Board game instance not found."));

        // Convert DTO to Event entity using the public constructor
        Event event = new Event(
            eventDTO.getEventDate(),
            eventDTO.getEventTime(),
            eventDTO.getLocation(),
            eventDTO.getDescription(),
            eventDTO.getMaxParticipants(),
            gameInstance,
            organizer
        );

        // Call service to create event
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.ok(createdEvent);
    }

    // Use Case 2: Delete an Event
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable int eventId, @RequestParam int userId) {
        eventService.deleteEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    // Use Case 6: Display All Available Events
    @GetMapping
    public ResponseEntity<Iterable<Event>> getAllEvents() { // Changed from List<Event> to Iterable<Event>
        Iterable<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}