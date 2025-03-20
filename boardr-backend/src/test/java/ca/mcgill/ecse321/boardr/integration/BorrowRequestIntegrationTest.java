package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestResponseDTO;
import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestStatusUpdateDTO;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.model.BorrowRequest.RequestStatus;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BorrowRequestRepository;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the BorrowRequest API endpoints.
 * These tests verify that the BorrowRequestController correctly interacts with the BorrowRequestService
 * and returns the expected responses.
 */
@SpringBootTest(classes = ca.mcgill.ecse321.boardr.BoardrApplication.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BorrowRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @MockBean
    private BorrowRequestRepository borrowRequestRepository;
    
    private UserAccount testUser;
    private BoardGameInstance testBoardGameInstance;
    private BorrowRequest testBorrowRequest;
    
    private static final Date TEST_REQUEST_DATE = Date.valueOf(LocalDate.of(2025, 2, 10));
    private static final Date TEST_RETURN_DATE = Date.valueOf(LocalDate.of(2025, 3, 1));
    private static final int TEST_USER_ID = 1;
    private static final int TEST_BOARD_GAME_INSTANCE_ID = 1;
    private static final int TEST_BORROW_REQUEST_ID = 1;

    @BeforeEach
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
        // Create test user
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
        GameOwner gameOwner = new GameOwner(testUser);
        
        testBoardGameInstance = new BoardGameInstance(testBoardGame, gameOwner, "Good");
        try {
            field = BoardGameInstance.class.getDeclaredField("individualGameId");
            field.setAccessible(true);
            field.set(testBoardGameInstance, TEST_BOARD_GAME_INSTANCE_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create test borrow request
        testBorrowRequest = new BorrowRequest(
            testBoardGameInstance,
            testUser,
            TEST_REQUEST_DATE,
            TEST_RETURN_DATE
        );
        try {
            field = BorrowRequest.class.getDeclaredField("borrowRequestId");
            field.setAccessible(true);
            field.set(testBorrowRequest, TEST_BORROW_REQUEST_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mock repository behaviors
        when(userAccountRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(boardGameInstanceRepository.findById(TEST_BOARD_GAME_INSTANCE_ID)).thenReturn(Optional.of(testBoardGameInstance));
        when(borrowRequestRepository.save(any(BorrowRequest.class))).thenReturn(testBorrowRequest);
        when(borrowRequestRepository.findById(TEST_BORROW_REQUEST_ID)).thenReturn(Optional.of(testBorrowRequest));
        when(borrowRequestRepository.findAll()).thenReturn(List.of(testBorrowRequest));
        
        // This is critical for void methods like delete()
        doNothing().when(borrowRequestRepository).delete(any(BorrowRequest.class));
    }

    /**
     * Test the POST /borrowRequests endpoint for creating a new borrow request.
     * Verifies that the borrow request is created successfully and the response contains
     * the correct borrow request data.
     */
    @Test
    public void testCreateBorrowRequest() throws Exception {
        // Arrange
        BorrowRequestCreationDTO borrowRequestCreationDTO = new BorrowRequestCreationDTO(
                TEST_USER_ID,
                TEST_BOARD_GAME_INSTANCE_ID,
                TEST_REQUEST_DATE,
                TEST_RETURN_DATE
        );
        
        String requestJson = objectMapper.writeValueAsString(borrowRequestCreationDTO);
        
        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/borrowRequests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        BorrowRequestResponseDTO responseDTO = objectMapper.readValue(responseJson, BorrowRequestResponseDTO.class);
        
        assertEquals(TEST_BORROW_REQUEST_ID, responseDTO.getId());
        
        // Convert java.sql.Date to LocalDate for comparison
        LocalDate expectedRequestDate = TEST_REQUEST_DATE.toLocalDate();
        LocalDate actualRequestDate = responseDTO.getRequestDate().toLocalDate();
        assertEquals(expectedRequestDate, actualRequestDate);
        
        LocalDate expectedReturnDate = TEST_RETURN_DATE.toLocalDate();
        LocalDate actualReturnDate = responseDTO.getReturnDate().toLocalDate();
        assertEquals(expectedReturnDate, actualReturnDate);
        
        assertEquals(TEST_BOARD_GAME_INSTANCE_ID, responseDTO.getBoardGameInstanceId());
        assertEquals(TEST_USER_ID, responseDTO.getUserAccountId());
    }

    /**
     * Test the GET /borrowRequests endpoint for retrieving all borrow requests.
     * Verifies that the response contains the correct list of borrow requests.
     */
    @Test
    public void testGetAllBorrowRequests() throws Exception {
        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/borrowRequests"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        BorrowRequestResponseDTO[] borrowRequests = objectMapper.readValue(responseJson, BorrowRequestResponseDTO[].class);
        
        assertEquals(1, borrowRequests.length);
        BorrowRequestResponseDTO responseDTO = borrowRequests[0];
        
        assertEquals(TEST_BORROW_REQUEST_ID, responseDTO.getId());

                // Convert java.sql.Date to LocalDate for comparison
        LocalDate expectedRequestDate = TEST_REQUEST_DATE.toLocalDate();
        LocalDate actualRequestDate = responseDTO.getRequestDate().toLocalDate();
        assertEquals(expectedRequestDate, actualRequestDate);

        LocalDate expectedReturnDate = TEST_RETURN_DATE.toLocalDate();
        LocalDate actualReturnDate = responseDTO.getReturnDate().toLocalDate();
        assertEquals(expectedReturnDate, actualReturnDate);
        
        assertEquals(TEST_BOARD_GAME_INSTANCE_ID, responseDTO.getBoardGameInstanceId());
        assertEquals(TEST_USER_ID, responseDTO.getUserAccountId());
    }

    @Test
    public void testUpdateBorrowRequestStatus() throws Exception {
        // Arrange
        BorrowRequestStatusUpdateDTO statusUpdateDTO = new BorrowRequestStatusUpdateDTO(RequestStatus.Accepted);
        String requestJson = objectMapper.writeValueAsString(statusUpdateDTO);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/borrowRequests/{borrowRequestId}/status", TEST_BORROW_REQUEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify that the repository was called to update the borrow request status
        verify(borrowRequestRepository).save(any(BorrowRequest.class));
    }

    /**
     * Test the DELETE /borrowRequests/{borrowRequestId} endpoint for deleting a borrow request.
     * Verifies that the borrow request is deleted successfully.
     */
    @Test
    public void testDeleteBorrowRequest() throws Exception {
        // Act and Assert - The controller returns 204 No Content
        mockMvc.perform(MockMvcRequestBuilders.delete("/borrowRequests/{borrowRequestId}", TEST_BORROW_REQUEST_ID))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        
        // Verify the borrow request repository was called to delete the borrow request by ID
        verify(borrowRequestRepository).deleteById(TEST_BORROW_REQUEST_ID);
    }
}