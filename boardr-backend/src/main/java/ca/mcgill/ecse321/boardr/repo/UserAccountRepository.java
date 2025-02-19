package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.UserAccount;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
    public UserAccount findByUserAccountId(int id);
}