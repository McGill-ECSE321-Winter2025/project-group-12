package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BoardGameInstanceService {

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    public List<BoardGameInstance> getAllBoardGameInstances() {
        return StreamSupport.stream(boardGameInstanceRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public Optional<BoardGameInstance> getBoardGameInstanceById(int id) {
        return boardGameInstanceRepository.findById(id);
    }

    public BoardGameInstance createBoardGameInstance(int boardGameId, int gameOwnerId, String condition) {
        BoardGame boardGame = boardGameRepository.findById(boardGameId)
                .orElseThrow(() -> new IllegalArgumentException("Board Game not found"));

        GameOwner gameOwner = gameOwnerRepository.findById(gameOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Game Owner not found"));

        return boardGameInstanceRepository.save(new BoardGameInstance(boardGame, gameOwner, condition));
    }

    public void removeBoardGameInstance(int instanceId) {
        if (!boardGameInstanceRepository.existsById(instanceId)) {
            throw new IllegalArgumentException("Board Game Instance not found");
        }
        boardGameInstanceRepository.deleteById(instanceId);
    }

    public void borrowBoardGameInstance(int instanceId) {
        BoardGameInstance instance = boardGameInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("Board Game Instance not found"));

        if (!instance.isAvailable()) {
            throw new IllegalStateException("Board Game Instance is not available for borrowing");
        }

        // Mark as unavailable when borrowed
        instance.setAvailable(false);
        boardGameInstanceRepository.save(instance);
    }
    
}
