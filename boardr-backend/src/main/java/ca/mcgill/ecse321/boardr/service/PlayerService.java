package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

}
