package ca.mcgill.ecse321.boardr.controller;

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

import ca.mcgill.ecse321.boardr.dto.Event.EventCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventResponseDTO;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.service.EventService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller class for managing events in the Boardr application.
 * Provides endpoints for creating and deleting events, as well as retrieving all events.
 * This class interacts with the EventService to perform operations related to events.
 * 
 * REST APIs:
 * - POST /events: Create a new event
 * - DELETE /events/{eventId}: Delete an event by its ID
 * - GET /events: Retrieve all available events
 * 
 * @author David Vo
 * @version 2.0
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // Use Case 1: Create an Event
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventCreationDTO eventDTO) {
        EventResponseDTO createdEvent = eventService.createEventFromDTO(eventDTO);
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
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        Iterable<Event> events = eventService.getAllEvents(); // Correct type: Iterable<Event>
        List<EventResponseDTO> eventDTOs = StreamSupport.stream(events.spliterator(), false)
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }
}