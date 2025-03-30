package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.Event;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {

    List<Event> findByboardGameInstance_BoardGame_GameId(int gameId);

}
