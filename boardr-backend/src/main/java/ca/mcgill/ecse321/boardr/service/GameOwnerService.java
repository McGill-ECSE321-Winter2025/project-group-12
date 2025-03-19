package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * Service class for managing game owners in the Boardr application.
 * Provides method for retrieving game owners from the repository.
 *
 * @author Jione Ban
 * @version 1.0
 * @since 2025-03-05
 */
public class GameOwnerService {
    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    public GameOwner findGameOwnerByAccountID(int id) {
        return gameOwnerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no gameOwner with ID " + id + "."));
    }

}
