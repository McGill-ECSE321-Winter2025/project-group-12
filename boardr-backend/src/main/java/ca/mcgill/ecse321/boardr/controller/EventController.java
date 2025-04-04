package ca.mcgill.ecse321.boardr.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardr.dto.ErrorDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventResponseDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventUpdateDTO;

import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.service.EventService;

/**
 * REST controller class for managing events in the Boardr application.
 * Provides endpoints for creating, updating, deleting, and retrieving events.
 * This class interacts with the EventService to perform operations related to events.
 * 
 * REST APIs:
 * - POST /events: Create a new event (user must be game owner or approved borrower)
 * - PUT /events/{eventId}: Update an existing event (user must be organizer and authorized for new board game instance)
 * - DELETE /events/{eventId}: Delete an event by its ID (user must be organizer)
 * - GET /events: Retrieve all available events
 * - GET /events/{eventId}: Retrieve a specific event by ID
 * 
 * @author David Vo
 * @version 2.2
 * @since 2025-03-18
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

    // Use Case 3: Update an Event
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable int eventId,
            @RequestParam int userId,
            @RequestBody EventCreationDTO eventDTO) {
        EventResponseDTO updatedEvent = eventService.updateEvent(eventId, userId, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    // Use Case 6: Display All Available Events
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        Iterable<Event> events = eventService.getAllEvents();
        List<EventResponseDTO> eventDTOs = StreamSupport.stream(events.spliterator(), false)
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable int eventId) {
        EventResponseDTO event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }
    
    // Exception handler for IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    // Exception handler for general exceptions (optional fallback)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDTO> handleGeneralException(Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO("An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/boardgame/{gameId}")
    public ResponseEntity<List<EventDTO>> getEventsByBoardGameId(@PathVariable int gameId) {
        List<EventDTO> eventDTOs = eventService.getEventsByBoardGameId(gameId);
        return ResponseEntity.ok(eventDTOs);
    }


    // Use Case: Update Event Details (only date, time, location, description)
    @PutMapping("/{eventId}/details")
    public ResponseEntity<EventResponseDTO> updateEventDetails(
        @PathVariable int eventId,
        @RequestBody EventUpdateDTO eventUpdateDTO) {
    EventResponseDTO updatedEvent = eventService.updateEventDetails(eventId, eventUpdateDTO);
    return ResponseEntity.ok(updatedEvent);
}

}