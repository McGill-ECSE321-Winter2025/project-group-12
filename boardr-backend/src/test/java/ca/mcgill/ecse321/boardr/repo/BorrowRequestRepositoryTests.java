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
        assertEquals(boardGameInstance.getindividualGameId(), retrieved.getBoardGameInstance().getindividualGameId(), "BoardGameInstance IDs should match");
        assertEquals(borrower.getUserAccountId(), retrieved.getUserAccount().getUserAccountId(), "Borrower IDs should match");
    }

}
