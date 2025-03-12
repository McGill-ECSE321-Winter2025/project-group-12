package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user account with personal details and associated roles.
 * 
 * @author Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
public class UserAccount {
    @Id
    @GeneratedValue /* UserAccount is irrelevant to user, but valuable for table management */
    private int userAccountId;
    private String name;
    private String email;
    private String password;
    private Date creationDate;


    // Allows to get the UserRole from the UserAccount Class
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Set<UserRole> userRoles;

    
    protected UserAccount(){};

    public UserAccount(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.creationDate = Date.valueOf(LocalDate.now());
        this.userRoles = Set.of(new Player(this), new GameOwner(this));
       
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Set<UserRole> getUserRole() {
        return userRoles;
    }
}
