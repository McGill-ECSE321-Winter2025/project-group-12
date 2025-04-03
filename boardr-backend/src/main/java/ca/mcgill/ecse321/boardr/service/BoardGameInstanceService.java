package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.*;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.UserAccount;

import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
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

    @Autowired
    private UserAccountRepository userAccountRepository;


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
    
    @Transactional
public List<BoardGameInstanceDTO> getBoardGameInstancesByBoardGameId(int boardGameId) {
    List<BoardGameInstance> instances = boardGameInstanceRepository.findAllByBoardGameId(boardGameId);

    if (instances.isEmpty()) {
        throw new BoardrException(HttpStatus.NOT_FOUND, "No instances found for Board Game ID " + boardGameId);
    }

    return instances.stream()
        .map(instance -> {
            // Retrieve the gameOwner role id from the instance
            int gameOwnerRoleId = instance.getGameOwner().getId();
            // Use the UserAccountRepository to find the associated UserAccount name
            String gameOwnerName = userAccountRepository.findByGameOwnerId(gameOwnerRoleId)
                                        .map(UserAccount::getName)
                                        .orElse("Unknown");
            return new BoardGameInstanceDTO(instance, gameOwnerName);
        })
        .collect(Collectors.toList());
}


    @Transactional(readOnly = true)
    public BoardGameInstanceResponseDTO getBoardGameInstanceById(int instanceId) {
        BoardGameInstance instance = boardGameInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Board Game Instance not found for ID: " + instanceId));
        return new BoardGameInstanceResponseDTO(instance);
    }

    @Transactional
public BoardGameInstanceResponseDTO updateBoardGameInstanceCondition(int instanceId, @Valid BoardGameInstanceUpdateDTO dto) {
    // Validate the new condition
    if (dto.getCondition() == null || dto.getCondition().trim().isEmpty()) {
        throw new BoardrException(HttpStatus.BAD_REQUEST, "Condition cannot be empty");
    }
    
    // Retrieve the board game instance by its ID
    BoardGameInstance instance = boardGameInstanceRepository.findById(instanceId)
            .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Board Game Instance not found for id: " + instanceId));
    
    // Update the condition (ensure your model has a setter for condition)
    instance.setCondition(dto.getCondition());
    
    // Save the updated instance
    boardGameInstanceRepository.save(instance);
    
    // Return the updated instance as a response DTO
    return new BoardGameInstanceResponseDTO(instance);
}

}