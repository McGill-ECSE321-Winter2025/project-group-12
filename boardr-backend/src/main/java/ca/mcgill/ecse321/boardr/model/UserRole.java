package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

/**
 * Represents an abstract user role associated with a user account, serving as a base for specific roles.
 * 
 * @author Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) /* Keep superclass due to difference in roles being only their functions, and not attributes */
@DiscriminatorColumn(name = "role_type", discriminatorType = DiscriminatorType.STRING)
public abstract class UserRole {
    @Id
    @GeneratedValue
    private int id;

    public UserRole() {}

    //Allows for a instantiation with account                                                                                                
    public UserRole(UserAccount userAccount) {
    }

    public int getId() {
        return id;
    }

    @Transient
    public String getUserRole() { return this.getClass().getAnnotation(DiscriminatorValue.class).value(); }
}
