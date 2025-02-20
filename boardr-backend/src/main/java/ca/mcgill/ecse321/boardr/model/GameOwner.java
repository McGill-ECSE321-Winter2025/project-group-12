package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GAMEOWNER")
public class GameOwner extends UserRole {
    public GameOwner() {
    }

    public GameOwner(UserAccount userAccount) {
        super(userAccount);
    }

}
