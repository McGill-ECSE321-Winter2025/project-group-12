package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_type", discriminatorType = DiscriminatorType.STRING)
public abstract class UserRole {
    @Id
    @GeneratedValue
    private int id;

    public UserRole() {}
    //This doesnt do anything                                                                                                   
    public UserRole(UserAccount userAccount) {
    }

    public int getId() {
        return id;
    }
}
