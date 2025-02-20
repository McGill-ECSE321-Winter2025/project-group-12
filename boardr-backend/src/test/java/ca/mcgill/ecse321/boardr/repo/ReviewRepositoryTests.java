package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.Review;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.BoardGame;

/**
 * Integration Tests for the {@link ReviewRepository}.
 * Ensures {@link Review} entities are correctly persisted, retrieved, and deleted.
 * 
 * Test Scenario: Verifies the creation, retrieval, and deletion of {@link Review} entities.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject the repository instances for related entities (Review, UserAccount, and BoardGame).
 * - Utilizes @AfterEach annotation to clear the database after each test to maintain a fresh state.
 * 
 * Test Cases:
 * 1. testCreateandReadReview
 * 2. testDeleteReview
 * 
 * Dependencies:
 * - Gradle
 * - Jakarta Persistence
 * - Spring Boot
 * 
 * Author: Yoon, Junho
 * Version: 1.0
 */

@SpringBootTest
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private UserAccountRepository userRepo;
    @Autowired
    private BoardGameRepository gameRepo;

    /**
     * Clears the repositories after each test to ensure a fresh state.
     */

    @AfterEach
    public void clearDatabase() {
        reviewRepo.deleteAll();
        userRepo.deleteAll();
        gameRepo.deleteAll();
    }
    
    @Test
    public void testCreateandReadReview() {
        
        // Arrange
        UserAccount userAccount = new UserAccount("testUser", "testuser@mail.mcgill.ca", "password");
        userAccount = userRepo.save(userAccount);

        BoardGame boardGame = new BoardGame("testGame", "testGameDescription");
        boardGame = gameRepo.save(boardGame);

        Review review = new Review(5, "Great game!", userAccount, boardGame);
        review = reviewRepo.save(review);

        // Act
        Review retrievedReview = reviewRepo.findByReviewId(review.getReviewID());

        // Assert
        assertNotNull(retrievedReview);
        assertEquals(review.getReviewID(), retrievedReview.getReviewID());
        assertEquals(review.getRating(), retrievedReview.getRating());
        assertEquals(review.getComment(), retrievedReview.getComment());
        assertEquals(review.getReviewDate(), retrievedReview.getReviewDate());
        assertEquals(review.getUserAccount().getUserAccountId(), retrievedReview.getUserAccount().getUserAccountId());
        assertEquals(review.getBoardGame().getGameId(), retrievedReview.getBoardGame().getGameId());

    }

    /**
     * Tests the creation and retrieval of a {@link Review} entity.
     *
     * Steps:
     * 1. Create a {@link UserAccount} (user) and save it to the repository.
     * 2. Create a {@link BoardGame} and save it.
     * 3. Create a {@link Review} with a rating, comment, and associations to user and game.
     * 4. Save the review to the repository.
     * 5. Retrieve the review by its ID.
     * 6. Verify that the retrieved review matches the original review in all attributes.
     *
     * Assertions:
     * - All attributes of the retrieved review should match those of the original review.
     */

    @Test
    public void testDeleteReview() {
        
        // Arrange
        UserAccount userAccount = new UserAccount("testUser", "testuser@mail.mcgill.ca", "password");
        userAccount = userRepo.save(userAccount);

        BoardGame boardGame = new BoardGame("testGame", "testGameDescription");
        boardGame = gameRepo.save(boardGame);

        Review review = new Review(5, "Great game!", userAccount, boardGame);
        review = reviewRepo.save(review);

        // Act
        reviewRepo.delete(review);
        boolean exists = reviewRepo.existsById(review.getReviewID());

        // Assert
        assertFalse(exists);
    }

}
