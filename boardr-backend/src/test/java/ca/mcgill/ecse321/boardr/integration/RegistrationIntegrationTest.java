package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationResponseDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrExceptionHandler;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.model.Registration.RegistrationKey;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.service.RegistrationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the Registration API endpoints.
 * These tests verify that the RegistrationController correctly interacts with the RegistrationService
 * and returns the expected responses.
* 
 * @author David Zhou
 * @date 2025-03-19
 */
@SpringBootTest(classes = ca.mcgill.ecse321.boardr.BoardrApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(BoardrExceptionHandler.class) // Import the exception handler
public class RegistrationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private RegistrationRepository registrationRepository;

    @MockBean
    private RegistrationService registrationService;

    private UserAccount testUser;
    private Event testEvent;
    private Registration testRegistration;
    private RegistrationKey testRegistrationKey;
    private RegistrationResponseDTO testResponseDTO;

    private static final int TEST_EVENT_DATE = 20250312;
    private static final int TEST_EVENT_TIME = 1800;
    private static final String TEST_LOCATION = "Test Location";
    private static final String TEST_DESCRIPTION = "Test Event Description";
    private static final int TEST_MAX_PARTICIPANTS = 10;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_EVENT_ID = 1;
    private static final Date TEST_REGISTRATION_DATE = Date.valueOf(LocalDate.now());

/**
     * Sets up the test environment before each test.
     * Initializes mock data for a user, event, and registration.
     * Configures the mocked repositories and services to return the expected data.
     */
    @BeforeEach
    public void setUp() {
        // Set up the MockMvc instance with the exception handler
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();

        // Create test user with constructor
        testUser = new UserAccount("Test User", "test@example.com", "password123");

        try {
            java.lang.reflect.Field field = UserAccount.class.getDeclaredField("userAccountId");
            field.setAccessible(true);
            field.set(testUser, TEST_USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create board game
        BoardGame testBoardGame = new BoardGame();
        try {
            java.lang.reflect.Field nameField = BoardGame.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(testBoardGame, "Test Game");

            java.lang.reflect.Field descField = BoardGame.class.getDeclaredField("description");
            descField.setAccessible(true);
            descField.set(testBoardGame, "A test board game");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find or create GameOwner role
        GameOwner gameOwner = null;
        for (UserRole role : testUser.getUserRole()) {
            if (role instanceof GameOwner) {
                gameOwner = (GameOwner) role;
                break;
            }
        }
        if (gameOwner == null) {
            gameOwner = new GameOwner(testUser);
        }

        // Create board game instance with constructor
        BoardGameInstance testBoardGameInstance = new BoardGameInstance(testBoardGame, gameOwner, "Good");

        // Create test event with constructor
        testEvent = new Event(
            TEST_EVENT_DATE,
            TEST_EVENT_TIME,
            TEST_LOCATION,
            TEST_DESCRIPTION,
            TEST_MAX_PARTICIPANTS,
            testBoardGameInstance,
            testUser
        );

        // Set ID for test event
        try {
            java.lang.reflect.Field field = Event.class.getDeclaredField("eventId");
            field.setAccessible(true);
            field.set(testEvent, TEST_EVENT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ensure registrations list is initialized
        if (testEvent.getRegistrations() == null) {
            try {
                java.lang.reflect.Field field = Event.class.getDeclaredField("registrations");
                field.setAccessible(true);
                field.set(testEvent, new ArrayList<Registration>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create test registration key
        testRegistrationKey = new RegistrationKey(testUser, testEvent);

        // Create test registration with constructor
        testRegistration = new Registration(testRegistrationKey);

        // Set registration date
        try {
            java.lang.reflect.Field field = Registration.class.getDeclaredField("registrationDate");
            field.setAccessible(true);
            field.set(testRegistration, TEST_REGISTRATION_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create DTO response using the registration instance
        testResponseDTO = new RegistrationResponseDTO(testRegistration);

        // Reset mocks
        reset(registrationService);

        // Mock service behaviors for happy path
        when(registrationService.createRegistration(any(RegistrationCreationDTO.class))).thenReturn(testResponseDTO);
    }

    /**
     * Test the POST /registrations endpoint for registering for an event.
     * Verifies that the registration is created successfully and the response contains
     * the correct registration data.
     */
    @Test
    public void testRegisterForEvent() throws Exception {
        // Arrange
        RegistrationCreationDTO registrationDTO = new RegistrationCreationDTO(
                TEST_USER_ID,
                TEST_EVENT_ID
        );

        String requestJson = objectMapper.writeValueAsString(registrationDTO);

        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        RegistrationResponseDTO responseDTO = objectMapper.readValue(responseJson, RegistrationResponseDTO.class);

        assertEquals(TEST_USER_ID, responseDTO.getUserId());
        assertEquals(TEST_EVENT_ID, responseDTO.getEventId());
        assertNotNull(responseDTO.getRegistrationDate());

        // Verify the service was called
        verify(registrationService).createRegistration(any(RegistrationCreationDTO.class));
    }

    /**
     * Test the POST /registrations endpoint when the user ID is invalid.
     * Verifies that a bad request response is returned when the user does not exist.
     */
    @Test
    public void testRegisterForEventWithInvalidUserId() throws Exception {
        // Arrange
        RegistrationCreationDTO registrationDTO = new RegistrationCreationDTO(
                -1, // Invalid user ID
                TEST_EVENT_ID
        );

        when(registrationService.createRegistration(any(RegistrationCreationDTO.class)))
                .thenThrow(new IllegalArgumentException("User not found."));

        String requestJson = objectMapper.writeValueAsString(registrationDTO);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("User not found."));

        // Verify the service was called
        verify(registrationService).createRegistration(any(RegistrationCreationDTO.class));
    }

    /**
     * Test the POST /registrations endpoint when the event ID is invalid.
     * Verifies that a bad request response is returned when the event does not exist.
     */
    @Test
    public void testRegisterForEventWithInvalidEventId() throws Exception {
        // Arrange
        RegistrationCreationDTO registrationDTO = new RegistrationCreationDTO(
                TEST_USER_ID,
                -1 // Invalid event ID
        );

        when(registrationService.createRegistration(any(RegistrationCreationDTO.class)))
                .thenThrow(new IllegalArgumentException("Event not found."));

        String requestJson = objectMapper.writeValueAsString(registrationDTO);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("Event not found."));

        // Verify the service was called
        verify(registrationService).createRegistration(any(RegistrationCreationDTO.class));
    }

    /**
     * Test the POST /registrations endpoint when the user is already registered.
     * Verifies that a bad request response is returned for duplicate registrations.
     */
    @Test
    public void testRegisterForEventDuplicateRegistration() throws Exception {
        // Arrange
        RegistrationCreationDTO registrationDTO = new RegistrationCreationDTO(
                TEST_USER_ID,
                TEST_EVENT_ID
        );

        when(registrationService.createRegistration(any(RegistrationCreationDTO.class)))
                .thenThrow(new IllegalArgumentException("User is already registered for this event."));

        String requestJson = objectMapper.writeValueAsString(registrationDTO);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("User is already registered for this event."));

        // Verify the service was called
        verify(registrationService).createRegistration(any(RegistrationCreationDTO.class));
    }

    /**
     * Test the POST /registrations endpoint when the event is fully booked.
     * Verifies that a bad request response is returned when the event has reached its maximum participants.
     */
    @Test
    public void testRegisterForEventWhenFullyBooked() throws Exception {
        // Arrange
        RegistrationCreationDTO registrationDTO = new RegistrationCreationDTO(
                TEST_USER_ID,
                TEST_EVENT_ID
        );

        when(registrationService.createRegistration(any(RegistrationCreationDTO.class)))
                .thenThrow(new IllegalArgumentException("Event is fully booked."));

        String requestJson = objectMapper.writeValueAsString(registrationDTO);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("Event is fully booked."));

        // Verify the service was called
        verify(registrationService).createRegistration(any(RegistrationCreationDTO.class));
    }
} 