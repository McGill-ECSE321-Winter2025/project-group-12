package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.sql.Date;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue
    private int userId;
    private String name;
    private String email;
    private String password;
    private Date creationDate;

    @OneToMany
    private Set<BorrowRequest> borrowRequests;

    @OneToMany
    private Set<UserRole> userRoles;

    protected User(){};

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.creationDate = new Date(System.currentTimeMillis());
    }

    public int getUserId() {
        return userId;
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
}
