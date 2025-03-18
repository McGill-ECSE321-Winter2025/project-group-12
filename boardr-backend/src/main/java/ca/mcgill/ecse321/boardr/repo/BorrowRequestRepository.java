package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;

@Repository
public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer> {
    public BorrowRequest findByBorrowRequestId(int id);

}

