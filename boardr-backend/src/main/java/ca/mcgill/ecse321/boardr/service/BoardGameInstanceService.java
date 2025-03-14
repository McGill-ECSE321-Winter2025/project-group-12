package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.*;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class for managing board game instances in the Boardr application.
 * Provides methods for creating, deleting, and retrieving board game instances. As well as managing borrows
 * This class interacts with the BoardGameInstanceRepository,BoardGameRepository, and GameOwnerRepository
 * to perform operations related to board game instances.
 * @author Jun Ho Oh
 * @version 1.0
 * @since 2025-03-11
 */


 @Service
@Validated
public class BoardGameInstanceService {

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    // 1.Retrieve all board game instances - ResponseDTO for getters
    public List<BoardGameInstanceResponseDTO> getAllBoardGameInstances() {
        return StreamSupport.stream(boardGameInstanceRepository.findAll().spliterator(), false)
                .map(BoardGameInstanceResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 2.Create a new board game instance - Creation DTO for input then Response DTO for output
    @Transactional
    public BoardGameInstanceResponseDTO createBoardGameInstance(@Valid BoardGameInstanceCreationDTO dto) {
        // Additional manual validation
        if (dto.getCondition() == null || dto.getCondition().trim().isEmpty()) {
            throw new BoardrException(HttpStatus.BAD_REQUEST, "Condition cannot be empty");
        }
        
        BoardGame boardGame = boardGameRepository.findById(dto.getBoardGameId())
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Board Game not found"));

        GameOwner gameOwner = gameOwnerRepository.findById(dto.getGameOwnerId())
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Game Owner not found"));

        BoardGameInstance instance = boardGameInstanceRepository.save(new BoardGameInstance(boardGame, gameOwner, dto.getCondition()));
        return new BoardGameInstanceResponseDTO(instance);
    }

    // 3.Remove a board game instance 
    @Transactional
    public void removeBoardGameInstance(int instanceId) {
        if (!boardGameInstanceRepository.existsById(instanceId)) {
            throw new BoardrException(HttpStatus.NOT_FOUND, "Board Game Instance not found");
        }
        boardGameInstanceRepository.deleteById(instanceId);
    }
}