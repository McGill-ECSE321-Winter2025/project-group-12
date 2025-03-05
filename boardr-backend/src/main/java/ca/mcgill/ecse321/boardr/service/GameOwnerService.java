package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class GameOwnerService {
    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    public GameOwner findGameOwnerByAccountID(int id) {
        return gameOwnerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no gameOwner with ID " + id + "."));
    }

}
