package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.Review.ReviewCreationDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.Review;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.ReviewRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewService, in a style similar to BoardGameInstanceServiceTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private UserAccountRepository userAccountRepo;

    @Mock
    private BoardGameRepository boardGameRepo;

    @InjectMocks
    private ReviewService reviewService;

    private UserAccount mockUser;
    private BoardGame mockBoardGame;
    private Review mockReview;

    @BeforeEach
    public void setUp() {
        // Mock user
        mockUser = mock(UserAccount.class);
        when(mockUser.getUserAccountId()).thenReturn(1);

        // Mock board game
        mockBoardGame = mock(BoardGame.class);
        when(mockBoardGame.getGameId()).thenReturn(10);

        // Mock review
        mockReview = mock(Review.class);
        when(mockReview.getReviewID()).thenReturn(100);
        when(mockReview.getRating()).thenReturn(5);
        when(mockReview.getComment()).thenReturn("Awesome game!");
        when(mockReview.getUserAccount()).thenReturn(mockUser);
        when(mockReview.getBoardGame()).thenReturn(mockBoardGame);
    }

    // --------------------------------
    // getAllReviews()
    // --------------------------------
    @Test
    public void testGetAllReviews_Success() {
        // Arrange
        Review anotherReview = mock(Review.class);
        when(reviewRepo.findAll()).thenReturn(Arrays.asList(mockReview, anotherReview));

        // Act
        List<Review> result = reviewService.getAllReviews();

        // Assert
        assertEquals(2, result.size());
        verify(reviewRepo, times(1)).findAll();
    }

    @Test
    public void testGetAllReviews_Empty() {
        // Arrange
        when(reviewRepo.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Review> result = reviewService.getAllReviews();

        // Assert
        assertTrue(result.isEmpty());
        verify(reviewRepo, times(1)).findAll();
    }

    // --------------------------------
    // createReview()
    // --------------------------------
    @Test
    public void testCreateReview_Success() {
        // Arrange
        ReviewCreationDTO dto = new ReviewCreationDTO(1, 10, 5, "Loved it!");
        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameRepo.findById(10)).thenReturn(Optional.of(mockBoardGame));
        when(reviewRepo.save(any(Review.class))).thenReturn(mockReview);

        // Act
        Review createdReview = reviewService.createReview(dto);

        // Assert
        assertNotNull(createdReview);
        assertEquals(5, createdReview.getRating());
        assertEquals("Awesome game!", createdReview.getComment()); // from mockReview stubs
        assertEquals(mockUser, createdReview.getUserAccount());
        assertEquals(mockBoardGame, createdReview.getBoardGame());

        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameRepo, times(1)).findById(10);
        verify(reviewRepo, times(1)).save(any(Review.class));
    }

    @Test
    public void testCreateReview_UserNotFound() {
        // Arrange
        ReviewCreationDTO dto = new ReviewCreationDTO(1, 10, 5, "Loved it!");
        when(userAccountRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            reviewService.createReview(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid user account ID", ex.getMessage());

        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameRepo, never()).findById(anyInt());
        verify(reviewRepo, never()).save(any(Review.class));
    }

    @Test
    public void testCreateReview_BoardGameNotFound() {
        // Arrange
        ReviewCreationDTO dto = new ReviewCreationDTO(1, 10, 5, "Loved it!");
        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameRepo.findById(10)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            reviewService.createReview(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid board game ID", ex.getMessage());

        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameRepo, times(1)).findById(10);
        verify(reviewRepo, never()).save(any(Review.class));
    }

    // --------------------------------
    // getReviewById(...)
    // --------------------------------
    @Test
    public void testGetReviewById_Success() {
        // Arrange
        when(reviewRepo.findById(100)).thenReturn(Optional.of(mockReview));

        // Act
        Review found = reviewService.getReviewById(100);

        // Assert
        assertNotNull(found);
        assertEquals(mockReview, found);
        verify(reviewRepo, times(1)).findById(100);
    }

    @Test
    public void testGetReviewById_NotFound() {
        // Arrange
        when(reviewRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            reviewService.getReviewById(999);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid review ID", ex.getMessage());
        verify(reviewRepo, times(1)).findById(999);
    }

    // --------------------------------
    // deleteReview(...)
    // --------------------------------
    @Test
    public void testDeleteReview() {
        // Act
        reviewService.deleteReview(100);

        // Assert
        verify(reviewRepo, times(1)).deleteById(100);
    }
}
