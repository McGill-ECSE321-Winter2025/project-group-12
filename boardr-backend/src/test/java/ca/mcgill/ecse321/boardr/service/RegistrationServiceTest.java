package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationResponseDTO;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.Registration;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationService.
 * 
 * Methods tested:
 * registerForEvent,
 * registerForEventDTO
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-17
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private UserAccount mockUser;
    private Event mockEvent;
    private Registration mockRegistration;
    private Registration.RegistrationKey mockRegistrationKey;
    private RegistrationCreationDTO mockRegistrationDTO;
    private Date registrationDate;

    @BeforeEach
    public void setUp() {
        // Set up mock objects
        mockUser = mock(UserAccount.class);
        when(mockUser.getUserAccountId()).thenReturn(1);

        mockEvent = mock(Event.class);
        when(mockEvent.getEventId()).thenReturn(1);
        when(mockEvent.getmaxParticipants()).thenReturn(8);
        
        // Set up registration key
        mockRegistrationKey = mock(Registration.RegistrationKey.class);
        when(mockRegistrationKey.getRegistrant()).thenReturn(mockUser);
        when(mockRegistrationKey.getEvent()).thenReturn(mockEvent);
        
        // Create mock registration
        mockRegistration = mock(Registration.class);
        registrationDate = new Date(System.currentTimeMillis());
        when(mockRegistration.getRegistrationKey()).thenReturn(mockRegistrationKey);
        when(mockRegistration.getRegistrationDate()).thenReturn(registrationDate);
        
        // Create mock DTO
        mockRegistrationDTO = new RegistrationCreationDTO(1, 1);
        
        // Setup default empty registration list for event
        List<Registration> emptyRegistrations = new ArrayList<>();
        when(mockEvent.getRegistrations()).thenReturn(emptyRegistrations);
    }

    // Test 1: registerForEvent - Successful registration
    @Test
    public void testRegisterForEvent_Success() {
        // Setup
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        Registration result = registrationService.registerForEvent(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result.getRegistrationKey().getRegistrant());
        assertEquals(mockEvent, result.getRegistrationKey().getEvent());
        assertEquals(registrationDate, result.getRegistrationDate());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    // Test 2: registerForEvent - User not found
    @Test
    public void testRegisterForEvent_UserNotFound() {
        // Setup
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("User not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).findById(any());
        verify(registrationRepository, never()).save(any());
    }

    // Test 3: registerForEvent - Event not found
    @Test
    public void testRegisterForEvent_EventNotFound() {
        // Setup
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, never()).findById(any());
        verify(registrationRepository, never()).save(any());
    }

    // Test 4: registerForEvent - Already registered
    @Test
    public void testRegisterForEvent_AlreadyRegistered() {
        // Setup
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("User is already registered for this event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 5: registerForEvent - Event fully booked
    @Test
    public void testRegisterForEvent_EventFullyBooked() {
        // Setup
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        
        // Create a list of mock registrations that equals maxParticipants
        List<Registration> fullRegistrations = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            fullRegistrations.add(mock(Registration.class));
        }
        when(mockEvent.getRegistrations()).thenReturn(fullRegistrations);

        // Throw error when registering for a booked event
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("Event is fully booked.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 6: registerForEventDTO - Successful registration
    @Test
    public void testRegisterForEventDTO_Success() {
        // Setup
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        RegistrationResponseDTO result = registrationService.registerForEventDTO(mockRegistrationDTO);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getEventId());
        assertEquals(registrationDate, result.getRegistrationDate());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    // Test 7: registerForEventDTO - Validation failure (propagated from registerForEvent)
    @Test
    public void testRegisterForEventDTO_ValidationFailure() {
        // Setup - Event is fully booked
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        
        // Create a list of mock registrations that equals maxParticipants
        List<Registration> fullRegistrations = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            fullRegistrations.add(mock(Registration.class));
        }
        when(mockEvent.getRegistrations()).thenReturn(fullRegistrations);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEventDTO(mockRegistrationDTO);
        });
        assertEquals("Event is fully booked.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // New Tests for Edge Cases and Validation

    // Invalid Input Validation
    @Test
    public void testRegisterForEvent_NegativeUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(-1, 1);
        });
        assertEquals("User ID must be positive.", exception.getMessage());
        verify(userAccountRepository, never()).findById(anyInt());
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).save(any());
    }

    @Test
    public void testRegisterForEvent_NegativeEventId() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, -1);
        });
        assertEquals("Event ID must be positive.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).save(any());
    }

    @Test
    public void testRegisterForEventDTO_NegativeUserId() {
        RegistrationCreationDTO invalidDTO = new RegistrationCreationDTO(-1, 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEventDTO(invalidDTO);
        });
        assertEquals("User ID must be positive.", exception.getMessage());
        verify(userAccountRepository, never()).findById(anyInt());
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).save(any());
    }

    @Test
    public void testRegisterForEventDTO_NegativeEventId() {
        RegistrationCreationDTO invalidDTO = new RegistrationCreationDTO(1, -1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEventDTO(invalidDTO);
        });
        assertEquals("Event ID must be positive.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).save(any());
    }

    // Registration Date Validation (assuming event date is future)
    @Test
    public void testRegisterForEvent_PastRegistrationDate() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(mockEvent.getEventDate()).thenReturn(20250320); // Future date
        when(mockRegistration.getRegistrationDate()).thenReturn(new Date(0)); // 1970-01-01
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("Registration date cannot be in the past.", exception.getMessage());
        verify(registrationRepository, never()).save(any());
    }

    // Organizer Registering for Own Event
    @Test
    public void testRegisterForEvent_OrganizerSelfRegistration() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(mockEvent.getOrganizer()).thenReturn(mockUser); // Organizer is the same as registrant

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("Organizer cannot register for their own event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Exceeding Max Participants
    @Test
    public void testRegisterForEvent_ExceedsMaxParticipants() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        List<Registration> overRegistrations = new ArrayList<>();
        for (int i = 0; i < 9; i++) { // One more than maxParticipants (8)
            overRegistrations.add(mock(Registration.class));
        }
        when(mockEvent.getRegistrations()).thenReturn(overRegistrations);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1, 1);
        });
        assertEquals("Event is fully booked.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Additional DTO-Specific Tests
    @Test
    public void testRegisterForEventDTO_UserNotFound() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEventDTO(mockRegistrationDTO);
        });
        assertEquals("User not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).save(any());
    }

    @Test
    public void testRegisterForEventDTO_EventNotFound() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEventDTO(mockRegistrationDTO);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, never()).save(any());
    }
}
