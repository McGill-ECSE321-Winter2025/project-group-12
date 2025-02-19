package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.GameOwner;

@SpringBootTest
public class BorrowRequestRepositoryTests {

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;
    @Autowired
    private UserAccountRepository userAccountRepo;
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepo;
    @Autowired
    private GameOwnerRepository gameOwnerRepo;
    @Autowired
    private BoardGameRepository boardGameRepo;

    @AfterEach
    public void clearDatabase() {
        borrowRequestRepo.deleteAll();
        userAccountRepo.deleteAll();
        boardGameInstanceRepo.deleteAll();
        gameOwnerRepo.deleteAll();
        boardGameRepo.deleteAll();
    }

    @Test
    public void testCreateandReadBorrowRequest() {
        
        // Arrange
        UserAccount userAccount1 = new UserAccount("testUser1", "testuser1@mail.mcgill.ca", "password1");
        UserAccount userAccount2 = new UserAccount("testUser2", "testuser2@mail.mcgill.ca", "password2");
        userAccount1 = userAccountRepo.save(userAccount1);
        userAccount2 = userAccountRepo.save(userAccount2);

        GameOwner gameOwner = new GameOwner(userAccount1);
        gameOwner = gameOwnerRepo.save(gameOwner);

        BoardGame boardGame = new BoardGame("testGame", "testGameDescription");
        boardGame = boardGameRepo.save(boardGame);

        BoardGameInstance boardGameInstance = new BoardGameInstance(boardGame, gameOwner, "testGameCondition");
        boardGameInstance = boardGameInstanceRepo.save(boardGameInstance);

        Date requestDate = Date.valueOf("2025-02-12");
        Date returnDate = Date.valueOf("2025-02-19");
        BorrowRequest borrowRequest = new BorrowRequest(boardGameInstance, userAccount2, requestDate, returnDate);
        borrowRequest = borrowRequestRepo.save(borrowRequest);

        // Act
        BorrowRequest retrievedBorrowRequest = borrowRequestRepo.findByBorrowRequestId(borrowRequest.getBorrowRequestId());

        // Assert
        assertNotNull(retrievedBorrowRequest);
        assertEquals(borrowRequest.getBoardGameInstance(), retrievedBorrowRequest.getBoardGameInstance());
        assertEquals(borrowRequest.getUserAccount(), retrievedBorrowRequest.getUserAccount());
        assertEquals(borrowRequest.getRequestDate(), retrievedBorrowRequest.getRequestDate());
        assertEquals(borrowRequest.getReturnDate(), retrievedBorrowRequest.getReturnDate());
        assertEquals(borrowRequest.getRequestStatus(), retrievedBorrowRequest.getRequestStatus());
    }
}