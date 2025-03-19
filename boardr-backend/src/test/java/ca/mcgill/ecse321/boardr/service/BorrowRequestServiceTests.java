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
        when(mockBorrowRequest.getRequestDate()).thenReturn(Date.valueOf("2025-03-10"));
        when(mockBorrowRequest.getReturnDate()).thenReturn(Date.valueOf("2025-03-20"));
    }

 
    @Test
    public void testGetAllBorrowRequests_Success() {
    
        BorrowRequest anotherRequest = mock(BorrowRequest.class);
        when(borrowRequestRepo.findAll()).thenReturn(Arrays.asList(mockBorrowRequest, anotherRequest));
        List<BorrowRequest> result = borrowRequestService.getAllBorrowRequests();
        assertEquals(2, result.size());
        verify(borrowRequestRepo, times(1)).findAll();
    }

    @Test
    public void testGetAllBorrowRequests_Empty() {
        when(borrowRequestRepo.findAll()).thenReturn(Arrays.asList());
        List<BorrowRequest> result = borrowRequestService.getAllBorrowRequests();
        assertTrue(result.isEmpty());
        verify(borrowRequestRepo, times(1)).findAll();
    }

    @Test
    public void testCreateBorrowRequest_Success() {
        Date requestDate = Date.valueOf("2025-03-10");
        Date returnDate = Date.valueOf("2025-03-20");
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(1, 10, requestDate, returnDate);

        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameInstanceRepo.findById(10)).thenReturn(Optional.of(mockGameInstance));
        when(borrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(mockBorrowRequest);
        BorrowRequest created = borrowRequestService.createBorrowRequest(dto);
        assertNotNull(created);
        assertEquals(mockBorrowRequest, created);
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
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(1, 10,
                Date.valueOf("2025-03-10"), Date.valueOf("2025-03-20"));
        when(userAccountRepo.findById(1)).thenReturn(Optional.empty());

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
    
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(1, 10,
                Date.valueOf("2025-03-10"), Date.valueOf("2025-03-20"));
        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameInstanceRepo.findById(10)).thenReturn(Optional.empty());

        BoardrException ex = assertThrows(BoardrException.class, () -> {
            borrowRequestService.createBorrowRequest(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("No available board game instances", ex.getMessage());
        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameInstanceRepo, times(1)).findById(10);
        verify(borrowRequestRepo, never()).save(any(BorrowRequest.class));
    }

   
    @Test
    public void testGetBorrowRequestById_Success() {

        when(borrowRequestRepo.findById(100)).thenReturn(Optional.of(mockBorrowRequest));
        BorrowRequest found = borrowRequestService.getBorrowRequestById(100);
        assertNotNull(found);
        assertEquals(mockBorrowRequest, found);
        verify(borrowRequestRepo, times(1)).findById(100);
    }

    @Test
    public void testGetBorrowRequestById_NotFound() {

        when(borrowRequestRepo.findById(999)).thenReturn(Optional.empty());

        BoardrException ex = assertThrows(BoardrException.class, () -> {
            borrowRequestService.getBorrowRequestById(999);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid borrow request ID", ex.getMessage());
        verify(borrowRequestRepo, times(1)).findById(999);
    }

 
    @Test
    public void testDeleteBorrowRequest() {
        // Act
        borrowRequestService.deleteBorrowRequest(100);

        // Assert
        verify(borrowRequestRepo, times(1)).deleteById(100);
    }
    
  
    @Test
    public void testUpdateBorrowRequestStatus_Accepted() {
    // Ensure the borrow request exists and returns our mock object
    when(borrowRequestRepo.findById(100)).thenReturn(Optional.of(mockBorrowRequest));
    when(borrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(mockBorrowRequest);
    when(boardGameInstanceRepo.save(any(BoardGameInstance.class))).thenReturn(mockGameInstance);
    
    // Update the status to Accepted
    BorrowRequest updatedRequest = borrowRequestService.updateBorrowRequestStatus(100, BorrowRequest.RequestStatus.Accepted);
    
    // Check that the status update is triggered on the borrow request
    verify(mockBorrowRequest, times(1)).setRequestStatus(BorrowRequest.RequestStatus.Accepted);
    // Verify that the board game instance is set to unavailable
    verify(mockGameInstance, times(1)).setAvailable(false);
    verify(boardGameInstanceRepo, times(1)).save(mockGameInstance);
    // Verify that the borrow request is saved
    verify(borrowRequestRepo, times(1)).save(mockBorrowRequest);
    assertNotNull(updatedRequest);
    assertEquals(mockBorrowRequest, updatedRequest);
}

@Test
public void testUpdateBorrowRequestStatus_Pending() {
    // Ensure the borrow request exists and returns our mock object
    when(borrowRequestRepo.findById(100)).thenReturn(Optional.of(mockBorrowRequest));
    when(borrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(mockBorrowRequest);
    
    // Update the status to Pending
    BorrowRequest updatedRequest = borrowRequestService.updateBorrowRequestStatus(100, BorrowRequest.RequestStatus.Pending);
    
    // Verify that the status update is triggered on the borrow request
    verify(mockBorrowRequest, times(1)).setRequestStatus(BorrowRequest.RequestStatus.Pending);
    // Since status is not Accepted, board game instance should not be updated
    verify(mockGameInstance, never()).setAvailable(anyBoolean());
    verify(boardGameInstanceRepo, never()).save(any(BoardGameInstance.class));
    // Verify that the borrow request is saved
    verify(borrowRequestRepo, times(1)).save(mockBorrowRequest);
    
    assertNotNull(updatedRequest);
    assertEquals(mockBorrowRequest, updatedRequest);
}

@Test
public void testUpdateBorrowRequestStatus_Declined() {
    // Ensure the borrow request exists and returns our mock object
    when(borrowRequestRepo.findById(100)).thenReturn(Optional.of(mockBorrowRequest));
    when(borrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(mockBorrowRequest);
    // Update the status to Declined
    BorrowRequest updatedRequest = borrowRequestService.updateBorrowRequestStatus(100, BorrowRequest.RequestStatus.Declined);
    // Verify that the status update is triggered on the borrow request
    verify(mockBorrowRequest, times(1)).setRequestStatus(BorrowRequest.RequestStatus.Declined);
    // Since status is not Accepted, board game instance should not be updated
    verify(mockGameInstance, never()).setAvailable(anyBoolean());
    verify(boardGameInstanceRepo, never()).save(any(BoardGameInstance.class));
    // Verify that the borrow request is saved
    verify(borrowRequestRepo, times(1)).save(mockBorrowRequest);
    assertNotNull(updatedRequest);
    assertEquals(mockBorrowRequest, updatedRequest);
}
}
