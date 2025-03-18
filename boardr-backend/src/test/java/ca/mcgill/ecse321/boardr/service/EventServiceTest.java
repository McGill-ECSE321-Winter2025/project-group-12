package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.Event.EventCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventResponseDTO;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventService.
 * 
 * Methods tested:
 * createEvent,
 * createEventFromDTO,
 * deleteEvent,
 * getAllEvents
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-17
 * 
 * 
 * missing: 
 * - idk if checking for game instance's owner is important (maybe remove)
 * - event should verify that the person has the board game instance in their collection and that it is the one we want to create event for
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private BoardGameInstanceRepository boardGameInstanceRepository;
    @InjectMocks
    private EventService eventService;

    private UserAccount mockOrganizer;
    private BoardGameInstance mockGameInstance;
    private GameOwner mockGameOwner;
    private Event mockEvent;
    private EventCreationDTO mockEventDTO;

    @BeforeEach
    public void setUp() {
        // Set up mock objects for the tests
        mockOrganizer = mock(UserAccount.class);
        when(mockOrganizer.getUserAccountId()).thenReturn(1);

        mockGameOwner = mock(GameOwner.class);
        when(mockGameOwner.getId()).thenReturn(1);

        mockGameInstance = mock(BoardGameInstance.class);
        when(mockGameInstance.getindividualGameId()).thenReturn(1);
        when(mockGameInstance.getGameOwner()).thenReturn(mockGameOwner);

        mockEvent = mock(Event.class);
        when(mockEvent.getEventId()).thenReturn(1);
        when(mockEvent.getEventDate()).thenReturn(20250320);
        when(mockEvent.getEventTime()).thenReturn(1800);
        when(mockEvent.getLocation()).thenReturn("Board Game Cafe");
        when(mockEvent.getDescription()).thenReturn("Settlers of Catan Tournament");
        when(mockEvent.getmaxParticipants()).thenReturn(8);
        when(mockEvent.getboardGameInstance()).thenReturn(mockGameInstance);
        when(mockEvent.getOrganizer()).thenReturn(mockOrganizer);

        // Create mock DTO
        mockEventDTO = new EventCreationDTO(
            20250320,
            1800,
            "Board Game Cafe",
            "Settlers of Catan Tournament",
            8,
            1,
            1
        );
    }

    // Test 1: createEvent - Successful creation
    @Test
    public void testCreateEvent_Success() {
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        // Create the mock event and test if inputs are good
        Event result = eventService.createEvent(mockEvent);

        assertNotNull(result);
        assertEquals(1, result.getEventId());
        assertEquals("Board Game Cafe", result.getLocation());
        assertEquals("Settlers of Catan Tournament", result.getDescription());
        assertEquals(8, result.getmaxParticipants());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 2: createEvent - Missing Organizer
    @Test
    public void testCreateEvent_MissingOrganizer() {
        when(mockEvent.getOrganizer()).thenReturn(null);

        // Throw exception if create is unsucessful
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Organizer must be provided.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 3: createEvent - Missing Board Game Instance
    @Test
    public void testCreateEvent_MissingBoardGameInstance() {
        when(mockEvent.getboardGameInstance()).thenReturn(null);

        // throw error for missing game board instance
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("A board game instance must be provided.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 4: createEvent - Board Game Instance has no owner
    @Test
    public void testCreateEvent_BoardGameInstanceNoOwner() {
        when(mockGameInstance.getGameOwner()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Board game instance must have an owner.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 5: createEvent - Missing required fields
    @Test
    public void testCreateEvent_MissingRequiredFields() {
        // Case 1: Missing description
        when(mockEvent.getDescription()).thenReturn(null);
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("All event fields must be provided and valid.", exception1.getMessage());

        // Case 2: Missing location
        when(mockEvent.getDescription()).thenReturn("Settlers of Catan Tournament");
        when(mockEvent.getLocation()).thenReturn(null);
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("All event fields must be provided and valid.", exception2.getMessage());

        // Case 3: Invalid max participants
        when(mockEvent.getLocation()).thenReturn("Board Game Cafe");
        when(mockEvent.getmaxParticipants()).thenReturn(0);
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("All event fields must be provided and valid.", exception3.getMessage());
        
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 6: createEventFromDTO - Successful creation
    @Test
    public void testCreateEventFromDTO_Success() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        EventResponseDTO result = eventService.createEventFromDTO(mockEventDTO);

        // validate DTO parameters
        assertNotNull(result);
        assertEquals(1, result.getEventId());
        assertEquals(20250320, result.getEventDate());
        assertEquals(1800, result.getEventTime());
        assertEquals("Board Game Cafe", result.getLocation());
        assertEquals("Settlers of Catan Tournament", result.getDescription());
        assertEquals(8, result.getMaxParticipants());
        assertEquals(1, result.getBoardGameInstanceId());
        assertEquals(1, result.getOrganizerId());
        verify(userAccountRepository, times(1)).findById(1);
        verify(boardGameInstanceRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 7: createEventFromDTO - Organizer not found
    @Test
    public void testCreateEventFromDTO_OrganizerNotFound() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        // Throw error
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(mockEventDTO);
        });
        assertEquals("Organizer not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(boardGameInstanceRepository, never()).findById(anyInt());
        verify(eventRepository, never()).save(any());
    }

    // Test 8: createEventFromDTO - Board game instance not found
    @Test
    public void testCreateEventFromDTO_BoardGameInstanceNotFound() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.empty());

        // Throw error
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(mockEventDTO);
        });
        assertEquals("Board game instance not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(boardGameInstanceRepository, times(1)).findById(1);
        verify(eventRepository, never()).save(any());
    }

    // Test 9: deleteEvent - Successful deletion
    @Test
    public void testDeleteEvent_Success() {
        // Setup
        int eventId = 1;
        int userId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        eventService.deleteEvent(eventId, userId);

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).delete(mockEvent);
    }

    // Test 10: deleteEvent - Event not found
    @Test
    public void testDeleteEvent_EventNotFound() {
        // Set up
        int eventId = 1;
        int userId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Throw error
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteEvent(eventId, userId);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).delete(any());
    }

    // Test 11: deleteEvent - User not the organizer
    @Test
    public void testDeleteEvent_UserNotOrganizer() {
        int eventId = 1;
        int userId = 2; // Different from organizer's ID
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        // Throw error
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteEvent(eventId, userId);
        });
        assertEquals("Only the organizer can delete the event.", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).delete(any());
    }

    // Test 12: getAllEvents - Successful retrieval
    @Test
    public void testGetAllEvents_Success() {
        // Setup
        List<Event> events = Arrays.asList(mockEvent);
        when(eventRepository.findAll()).thenReturn(events);

        Iterable<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(1, ((List<Event>) result).size());
        verify(eventRepository, times(1)).findAll();
    }

    // Test 13: getAllEvents - Empty list
    @Test
    public void testGetAllEvents_Empty() {
        when(eventRepository.findAll()).thenReturn(new ArrayList<>());

        Iterable<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
        verify(eventRepository, times(1)).findAll();
    }

    // More tests
    // Ownership Validation FAILED but need to change
    // @Test
    // public void testCreateEvent_OrganizerDoesNotOwnGameInstance() {
    //     when(mockGameOwner.getId()).thenReturn(2); // Different from organizer's ID (1)
    //     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
    //         eventService.createEvent(mockEvent);
    //     });
    //     assertEquals("Organizer must own the board game instance.", exception.getMessage());
    //     verify(eventRepository, never()).save(any(Event.class));
    // }

    // FAILED but need to change
    // @Test
    // public void testCreateEventFromDTO_OrganizerDoesNotOwnGameInstance() {
    //     when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
    //     when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
    //     when(mockGameOwner.getId()).thenReturn(2); // Different from organizer's ID (1)
    //     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
    //         eventService.createEventFromDTO(mockEventDTO);
    //     });
    //     assertEquals("Organizer must own the board game instance.", exception.getMessage());
    //     verify(userAccountRepository, times(1)).findById(1);
    //     verify(boardGameInstanceRepository, times(1)).findById(1);
    //     verify(eventRepository, never()).save(any());
    // }

    // Date and Time Validation FAILED
    @Test
    public void testCreateEvent_InvalidDate() {
        when(mockEvent.getEventDate()).thenReturn(-20250320); // Negative date
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Event date must be valid.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // FAILED
    @Test
    public void testCreateEvent_InvalidTime() {
        when(mockEvent.getEventTime()).thenReturn(2500); // Beyond 2359
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Event time must be between 0000 and 2359.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // FAILED
    @Test
    public void testCreateEventFromDTO_InvalidDate() {
        EventCreationDTO invalidDateDTO = new EventCreationDTO(
            -20250320, 1800, "Board Game Cafe", "Settlers of Catan Tournament", 8, 1, 1
        );
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(invalidDateDTO);
        });
        assertEquals("Event date must be valid.", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    // FAILED
    @Test
    public void testCreateEventFromDTO_InvalidTime() {
        EventCreationDTO invalidTimeDTO = new EventCreationDTO(
            20250320, -1800, "Board Game Cafe", "Settlers of Catan Tournament", 8, 1, 1
        );
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(invalidTimeDTO);
        });
        assertEquals("Event time must be between 0000 and 2359.", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    // Max Participants Edge Cases FAILED
    @Test
    public void testCreateEvent_NegativeMaxParticipants() {
        when(mockEvent.getmaxParticipants()).thenReturn(-1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Max participants must be greater than zero.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // not really useful
    // @Test
    // public void testCreateEvent_UnreasonablyLargeMaxParticipants() {
    //     when(mockEvent.getmaxParticipants()).thenReturn(1000000); // Arbitrary large number
    //     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
    //         eventService.createEvent(mockEvent);
    //     });
    //     assertEquals("Max participants must be reasonable (less than 1000).", exception.getMessage());
    //     verify(eventRepository, never()).save(any(Event.class));
    // }

    // FAILED
    @Test
    public void testCreateEventFromDTO_NegativeMaxParticipants() {
        EventCreationDTO invalidMaxDTO = new EventCreationDTO(
            20250320, 1800, "Board Game Cafe", "Settlers of Catan Tournament", -1, 1, 1
        );
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(invalidMaxDTO);
        });
        assertEquals("Max participants must be greater than zero.", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    // Not really useful
    // @Test
    // public void testCreateEventFromDTO_UnreasonablyLargeMaxParticipants() {
    //     EventCreationDTO invalidMaxDTO = new EventCreationDTO(
    //         20250320, 1800, "Board Game Cafe", "Settlers of Catan Tournament", 1000000, 1, 1
    //     );
    //     when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
    //     when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
    //     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
    //         eventService.createEventFromDTO(invalidMaxDTO);
    //     });
    //     assertEquals("Max participants must be reasonable (less than 1000).", exception.getMessage());
    //     verify(eventRepository, never()).save(any());
    // }
}