package ca.mcgill.ecse321.boardr.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.boardr.model.Registration;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Registration.RegistrationKey> {
    //public Registration findRegistrationByKey(Registration.RegistrationKey registrationKey);
}