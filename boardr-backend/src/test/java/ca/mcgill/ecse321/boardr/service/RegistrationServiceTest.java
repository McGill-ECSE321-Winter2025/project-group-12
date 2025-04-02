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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
 * createRegistration, getAllRegistrations, getRegistration, updateRegistration, deleteRegistration, cancelRegistration
 * 
 * Description of tests: Validate logic and inputs to functions of service layer
 * 
 * @author Jun Ho
 * @version 1.3
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
        when(mockEvent.getEventDate()).thenReturn(20300325); // Future date: 2030-03-25

        // Set up registration key
        mockRegistrationKey = mock(Registration.RegistrationKey.class);
        when(mockRegistrationKey.getRegistrant()).thenReturn(mockUser);
        when(mockRegistrationKey.getEvent()).thenReturn(mockEvent);

        // Create mock registration
        mockRegistration = mock(Registration.class);
        registrationDate = Date.valueOf(LocalDate.now());
        when(mockRegistration.getRegistrationKey()).thenReturn(mockRegistrationKey);
        when(mockRegistration.getRegistrationDate()).thenReturn(registrationDate);

        // Create mock DTO
        mockRegistrationDTO = new RegistrationCreationDTO(1, 1);

        // Setup default empty registration list for event
        List<Registration> emptyRegistrations = new ArrayList<>();
        when(mockEvent.getRegistrations()).thenReturn(emptyRegistrations);

        // Default organizer setup
        UserAccount mockOrganizer = mock(UserAccount.class);
        when(mockOrganizer.getUserAccountId()).thenReturn(2); // Different from registrant
        when(mockEvent.getOrganizer()).thenReturn(mockOrganizer);
    }

    // Test 1: createRegistration - Successful registration
    @Test
    public void testCreateRegistration_Success() {
        // Registration created successfully
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        RegistrationResponseDTO result = registrationService.createRegistration(mockRegistrationDTO);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getEventId());
        assertEquals(registrationDate, result.getRegistrationDate());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    // Test 2: createRegistration - User not found
    @Test
    public void testCreateRegistration_UserNotFound() {
        // Cannot register without existing user
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(mockRegistrationDTO);
        });
        assertEquals("User not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).findById(anyInt());
        verify(registrationRepository, never()).findById(any());
        verify(registrationRepository, never()).save(any());
    }

    // Test 3: createRegistration - Event not found
    @Test
    public void testCreateRegistration_EventNotFound() {
        // Cannot register to event if event is not found
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(mockRegistrationDTO);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, never()).findById(any());
        verify(registrationRepository, never()).save(any());
    }

    // Test 4: createRegistration - Already registered
    @Test
    public void testCreateRegistration_AlreadyRegistered() {
        // Cannot register to event if already registered to it
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(mockRegistrationDTO);
        });
        assertEquals("User is already registered for this event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 5: createRegistration - Event fully booked
    @Test
    public void testCreateRegistration_EventFullyBooked() {
        // Cannot register to event if fully booked
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());

        List<Registration> fullRegistrations = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            fullRegistrations.add(mock(Registration.class));
        }
        when(mockEvent.getRegistrations()).thenReturn(fullRegistrations);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(mockRegistrationDTO);
        });
        assertEquals("Event is fully booked.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 6: createRegistration - Past event
    @Test
    public void testCreateRegistration_PastEvent() {
        // Cannot register to event if dates/time is passed
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(mockEvent.getEventDate()).thenReturn(20230325); // Past date: 2023-03-25

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(mockRegistrationDTO);
        });
        assertEquals("Cannot register for a past event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 7: createRegistration - Organizer self-registration
    @Test
    public void testCreateRegistration_OrganizerSelfRegistration() {
        // Cannot register to the same event you organized
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());
        when(mockEvent.getOrganizer()).thenReturn(mockUser); // Same as registrant

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(mockRegistrationDTO);
        });
        assertEquals("Organizer cannot register for their own event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 8: getAllRegistrations - Success
    @Test
    public void testGetAllRegistrations_Success() {
        // Getting all registration
        List<Registration> registrations = Arrays.asList(mockRegistration);
        when(registrationRepository.findAll()).thenReturn(registrations);

        List<RegistrationResponseDTO> result = registrationService.getAllRegistrations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals(1, result.get(0).getEventId());
        verify(registrationRepository, times(1)).findAll();
    }

    // Test 9: getAllRegistrations - Empty list
    @Test
    public void testGetAllRegistrations_Empty() {
        // Getting an empty list of registrations
        when(registrationRepository.findAll()).thenReturn(new ArrayList<>());

        List<RegistrationResponseDTO> result = registrationService.getAllRegistrations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(registrationRepository, times(1)).findAll();
    }

    // Test 10: getRegistration - Success
    @Test
    public void testGetRegistration_Success() {
        // Getting a registration succeeded
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));

        RegistrationResponseDTO result = registrationService.getRegistration(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getEventId());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
    }

    // Test 11: getRegistration - User not found
    @Test
    public void testGetRegistration_UserNotFound() {
        // Cannot registration for an unexisting user
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.getRegistration(1, 1);
        });
        assertEquals("User not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, never()).findById(anyInt());
    }

    // Test 12: getRegistration - Event not found
    @Test
    public void testGetRegistration_EventNotFound() {
        // Cannot get registration for unexisting event
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.getRegistration(1, 1);
        });
        assertEquals("Event not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
    }

    // Test 13: getRegistration - Registration not found
    @Test
    public void testGetRegistration_RegistrationNotFound() {
        // Cannot get an unexisting registration
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.getRegistration(1, 1);
        });
        assertEquals("Registration not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
    }

    // Test 14: updateRegistration - Success
    @Test
    public void testUpdateRegistration_Success() {
        // Updating registration success
        Date newDate = Date.valueOf(LocalDate.of(2030, 3, 20));
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        RegistrationResponseDTO result = registrationService.updateRegistration(1, 1, newDate);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getEventId());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    // Test 15: updateRegistration - Past event
    @Test
    public void testUpdateRegistration_PastEvent() {
        // Cannot update registration for a passed event (finished event)
        Date newDate = Date.valueOf(LocalDate.of(2023, 3, 20));
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));
        when(mockEvent.getEventDate()).thenReturn(20230325); // Past date

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.updateRegistration(1, 1, newDate);
        });
        assertEquals("Cannot update registration for a past event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 16: updateRegistration - Date after event
    @Test
    public void testUpdateRegistration_DateAfterEvent() {
        // Cannot update registration if event is passed
        Date newDate = Date.valueOf(LocalDate.of(2030, 3, 26)); // After event date 2030-03-25
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.updateRegistration(1, 1, newDate);
        });
        assertEquals("Registration date cannot be after the event date.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).save(any());
    }

    // Test 17: deleteRegistration - Success
    @Test
    public void testDeleteRegistration_Success() {
        // Deleting registration successful
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));

        registrationService.deleteRegistration(1, 1);

        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, times(1)).delete(mockRegistration);
    }

    // Test 18: deleteRegistration - Registration not found
    @Test
    public void testDeleteRegistration_RegistrationNotFound() {
        // Cannot delete registration if registration not found
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.deleteRegistration(1, 1);
        });
        assertEquals("Registration not found.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).delete(any());
    }

    // Test 19: cancelRegistration - Success
    @Test
    public void testCancelRegistration_Success() {
        // Canceling a registration is success
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));

        registrationService.cancelRegistration(1, 1);

        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, times(1)).delete(mockRegistration);
    }

    // Test 20: cancelRegistration - Past event
    @Test
    public void testCancelRegistration_PastEvent() {
        // Cannot cancel registration for passed event
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(1)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.findById(any(Registration.RegistrationKey.class))).thenReturn(Optional.of(mockRegistration));
        when(mockEvent.getEventDate()).thenReturn(20230325); // Past date

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.cancelRegistration(1, 1);
        });
        assertEquals("Cannot cancel registration for a past event.", exception.getMessage());
        verify(userAccountRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).findById(1);
        verify(registrationRepository, times(1)).findById(any(Registration.RegistrationKey.class));
        verify(registrationRepository, never()).delete(any());
    }
}