package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Entity;

@Entity
public class GameOwner extends UserRole {
    public GameOwner() {
    }

    public GameOwner(UserAccount userAccount) {
        super(userAccount);
    }

}
