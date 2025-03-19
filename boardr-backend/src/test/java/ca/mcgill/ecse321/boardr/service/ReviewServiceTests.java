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
 * Unit tests for ReviewService.
 *
 * Methods tested:
 * getAllReviews,
 * createReview,
 * getReviewById,
 * deleteReview
 *
 * @author Kyujin Chu
 * @version 1.0
 * @since 2025-03-18
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
 
        mockUser = mock(UserAccount.class);
        when(mockUser.getUserAccountId()).thenReturn(1);


        mockBoardGame = mock(BoardGame.class);
        when(mockBoardGame.getGameId()).thenReturn(10);


        mockReview = mock(Review.class);
        when(mockReview.getReviewID()).thenReturn(100);
        when(mockReview.getRating()).thenReturn(5);
        when(mockReview.getComment()).thenReturn("Fun");
        when(mockReview.getUserAccount()).thenReturn(mockUser);
        when(mockReview.getBoardGame()).thenReturn(mockBoardGame);
    }

   
    @Test
    public void testGetAllReviews_Success() {

        Review anotherReview = mock(Review.class);
        when(reviewRepo.findAll()).thenReturn(Arrays.asList(mockReview, anotherReview));

        List<Review> result = reviewService.getAllReviews();
        assertEquals(2, result.size());
        verify(reviewRepo, times(1)).findAll();
    }

    @Test
    public void testGetAllReviews_Empty() {
       
        when(reviewRepo.findAll()).thenReturn(Arrays.asList());

        List<Review> result = reviewService.getAllReviews();

        assertTrue(result.isEmpty());
        verify(reviewRepo, times(1)).findAll();
    }

 
    @Test
    public void testCreateReview_Success() {
      
        ReviewCreationDTO dto = new ReviewCreationDTO(1, 10, 5, "Fun");
        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameRepo.findById(10)).thenReturn(Optional.of(mockBoardGame));
        when(reviewRepo.save(any(Review.class))).thenReturn(mockReview);

        Review createdReview = reviewService.createReview(dto);

       
        assertNotNull(createdReview);
        assertEquals(5, createdReview.getRating());
        assertEquals("Fun", createdReview.getComment());
        assertEquals(mockUser, createdReview.getUserAccount());
        assertEquals(mockBoardGame, createdReview.getBoardGame());

        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameRepo, times(1)).findById(10);
        verify(reviewRepo, times(1)).save(any(Review.class));
    }

    @Test
    public void testCreateReview_UserNotFound() {

        ReviewCreationDTO dto = new ReviewCreationDTO(1, 10, 5, "Fun");
        when(userAccountRepo.findById(1)).thenReturn(Optional.empty());
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

        ReviewCreationDTO dto = new ReviewCreationDTO(1, 10, 5, "Loved it!");
        when(userAccountRepo.findById(1)).thenReturn(Optional.of(mockUser));
        when(boardGameRepo.findById(10)).thenReturn(Optional.empty());

        BoardrException ex = assertThrows(BoardrException.class, () -> {
            reviewService.createReview(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid board game ID", ex.getMessage());

        verify(userAccountRepo, times(1)).findById(1);
        verify(boardGameRepo, times(1)).findById(10);
        verify(reviewRepo, never()).save(any(Review.class));
    }


    @Test
    public void testGetReviewById_Success() {
    
        when(reviewRepo.findById(100)).thenReturn(Optional.of(mockReview));
     
        Review found = reviewService.getReviewById(100);      
        assertNotNull(found);
        assertEquals(mockReview, found);
        verify(reviewRepo, times(1)).findById(100);
    }

    @Test
    public void testGetReviewById_NotFound() {
        
        when(reviewRepo.findById(999)).thenReturn(Optional.empty());

    
        BoardrException ex = assertThrows(BoardrException.class, () -> {
            reviewService.getReviewById(999);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Invalid review ID", ex.getMessage());
        verify(reviewRepo, times(1)).findById(999);
    }

   
    @Test
    public void testDeleteReview() {
       
        reviewService.deleteReview(100);

        verify(reviewRepo, times(1)).deleteById(100);
    }

}
