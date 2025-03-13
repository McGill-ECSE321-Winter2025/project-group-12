package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceResponseDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BoardGameInstanceService.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BoardGameInstanceServiceTest {

    @Mock
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Mock
    private BoardGameRepository boardGameRepository;

    @Mock
    private GameOwnerRepository gameOwnerRepository;

    @InjectMocks
    private BoardGameInstanceService boardGameInstanceService;

    private BoardGame mockBoardGame;
    private GameOwner mockGameOwner;
    private BoardGameInstance mockInstance;

    @BeforeEach
    public void setUp() {
        mockBoardGame = mock(BoardGame.class);
        when(mockBoardGame.getGameId()).thenReturn(1); 

        mockGameOwner = mock(GameOwner.class);
        when(mockGameOwner.getId()).thenReturn(1); 

        mockInstance = mock(BoardGameInstance.class);
        when(mockInstance.getindividualGameId()).thenReturn(1); 
        when(mockInstance.getBoardGame()).thenReturn(mockBoardGame); 
        when(mockInstance.getGameOwner()).thenReturn(mockGameOwner); 
        when(mockInstance.getCondition()).thenReturn("New"); 
        when(mockInstance.isAvailable()).thenReturn(true); 
    }

    // Test 1: getAllBoardGameInstances - Successful retrieval
    @Test
    public void testGetAllBoardGameInstances_Success() {
        // Arrange
        BoardGameInstance instance2 = new BoardGameInstance(mockBoardGame, mockGameOwner, "Used");
        List<BoardGameInstance> instances = Arrays.asList(mockInstance, instance2);
        when(boardGameInstanceRepository.findAll()).thenReturn(instances);

        // Act
        List<BoardGameInstanceResponseDTO> result = boardGameInstanceService.getAllBoardGameInstances();

        // Assert
        assertEquals(2, result.size());
        assertEquals("New", result.get(0).getCondition());
        assertEquals("Used", result.get(1).getCondition());
        assertTrue(result.get(0).isAvailable());
        assertEquals(1, result.get(0).getBoardGameId());
        assertEquals(1, result.get(0).getGameOwnerId());
        verify(boardGameInstanceRepository, times(1)).findAll();
    }

    // Test 2: getAllBoardGameInstances - Empty list
    @Test
    public void testGetAllBoardGameInstances_Empty() {
        // Arrange
        when(boardGameInstanceRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<BoardGameInstanceResponseDTO> result = boardGameInstanceService.getAllBoardGameInstances();

        // Assert
        assertTrue(result.isEmpty());
        verify(boardGameInstanceRepository, times(1)).findAll();
    }

    // Test 3: createBoardGameInstance - Successful creation
    @Test
    public void testCreateBoardGameInstance_Success() {
        // Arrange
        BoardGameInstanceCreationDTO dto = new BoardGameInstanceCreationDTO("New", 1, 1);
        when(boardGameRepository.findById(1)).thenReturn(Optional.of(mockBoardGame));
        when(gameOwnerRepository.findById(1)).thenReturn(Optional.of(mockGameOwner));
        when(boardGameInstanceRepository.save(any(BoardGameInstance.class))).thenReturn(mockInstance);

        // Act
        BoardGameInstanceResponseDTO result = boardGameInstanceService.createBoardGameInstance(dto);

        // Assert
        assertNotNull(result);
        assertEquals("New", result.getCondition());
        assertTrue(result.isAvailable());
        assertEquals(1, result.getBoardGameId());
        assertEquals(1, result.getGameOwnerId());
        verify(boardGameRepository, times(1)).findById(1);
        verify(gameOwnerRepository, times(1)).findById(1);
        verify(boardGameInstanceRepository, times(1)).save(any(BoardGameInstance.class));
    }

    // Test 4: createBoardGameInstance - BoardGame not found
    @Test
    public void testCreateBoardGameInstance_BoardGameNotFound() {
        // Arrange
        BoardGameInstanceCreationDTO dto = new BoardGameInstanceCreationDTO("New", 1, 1);
        when(boardGameRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException exception = assertThrows(BoardrException.class, () -> {
            boardGameInstanceService.createBoardGameInstance(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Board Game not found", exception.getMessage());
        verify(boardGameRepository, times(1)).findById(1);
        verify(gameOwnerRepository, never()).findById(anyInt());
        verify(boardGameInstanceRepository, never()).save(any());
    }

    // Test 5: createBoardGameInstance - GameOwner not found
    @Test
    public void testCreateBoardGameInstance_GameOwnerNotFound() {
        // Arrange
        BoardGameInstanceCreationDTO dto = new BoardGameInstanceCreationDTO("New", 1, 1);
        when(boardGameRepository.findById(1)).thenReturn(Optional.of(mockBoardGame));
        when(gameOwnerRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BoardrException exception = assertThrows(BoardrException.class, () -> {
            boardGameInstanceService.createBoardGameInstance(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game Owner not found", exception.getMessage());
        verify(boardGameRepository, times(1)).findById(1);
        verify(gameOwnerRepository, times(1)).findById(1);
        verify(boardGameInstanceRepository, never()).save(any());
    }

    // Test 6: removeBoardGameInstance - Successful removal
    @Test
    public void testRemoveBoardGameInstance_Success() {
        // Arrange
        int instanceId = 1;
        when(boardGameInstanceRepository.existsById(instanceId)).thenReturn(true);

        // Act
        boardGameInstanceService.removeBoardGameInstance(instanceId);

        // Assert
        verify(boardGameInstanceRepository, times(1)).existsById(instanceId);
        verify(boardGameInstanceRepository, times(1)).deleteById(instanceId);
    }

    // Test 7: removeBoardGameInstance - Instance not found
    @Test
    public void testRemoveBoardGameInstance_NotFound() {
        // Arrange
        int instanceId = 1;
        when(boardGameInstanceRepository.existsById(instanceId)).thenReturn(false);

        // Act & Assert
        BoardrException exception = assertThrows(BoardrException.class, () -> {
            boardGameInstanceService.removeBoardGameInstance(instanceId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Board Game Instance not found", exception.getMessage());
        verify(boardGameInstanceRepository, times(1)).existsById(instanceId);
        verify(boardGameInstanceRepository, never()).deleteById(anyInt());
    }
}
