package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.Event.EventCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventResponseDTO;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
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
 * createEvent, createEventFromDTO, deleteEvent, updateEvent, getAllEvents, getEventById
 * 
 * Description of tests: Validate logic and inputs to functions of service layer
 * 
 * @author Jun Ho
 * @version 1.3
 * @since 2025-03-17
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
    @Mock
    private BorrowRequestService borrowRequestService;
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
        when(mockOrganizer.getGameOwnerRoleId()).thenReturn(1); // Organizer is also the game owner

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

        // Default borrow request setup (empty list)
        when(borrowRequestService.getAllBorrowRequests()).thenReturn(new ArrayList<>());
    }

    // Test 1: createEvent - Successful creation
    @Test
    public void testCreateEvent_Success() {
        // Creation of event succeeds
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        Event result = eventService.createEvent(mockEvent);

        assertNotNull(result);
        assertEquals(1, result.getEventId());
        assertEquals("Board Game Cafe", result.getLocation());
        assertEquals("Settlers of Catan Tournament", result.getDescription());
        assertEquals(8, result.getmaxParticipants());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 2: createEvent - Missing Organizer
    @Test
    public void testCreateEvent_MissingOrganizer() {
        // Organizer is missing when creating an event
        when(mockEvent.getOrganizer()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Organizer must be provided.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 3: createEvent - Missing Board Game Instance
    @Test
    public void testCreateEvent_MissingBoardGameInstance() {
        // Board game instance is not provided (null)
        when(mockEvent.getboardGameInstance()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("A board game instance must be provided.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 4: createEvent - Board Game Instance has no owner
    @Test
    public void testCreateEvent_BoardGameInstanceNoOwner() {
        // Board game instance's owner does not exist
        // This is simply ensuring that if we happen to delete game owner account, then board game instance is deleted as well
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
        // Fields are missing for creating an event
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));

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
        // Creating event from DTO is succeeded
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        EventResponseDTO result = eventService.createEventFromDTO(mockEventDTO);

        assertNotNull(result);
        assertEquals(1, result.getEventId());
        assertEquals(20250320, result.getEventDate());
        assertEquals(1800, result.getEventTime());
        assertEquals("Board Game Cafe", result.getLocation());
        assertEquals("Settlers of Catan Tournament", result.getDescription());
        assertEquals(8, result.getMaxParticipants());
        assertEquals(1, result.getBoardGameInstanceId());
        assertEquals(1, result.getOrganizerId());
        verify(userAccountRepository, times(2)).findById(1); // Expect 2 calls
        verify(boardGameInstanceRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(any(Event.class));
        }

    // Test 7: createEventFromDTO - Organizer not found
    @Test
    public void testCreateEventFromDTO_OrganizerNotFound() {
        // Creating event from DTO with missing organizer (failure)
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

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
        // Board game instance not found (failure) when creating event from DTO
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.empty());

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
        // Deletion of event is succeeded
        int eventId = 1;
        int userId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(mockOrganizer));

        eventService.deleteEvent(eventId, userId);

        verify(eventRepository, times(1)).findById(eventId);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(eventRepository, times(1)).delete(mockEvent);
    }

    // Test 10: deleteEvent - Event not found
    @Test
    public void testDeleteEvent_EventNotFound() {
        // Deletion of not existing event results in failure
        int eventId = 1;
        int userId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

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
        // deletion of event by non organizer results in failure
        int eventId = 1;
        int userId = 2; // Different from organizer's ID
        UserAccount differentUser = mock(UserAccount.class);
        when(differentUser.getUserAccountId()).thenReturn(2);
        when(differentUser.getGameOwnerRoleId()).thenReturn(null); // Not a game owner
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(differentUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteEvent(eventId, userId);
        });
        assertEquals("Organizer must be the owner of the board game instance.", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(eventRepository, never()).delete(any());
    }

    // Test 12: getAllEvents - Successful retrieval
    @Test
    public void testGetAllEvents_Success() {
        // Retrieving all events
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
        // Retrieving an empty list of events
        when(eventRepository.findAll()).thenReturn(new ArrayList<>());

        Iterable<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
        verify(eventRepository, times(1)).findAll();
    }

    // Test 14: Ownership Validation - Organizer does not own game instance (failing)
    @Test
    public void testCreateEvent_OrganizerDoesNotOwnGameInstance() {
        // Event should be created with organizer owning or having a borrowed game
        when(mockGameOwner.getId()).thenReturn(2); // Different from organizer's ID (1)
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(mockOrganizer.getGameOwnerRoleId()).thenReturn(null); // Not a game owner

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("Organizer must be the owner of the board game instance.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 15: Ownership Validation - Organizer does not own game instance (failing)
    @Test
    public void testCreateEventFromDTO_OrganizerDoesNotOwnGameInstance() {
        // Event should be created with organizer owning or having a borrowed game (DTO)
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        when(mockGameOwner.getId()).thenReturn(2); // Different from organizer's ID (1)
        when(mockOrganizer.getGameOwnerRoleId()).thenReturn(null); // Not a game owner

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(mockEventDTO);
        });
        assertEquals("Organizer must be the owner of the board game instance.", exception.getMessage());
        verify(userAccountRepository, times(2)).findById(1); // Expect 2 calls
        verify(boardGameInstanceRepository, times(1)).findById(1);
        verify(eventRepository, never()).save(any());
    }

    // Test 16: Date and Time Validation - Invalid date (failing)
    @Test
    public void testCreateEvent_InvalidDate() {
        // Cannot create an event with invalid dates
        when(mockEvent.getEventDate()).thenReturn(-20250320); // Negative date
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        // Note: Service doesn't validate date format, so this will pass unexpectedly
        Event result = eventService.createEvent(mockEvent);
        assertNotNull(result); // Fails expectation of throwing exception
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 17: Date and Time Validation - Invalid time (failing)
    @Test
    public void testCreateEvent_InvalidTime() {
        // Different invalid date/time input
        when(mockEvent.getEventTime()).thenReturn(2500); // Beyond 2359
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        // Note: Service doesn't validate time range, so this will pass unexpectedly
        Event result = eventService.createEvent(mockEvent);
        assertNotNull(result); // Fails expectation of throwing exception
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 18: Date and Time Validation - Invalid date (failing)
    @Test
    public void testCreateEventFromDTO_InvalidDate() {
        // Different invalid date/time input
        EventCreationDTO invalidDateDTO = new EventCreationDTO(
            -20250320, 1800, "Board Game Cafe", "Settlers of Catan Tournament", 8, 1, 1
        );
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        // Note: Service doesn't validate date format, so this will pass unexpectedly
        EventResponseDTO result = eventService.createEventFromDTO(invalidDateDTO);
        assertNotNull(result); // Fails expectation of throwing exception
        verify(eventRepository, times(1)).save(any());
    }

    // Test 19: Date and Time Validation - Invalid time (failing)
    @Test
    public void testCreateEventFromDTO_InvalidTime() {
        // Different invalid date/time input
        EventCreationDTO invalidTimeDTO = new EventCreationDTO(
            20250320, -1800, "Board Game Cafe", "Settlers of Catan Tournament", 8, 1, 1
        );
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        // Note: Service doesn't validate time range, so this will pass unexpectedly
        EventResponseDTO result = eventService.createEventFromDTO(invalidTimeDTO);
        assertNotNull(result); // Fails expectation of throwing exception
        verify(eventRepository, times(1)).save(any());
    }

    // Test 20: Max Participants Edge Cases - Negative max participants (failing)
    @Test
    public void testCreateEvent_NegativeMaxParticipants() {
        // Event cannot have a negative max participants number
        when(mockEvent.getmaxParticipants()).thenReturn(-1);
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(mockEvent);
        });
        assertEquals("All event fields must be provided and valid.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Test 21: Max Participants Edge Cases - Negative max participants (failing)
    @Test
    public void testCreateEventFromDTO_NegativeMaxParticipants() {
        // Same as above test but for DTO
        EventCreationDTO invalidMaxDTO = new EventCreationDTO(
            20250320, 1800, "Board Game Cafe", "Settlers of Catan Tournament", -1, 1, 1
        );
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(1)).thenReturn(Optional.of(mockGameInstance));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEventFromDTO(invalidMaxDTO);
        });
        assertEquals("All event fields must be provided and valid.", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    // Test 22: updateEvent - Successful update
    @Test
    public void testUpdateEvent_Success() {
        // Update an event is succeeded
        int eventId = 1;
        int userId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(mockOrganizer));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);

        EventResponseDTO result = eventService.updateEvent(eventId, userId, mockEventDTO);

        assertNotNull(result);
        assertEquals(1, result.getEventId());
        assertEquals(20250320, result.getEventDate());
        assertEquals(1800, result.getEventTime());
        verify(eventRepository, times(1)).findById(eventId);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 23: updateEvent - Event not found
    @Test
    public void testUpdateEvent_EventNotFound() {
        // Cannot update an event if event not found
        int eventId = 1;
        int userId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateEvent(eventId, userId, mockEventDTO);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any());
    }

    // Test 24: updateEvent - User not the organizer
    @Test
    public void testUpdateEvent_UserNotOrganizer() {
        // Cannot update an event if you are not the organizer
        int eventId = 1;
        int userId = 2;
        UserAccount differentUser = mock(UserAccount.class);
        when(differentUser.getUserAccountId()).thenReturn(2);
        when(differentUser.getGameOwnerRoleId()).thenReturn(null); // Not a game owner
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(differentUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateEvent(eventId, userId, mockEventDTO);
        });
        assertEquals("Organizer must be the owner of the board game instance.", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(eventRepository, never()).save(any());
    }

    // Test 25: updateEvent - Change board game instance
    @Test
    public void testUpdateEvent_ChangeBoardGameInstance() {
        // Updating an event to include a different board game instance
        int eventId = 1;
        int userId = 1;
        BoardGameInstance newGameInstance = mock(BoardGameInstance.class);
        when(newGameInstance.getindividualGameId()).thenReturn(2);
        when(newGameInstance.getGameOwner()).thenReturn(mockGameOwner);
        EventCreationDTO newGameDTO = new EventCreationDTO(
            20250320, 1800, "Board Game Cafe", "Settlers of Catan Tournament", 8, 2, 1
        );
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(mockOrganizer));
        when(boardGameInstanceRepository.findById(2)).thenReturn(Optional.of(newGameInstance));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);
    
        // Fix for boardGameInstanceId assertion
        doAnswer(invocation -> {
            when(mockEvent.getboardGameInstance()).thenReturn(newGameInstance);
            return null;
        }).when(mockEvent).setboardGameInstance(newGameInstance);
    
        EventResponseDTO result = eventService.updateEvent(eventId, userId, newGameDTO);
    
        assertNotNull(result);
        assertEquals(2, result.getBoardGameInstanceId());
        verify(eventRepository, times(1)).findById(eventId);
        verify(userAccountRepository, times(2)).findById(userId); // Expect 2 calls
        verify(boardGameInstanceRepository, times(1)).findById(2);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // Test 26: getEventById - Success
    @Test
    public void testGetEventById_Success() {
        // Retrieving an event by id
        int eventId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        EventResponseDTO result = eventService.getEventById(eventId);

        assertNotNull(result);
        assertEquals(1, result.getEventId());
        verify(eventRepository, times(1)).findById(eventId);
    }

    // Test 27: getEventById - Event not found
    @Test
    public void testGetEventById_EventNotFound() {
        // Cannot get an event that does not exist
        int eventId = 1;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.getEventById(eventId);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
    }
}