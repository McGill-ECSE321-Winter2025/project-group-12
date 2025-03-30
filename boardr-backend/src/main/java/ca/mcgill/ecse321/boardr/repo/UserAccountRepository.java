package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.UserAccount;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
    public Optional<UserAccount> findByUserAccountId(int id);
    public Optional<UserAccount> findByEmail(String email);

    @Query("SELECT u FROM UserAccount u JOIN u.userRoles r WHERE TYPE(r) = GameOwner AND r.id = :gameOwnerRoleId")
Optional<UserAccount> findByGameOwnerId(@Param("gameOwnerRoleId") int gameOwnerRoleId);


    
    

}
