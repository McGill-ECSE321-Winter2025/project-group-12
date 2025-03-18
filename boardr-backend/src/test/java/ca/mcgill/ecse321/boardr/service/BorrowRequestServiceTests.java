package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowRequestService, similar in style to BoardGameInstanceServiceTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BorrowRequestServiceTests {

    @Mock
    private BorrowRequestRepository borrowRequestRepo;

    @Mock
    private UserAccountRepository userAccountRepo;

    @Mock
    private BoardGameInstanceRepository boardGameInstanceRepo;

    @InjectMocks
    private BorrowRequestService borrowRequestService;

    // Mocks for domain objects
    private UserAccount mockUser;
    private BoardGameInstance mockGameInstance;
    private BorrowRequest mockBorrowRequest;

    @BeforeEach
    public void setUp() {
        mockUser = mock(UserAccount.class);
        when(mockUser.getUserAccountId()).thenReturn(1);

        mockGameInstance = mock(BoardGameInstance.class);
        when(mockGameInstance.getindividualGameId()).thenReturn(10);

        mockBorrowRequest = mock(BorrowRequest.class);
        when(mockBorrowRequest.getBorrowRequestId()).thenReturn(100);
        when(mockBorrowRequest.getUserAccount()).thenReturn(mockUser);
        when(mockBorrowRequest.getBoardGameInstance()).thenReturn(mockGameInstance);
        when(mockBorrowRequest.getRequestDate()).thenReturn(Date.valueOf("2024-01-10"));
        when(mockBorrowRequest.getReturnDate()).thenReturn(Date.valueOf("2024-01-20"));
    }

    // ----------------------------------
    // getAllBorrowRequests()
    // ----------------------------------
    @Test
    public void testGetAllBorrowRequests_Success() {
        // Arrange
        BorrowRequest anotherRequest = mock(BorrowRequest.class);
        when(borrowRequestRepo.findAll()).thenReturn(Arrays.asList(mockBorrowRequest, anotherRequest));

        // Act
        List<BorrowRequest> result = borrowRequestService.getAllBorrowRequests();

        // Assert
        assertEquals(2, result.size());
        verify(borrowRequestRepo, times(1)).findAll();
    }

    @Test
    public void testGetAllBorrowRequests_Empty() {
        // Arrange
        when(borrowRequestRepo.findAll()).thenReturn(Arrays.asList());

        // Act
        List<BorrowRequest> result = borrowRequestService.getAllBorrowRequests();

        // Assert
        assertTrue(result.isEmpty());
        verify(borrowRequestRepo, times(1)).findAll();
    }

    // ----------------------------------
    // createBorrowRequest(...)
    // ----------------------------------
    @Test
    public void testCreateBorrowRequest_Success() {
        // Arrange
        Date requestDate = Date.valueOf("2024-01-10");
        Date returnDate = Date.valueOf("2024-01-20");
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(1, 10, requestDate, returnDate);

        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameInstanceRepo.findById(10)).thenReturn(Optional.of(mockGameInstance));
        when(borrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(mockBorrowRequest);

        // Act
        BorrowRequest created = borrowRequestService.createBorrowRequest(dto);

        // Assert
        assertNotNull(created);
        assertEquals(mockBorrowRequest, created);
        // borrowed request uses the same mock
        assertEquals(100, created.getBorrowRequestId());
        assertEquals(mockUser, created.getUserAccount());
        assertEquals(mockGameInstance, created.getBoardGameInstance());
        assertEquals(requestDate, created.getRequestDate());
        assertEquals(returnDate, created.getReturnDate());

        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameInstanceRepo, times(1)).findById(10);
        verify(borrowRequestRepo, times(1)).save(any(BorrowRequest.class));
    }

    @Test
    public void testCreateBorrowRequest_UserNotFound() {
        // Arrange
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(1, 10,
                Date.valueOf("2024-01-10"), Date.valueOf("2024-01-20"));
        when(userAccountRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            borrowRequestService.createBorrowRequest(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid user account ID", ex.getMessage());
        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameInstanceRepo, never()).findById(anyInt());
        verify(borrowRequestRepo, never()).save(any(BorrowRequest.class));
    }

    @Test
    public void testCreateBorrowRequest_BoardGameInstanceNotFound() {
        // Arrange
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(1, 10,
                Date.valueOf("2024-01-10"), Date.valueOf("2024-01-20"));
        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameInstanceRepo.findById(10)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            borrowRequestService.createBorrowRequest(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("No available board game instances", ex.getMessage());
        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameInstanceRepo, times(1)).findById(10);
        verify(borrowRequestRepo, never()).save(any(BorrowRequest.class));
    }

    // ----------------------------------
    // getBorrowRequestById(...)
    // ----------------------------------
    @Test
    public void testGetBorrowRequestById_Success() {
        // Arrange
        when(borrowRequestRepo.findById(100)).thenReturn(Optional.of(mockBorrowRequest));

        // Act
        BorrowRequest found = borrowRequestService.getBorrowRequestById(100);

        // Assert
        assertNotNull(found);
        assertEquals(mockBorrowRequest, found);
        verify(borrowRequestRepo, times(1)).findById(100);
    }

    @Test
    public void testGetBorrowRequestById_NotFound() {
        // Arrange
        when(borrowRequestRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            borrowRequestService.getBorrowRequestById(999);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid borrow request ID", ex.getMessage());
        verify(borrowRequestRepo, times(1)).findById(999);
    }

    // ----------------------------------
    // deleteBorrowRequest(...)
    // ----------------------------------
    @Test
    public void testDeleteBorrowRequest() {
        // Act
        borrowRequestService.deleteBorrowRequest(100);

        // Assert
        verify(borrowRequestRepo, times(1)).deleteById(100);
    }
}
