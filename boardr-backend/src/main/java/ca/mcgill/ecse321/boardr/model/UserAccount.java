package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

@Entity
public class UserAccount {
    @Id
    @GeneratedValue
    private int userAccountId;
    private String name;
    private String email;
    private String password;
    private Date creationDate;

    // We HAVE to see user role from user so we have to use this
    @OneToMany
    @JoinColumn(name = "role_id")
    private Set<UserRole> userRole;

    protected UserAccount(){};

    public UserAccount(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.creationDate = Date.valueOf(LocalDate.now());
        this.userRole = Set.of(new Player(this), new GameOwner(this));
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
        return userRole;
    }
}
