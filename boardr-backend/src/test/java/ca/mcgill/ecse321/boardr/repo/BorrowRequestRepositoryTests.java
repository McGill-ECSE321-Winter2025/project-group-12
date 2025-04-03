package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.UserAccount;

/**
 * Integration Tests for the {@link BorrowRequestRepository}.
 * Ensures {@link BorrowRequest} entities are correctly persisted and retrieved.
 * 
 * Test Scenario: Verifies the creation, persistence, and retrieval of
 * {@link BorrowRequest} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject the repository instances for related entities (BoardGame, UserAccount, GameOwner, etc.).
 * - Utilizes @BeforeEach and @AfterEach annotations to clear the database before and after each test to maintain a fresh state.
 * 
 * Test Cases:
 * 1. testCreateAndReadBorrowRequest
 * 
 * Dependencies:
 * - Gradle
 * - Jakarta Persistence
 * - Spring Boot
 * 
 * Author: Yoon, Kyujin, Jun Ho
 * Version: 1.0
 */

@SpringBootTest
public class BorrowRequestRepositoryTests {

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;
    
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepo;
    
    @Autowired
    private UserAccountRepository userRepo;
    
    @Autowired
    private BoardGameRepository boardGameRepo;
    
    @Autowired
    private GameOwnerRepository gameOwnerRepo;

    /**
    * Clears the database before and after each test to maintain a fresh state.
    */

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear all repositories to ensure a fresh database state for each test
        borrowRequestRepo.deleteAll();
        boardGameInstanceRepo.deleteAll();
        userRepo.deleteAll();
        boardGameRepo.deleteAll();
        gameOwnerRepo.deleteAll();
    }

    /**
     * Tests the creation and retrieval of a {@link BorrowRequest} entity.
     *
     * Steps:
     * 1. Create a {@link BoardGame} and save it to the repository.
     * 2. Create a {@link UserAccount} for the lender/game owner and save it.
     * 3. Create a {@link GameOwner} linked to the {@link UserAccount}.
     * 4. Create a {@link BoardGameInstance} linked to the {@link GameOwner}.
     * 5. Create and save a {@link UserAccount} for the borrower.
     * 6. Create and save a {@link BorrowRequest} for the {@link BoardGameInstance}.
     * 7. Retrieve and verify that all attributes match the expected values.
     *
     * Assertions:
     * - {@link BorrowRequest} ID should not be null after being saved.
     * - {@link BorrowRequest} should be retrievable from the repository.
     * - The request and return dates should match the expected values.
     * - The default status should be Pending.
     * - The references to {@link BoardGameInstance} and {@link UserAccount} (borrower) should not be null.
     * - The IDs for the {@link BoardGameInstance} and {@link UserAccount} (borrower) should match the saved values.
     */

    @Test
    public void testCreateAndReadBorrowRequest() {

        //Create Board Game
        BoardGame boardGame = new BoardGame("Catan", "A popular strategy board game.");
        boardGame = boardGameRepo.save(boardGame);
        
        //Create user account for lender/game owner
        UserAccount userAccount = new UserAccount("Bobette", "bobette@mail.mcgill.ca.com", "password");
        userAccount = userRepo.save(userAccount);
        
        //Create game owner account
        GameOwner owner = new GameOwner(userAccount);
        owner = gameOwnerRepo.save(owner);

       //Create a board game instance
        BoardGameInstance boardGameInstance = new BoardGameInstance(boardGame, owner, "New");
        boardGameInstance = boardGameInstanceRepo.save(boardGameInstance);

        //Create user account for borrower
        UserAccount borrower = new UserAccount("Bob", "bob@mail.mcgill.ca.com", "password");
        borrower = userRepo.save(borrower);

        
        Date requestDate = Date.valueOf(LocalDate.of(2025, 2, 20));
        Date returnDate  = Date.valueOf(LocalDate.of(2025, 3, 1));

        BorrowRequest borrowRequest = new BorrowRequest(boardGameInstance, borrower, requestDate, returnDate);
        borrowRequest = borrowRequestRepo.save(borrowRequest);
        assertNotNull(borrowRequest.getBorrowRequestId(), "BorrowRequest should have an ID after being saved");

       
        Optional<BorrowRequest> borrowRequestFromDb= borrowRequestRepo.findById(borrowRequest.getBorrowRequestId());
        assertTrue(borrowRequestFromDb.isPresent(), "BorrowRequest should be retrievable from the repository");

        BorrowRequest retrieved = borrowRequestFromDb.get();
        
        // Verify the request and return dates
        assertEquals(requestDate, retrieved.getRequestDate(), "Request date should match");
        assertEquals(returnDate, retrieved.getReturnDate(), "Return date should match");
       
        // Verify default status (should be Pending)
        assertEquals(BorrowRequest.RequestStatus.Pending, retrieved.getRequestStatus(), "Default status should be Pending");

        // Verify references to BoardGameInstance and UserAccount
        assertNotNull(retrieved.getBoardGameInstance(), "BoardGameInstance reference should not be null");
        assertNotNull(retrieved.getUserAccount(), "UserAccount (borrower) reference should not be null");
        assertEquals(boardGameInstance.getIndividualGameId(), retrieved.getBoardGameInstance().getIndividualGameId(), "BoardGameInstance IDs should match");
        assertEquals(borrower.getUserAccountId(), retrieved.getUserAccount().getUserAccountId(), "Borrower IDs should match");
    }

}
