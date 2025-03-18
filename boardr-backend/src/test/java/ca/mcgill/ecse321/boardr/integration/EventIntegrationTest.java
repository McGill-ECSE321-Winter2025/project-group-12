package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.Event.EventCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Event.EventResponseDTO;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the Event API endpoints.
 * These tests verify that the EventController correctly interacts with the EventService
 * and returns the expected responses.
 */
@SpringBootTest(classes = ca.mcgill.ecse321.boardr.BoardrApplication.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @MockBean
    private EventRepository eventRepository;
    
    private UserAccount testUser;
    private BoardGameInstance testBoardGameInstance;
    private Event testEvent;
    
    private static final int TEST_EVENT_DATE = 20250312; 
    private static final int TEST_EVENT_TIME = 1800; 
    private static final String TEST_LOCATION = "Test Location";
    private static final String TEST_DESCRIPTION = "Test Event Description";
    private static final int TEST_MAX_PARTICIPANTS = 10;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_BOARD_GAME_INSTANCE_ID = 1;
    private static final int TEST_EVENT_ID = 1;

    @BeforeEach
    public void setUp() {
        // Create test user with GameOwner role
        testUser = new UserAccount("Test User", "test@example.com", "password123");

        java.lang.reflect.Field field;
        try {
            field = UserAccount.class.getDeclaredField("userAccountId");
            field.setAccessible(true);
            field.set(testUser, TEST_USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create test board game and instance
        BoardGame testBoardGame = new BoardGame("Test Game", "A test board game");
        GameOwner gameOwner = null;
        for (UserRole role : testUser.getUserRole()) {
            if (role instanceof GameOwner) {
                gameOwner = (GameOwner) role;
                break;
            }
        }
        
        testBoardGameInstance = new BoardGameInstance(testBoardGame, gameOwner, "Good");
        try {
            field = BoardGameInstance.class.getDeclaredField("individualGameId");
            field.setAccessible(true);
            field.set(testBoardGameInstance, TEST_BOARD_GAME_INSTANCE_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create test event
        testEvent = new Event(
            TEST_EVENT_DATE,
            TEST_EVENT_TIME,
            TEST_LOCATION,
            TEST_DESCRIPTION,
            TEST_MAX_PARTICIPANTS,
            testBoardGameInstance,
            testUser
        );
        try {
            field = Event.class.getDeclaredField("eventId");
            field.setAccessible(true);
            field.set(testEvent, TEST_EVENT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mock repository behaviors
        when(userAccountRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(boardGameInstanceRepository.findById(TEST_BOARD_GAME_INSTANCE_ID)).thenReturn(Optional.of(testBoardGameInstance));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventRepository.findById(TEST_EVENT_ID)).thenReturn(Optional.of(testEvent));
        when(eventRepository.findAll()).thenReturn(List.of(testEvent));
        
        // This is critical for void methods like delete()
        // Without this, Mockito will throw an UnstubbedMethodInvocationException when delete() is called
        doNothing().when(eventRepository).delete(any(Event.class));
    }

    /**
     * Test the POST /events endpoint for creating a new event.
     * Verifies that the event is created successfully and the response contains
     * the correct event data.
     */
    @Test
    public void testCreateEvent() throws Exception {
        // Arrange
        EventCreationDTO eventCreationDTO = new EventCreationDTO(
                TEST_EVENT_DATE,
                TEST_EVENT_TIME,
                TEST_LOCATION,
                TEST_DESCRIPTION,
                TEST_MAX_PARTICIPANTS,
                TEST_BOARD_GAME_INSTANCE_ID,
                TEST_USER_ID
        );
        
        String requestJson = objectMapper.writeValueAsString(eventCreationDTO);
        
        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        EventResponseDTO responseDTO = objectMapper.readValue(responseJson, EventResponseDTO.class);
        
        assertEquals(TEST_EVENT_ID, responseDTO.getEventId());
        assertEquals(TEST_EVENT_DATE, responseDTO.getEventDate());
        assertEquals(TEST_EVENT_TIME, responseDTO.getEventTime());
        assertEquals(TEST_LOCATION, responseDTO.getLocation());
        assertEquals(TEST_DESCRIPTION, responseDTO.getDescription());
        assertEquals(TEST_MAX_PARTICIPANTS, responseDTO.getMaxParticipants());
        assertEquals(TEST_BOARD_GAME_INSTANCE_ID, responseDTO.getBoardGameInstanceId());
        assertEquals(TEST_USER_ID, responseDTO.getOrganizerId());
    }

    /**
     * Test the GET /events endpoint for retrieving all events.
     * Verifies that the response contains the correct list of events.
     */
    @Test
    public void testGetAllEvents() throws Exception {
        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        EventResponseDTO[] events = objectMapper.readValue(responseJson, EventResponseDTO[].class);
        
        assertEquals(1, events.length);
        EventResponseDTO responseDTO = events[0];
        
        assertEquals(TEST_EVENT_ID, responseDTO.getEventId());
        assertEquals(TEST_EVENT_DATE, responseDTO.getEventDate());
        assertEquals(TEST_EVENT_TIME, responseDTO.getEventTime());
        assertEquals(TEST_LOCATION, responseDTO.getLocation());
        assertEquals(TEST_DESCRIPTION, responseDTO.getDescription());
        assertEquals(TEST_MAX_PARTICIPANTS, responseDTO.getMaxParticipants());
        assertEquals(TEST_BOARD_GAME_INSTANCE_ID, responseDTO.getBoardGameInstanceId());
        assertEquals(TEST_USER_ID, responseDTO.getOrganizerId());
    }

    /**
     * Test the DELETE /events/{eventId} endpoint for deleting an event.
     * Verifies that the event is deleted successfully.
     */
    @Test
    public void testDeleteEvent() throws Exception {
        // Act and Assert - The controller returns 204 No Content
        mockMvc.perform(MockMvcRequestBuilders.delete("/events/{eventId}", TEST_EVENT_ID)
                .param("userId", String.valueOf(TEST_USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        
        // Verify the event repository was called to delete the event
        verify(eventRepository).delete(testEvent);
    }
}