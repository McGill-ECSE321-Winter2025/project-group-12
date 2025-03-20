package ca.mcgill.ecse321.boardr.repo;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardGameInstanceRepository extends CrudRepository<BoardGameInstance, Integer> {

    @Query("SELECT b FROM BoardGameInstance b " +
       "WHERE b.gameOwner.id = :gameOwnerId")
List<BoardGameInstance> findAllByGameOwnerId(@Param("gameOwnerId") int gameOwnerId);

}


