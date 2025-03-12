package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BoardGameInstanceService {
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    public BoardGameInstance findBoardGameInstanceByIndividualGameId(int id) {
        return boardGameInstanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no boardGameInstance with ID " + id + "."));
    }

}
//TODO
//find all use cases relating to this classService, then implement those methods