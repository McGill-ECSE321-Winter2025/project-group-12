package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Represents a game owner role associated with a user account.
 * 
 * @author Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
@DiscriminatorValue("GAMEOWNER")
public class GameOwner extends UserRole {
    private int userAccountId;

    public GameOwner() {
    }

    public GameOwner(UserAccount userAccount) {
        super(userAccount);
        this.userAccountId = userAccount.getUserAccountId();
    }

    public int getUserAccountId() {
        return userAccountId;
    }
}
