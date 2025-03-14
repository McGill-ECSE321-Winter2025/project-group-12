package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameResponseDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BoardGameService.
 */
@ExtendWith(MockitoExtension.class)
public class BoardGameServiceTest {

    @Mock
    private BoardGameRepository boardGameRepository;

    @InjectMocks
    private BoardGameService boardGameService;

    private BoardGame mockBoardGame;

    @BeforeEach
    public void setUp() {
        mockBoardGame = mock(BoardGame.class);
    }

    private void setUpFullBoardGame() {
        when(mockBoardGame.getGameId()).thenReturn(1);
        when(mockBoardGame.getName()).thenReturn("Chess");
        when(mockBoardGame.getDescription()).thenReturn("A strategic board game");
    }

    // Test 1: getAllBoardGames - Successful retrieval
    @Test
    public void testGetAllBoardGames_Success() {
        setUpFullBoardGame();
        BoardGame mockBoardGame2 = mock(BoardGame.class);
        when(mockBoardGame2.getGameId()).thenReturn(2);
        when(mockBoardGame2.getName()).thenReturn("Monopoly");
        when(mockBoardGame2.getDescription()).thenReturn("A property trading game");

        List<BoardGame> boardGames = Arrays.asList(mockBoardGame, mockBoardGame2);
        when(boardGameRepository.findAll()).thenReturn(boardGames);

        List<BoardGameResponseDTO> result = boardGameService.getAllBoardGames();

        assertEquals(2, result.size());
        assertEquals("Chess", result.get(0).getName());
        assertEquals("Monopoly", result.get(1).getName());
        assertEquals(1, result.get(0).getGameId());
        assertEquals(2, result.get(1).getGameId());
        verify(boardGameRepository, times(1)).findAll();
    }

    // Test 2: getAllBoardGames - Empty list
    @Test
    public void testGetAllBoardGames_Empty() {
        when(boardGameRepository.findAll()).thenReturn(Arrays.asList());

        List<BoardGameResponseDTO> result = boardGameService.getAllBoardGames();

        assertTrue(result.isEmpty());
        verify(boardGameRepository, times(1)).findAll();
    }

    // Test 3: getBoardGameById - Successful retrieval
    @Test
    public void testGetBoardGameById_Success() {
        setUpFullBoardGame();
        when(boardGameRepository.findById(1)).thenReturn(Optional.of(mockBoardGame));

        BoardGameResponseDTO result = boardGameService.getBoardGameById(1);

        assertNotNull(result);
        assertEquals(1, result.getGameId());
        assertEquals("Chess", result.getName());
        assertEquals("A strategic board game", result.getDescription());
        verify(boardGameRepository, times(1)).findById(1);
    }

    // Test 4: getBoardGameById - Not found
    @Test
    public void testGetBoardGameById_NotFound() {
        when(boardGameRepository.findById(1)).thenReturn(Optional.empty());

        BoardrException exception = assertThrows(BoardrException.class, () -> {
            boardGameService.getBoardGameById(1);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Board Game not found", exception.getMessage());
        verify(boardGameRepository, times(1)).findById(1);
    }

    // Test 5: createBoardGame - Successful creation
    @Test
    public void testCreateBoardGame_Success() {
        setUpFullBoardGame();
        BoardGameCreationDTO dto = new BoardGameCreationDTO("Chess", "A strategic board game");
        when(boardGameRepository.save(any(BoardGame.class))).thenReturn(mockBoardGame);

        BoardGameResponseDTO result = boardGameService.createBoardGame(dto);

        assertNotNull(result);
        assertEquals("Chess", result.getName());
        assertEquals("A strategic board game", result.getDescription());
        assertEquals(1, result.getGameId());
        verify(boardGameRepository, times(1)).save(any(BoardGame.class));
    }

    // Test 6: removeBoardGame - Successful removal
    @Test
    public void testRemoveBoardGame_Success() {
        int gameId = 1;
        when(boardGameRepository.existsById(gameId)).thenReturn(true);

        boardGameService.removeBoardGame(gameId);

        verify(boardGameRepository, times(1)).existsById(gameId);
        verify(boardGameRepository, times(1)).deleteById(gameId);
    }

    // Test 7: removeBoardGame - Not found
    @Test
    public void testRemoveBoardGame_NotFound() {
        int gameId = 1;
        when(boardGameRepository.existsById(gameId)).thenReturn(false);

        BoardrException exception = assertThrows(BoardrException.class, () -> {
            boardGameService.removeBoardGame(gameId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Board Game not found", exception.getMessage());
        verify(boardGameRepository, times(1)).existsById(gameId);
        verify(boardGameRepository, never()).deleteById(anyInt());
    }
}