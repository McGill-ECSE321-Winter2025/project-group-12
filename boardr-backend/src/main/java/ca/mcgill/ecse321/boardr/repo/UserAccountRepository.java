package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.UserAccount;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
    public Optional<UserAccount> findByUserAccountId(int id);
    public Optional<UserAccount> findByEmail(String email);

}
