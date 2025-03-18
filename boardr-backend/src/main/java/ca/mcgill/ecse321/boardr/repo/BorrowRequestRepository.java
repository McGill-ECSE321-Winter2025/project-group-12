package ca.mcgill.ecse321.boardr.repo;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import java.util.List; 
import org.springframework.data.repository.query.Param; 

@Repository
public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer> {
    public BorrowRequest findByBorrowRequestId(int id);

    @Query("SELECT br.boardGameInstance " +
           "FROM BorrowRequest br " +
           "WHERE br.status = ca.mcgill.ecse321.boardr.model.BorrowRequest.RequestStatus.Accepted " +
           "AND br.userAccount.userAccountId = :borrowerId")
    List<BoardGameInstance> findAcceptedBoardGameInstancesByBorrower(@Param("borrowerId") int borrowerId);

    @Query("SELECT br FROM BorrowRequest br " +
           "WHERE br.boardGameInstance.gameOwner.userAccountId = :gameOwnerId")
    List<BorrowRequest> findAllBorrowRequestsByGameOwner(@Param("gameOwnerId") int gameOwnerId);

}

