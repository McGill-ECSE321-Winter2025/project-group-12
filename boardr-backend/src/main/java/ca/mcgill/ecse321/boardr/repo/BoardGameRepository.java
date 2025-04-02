package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import java.util.Optional;

@Repository
public interface BoardGameRepository extends CrudRepository<BoardGame, Integer> {
    Optional<BoardGame> findByName(String name);
}