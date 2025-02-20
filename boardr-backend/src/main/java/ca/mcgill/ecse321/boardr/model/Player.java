package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PLAYER")
public class Player extends UserRole {


    public Player() {
    }

    public Player(UserAccount userAccount) {
        super(userAccount);
    }
}