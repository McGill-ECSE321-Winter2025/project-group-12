package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_type", discriminatorType = DiscriminatorType.STRING)
public abstract class UserRole {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    public UserRole() {}

    public UserRole(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public int getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }



}
