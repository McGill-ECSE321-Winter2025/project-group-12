package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.Review.ReviewCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Review.ReviewResponseDTO;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.repo.ReviewRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;

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

@SpringBootTest(classes = ca.mcgill.ecse321.boardr.BoardrApplication.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private BoardGameRepository boardGameRepository;

    @MockBean
    private ReviewRepository reviewRepository;
    
    private UserAccount testUser;
    private BoardGame testBoardGame;
    private Review testReview;
    
    private static final int TEST_REVIEW_ID = 2;
    private static final int TEST_USER_ID = 3;
    private static final int TEST_BOARD_GAME_ID = 4;
    private static final int TEST_RATING = 5;
    private static final String TEST_COMMENT = "Great game!";

    @BeforeEach
    public void setUp() {
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
        
        // Create test board game
        testBoardGame = new BoardGame("Test Game", "A test board game");
        try {
            field = BoardGame.class.getDeclaredField("gameId");
            field.setAccessible(true);
            field.set(testBoardGame, TEST_BOARD_GAME_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create test review
        testReview = new Review(TEST_RATING, TEST_COMMENT, testUser, testBoardGame);
        try {
            // Ensure the field name is correct
            field = Review.class.getDeclaredField("reviewId"); // Check if this is the correct field name
            field.setAccessible(true);
            field.set(testReview, TEST_REVIEW_ID);
        } catch (NoSuchFieldException e) {
            System.err.println("Field 'reviewID' not found in Review class.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Cannot access field 'reviewID'.");
            e.printStackTrace();
        }
        
        // Mock repository behaviors
        when(userAccountRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(boardGameRepository.findById(TEST_BOARD_GAME_ID)).thenReturn(Optional.of(testBoardGame));
        when(reviewRepository.findById(TEST_REVIEW_ID)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(reviewRepository.findAll()).thenReturn(List.of(testReview));
        
        doNothing().when(reviewRepository).deleteById(TEST_REVIEW_ID);
    }

    @Test
    public void testCreateReview() throws Exception {
        // Arrange
        ReviewCreationDTO reviewCreationDTO = new ReviewCreationDTO(
                TEST_USER_ID,
                TEST_BOARD_GAME_ID,
                TEST_RATING,
                TEST_COMMENT
        );
        
        String requestJson = objectMapper.writeValueAsString(reviewCreationDTO);
        
        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        ReviewResponseDTO responseDTO = objectMapper.readValue(responseJson, ReviewResponseDTO.class);
        assertEquals(TEST_REVIEW_ID, responseDTO.getId());
        assertEquals(TEST_RATING, responseDTO.getRating());
        assertEquals(TEST_COMMENT, responseDTO.getComment());
        assertEquals(TEST_USER_ID, responseDTO.getUserAccountId());
        assertEquals(TEST_BOARD_GAME_ID, responseDTO.getBoardGameId());
    }

    @Test
    public void testGetAllReviews() throws Exception {
        // Act and Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        ReviewResponseDTO[] reviews = objectMapper.readValue(responseJson, ReviewResponseDTO[].class);
        
        assertEquals(1, reviews.length);
        ReviewResponseDTO responseDTO = reviews[0];
        
        assertEquals(TEST_REVIEW_ID, responseDTO.getId());
        assertEquals(TEST_RATING, responseDTO.getRating());
        assertEquals(TEST_COMMENT, responseDTO.getComment());
        assertEquals(TEST_USER_ID, responseDTO.getUserAccountId());
        assertEquals(TEST_BOARD_GAME_ID, responseDTO.getBoardGameId());
    }

    @Test
    public void testDeleteReview() throws Exception {
        // Act and Assert - The controller returns 204 No Content
        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/{reviewId}", TEST_REVIEW_ID))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        
        // Verify the review repository was called to delete the review by ID
        verify(reviewRepository).deleteById(TEST_REVIEW_ID);
    }
}