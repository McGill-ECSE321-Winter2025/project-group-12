package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.EventDTO;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        // Convert DTO to Event entity
        Event event = new Event();
        event.setEventDate(eventDTO.getEventDate());
        event.setEventTime(eventDTO.getEventTime());
        event.setLocation(eventDTO.getLocation());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());

        // Fetch related entities
        UserAccount organizer = userAccountRepository.findById(eventDTO.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found."));
        BoardGameInstance gameInstance = boardGameInstanceRepository.findById(eventDTO.getBoardGameInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Board game instance not found."));
        event.setOrganizer(organizer);
        event.setBoardGameInstance(gameInstance);

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
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}