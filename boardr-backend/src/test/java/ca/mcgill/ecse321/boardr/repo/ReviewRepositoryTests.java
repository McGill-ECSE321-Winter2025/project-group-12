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

@SpringBootTest
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private UserAccountRepository userRepo;
    @Autowired
    private BoardGameRepository gameRepo;

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
    