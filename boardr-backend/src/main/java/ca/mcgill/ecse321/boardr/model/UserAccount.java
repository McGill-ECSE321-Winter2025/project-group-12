package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.sql.Date;

@Entity
public class UserAccount {
    @Id
    @GeneratedValue
    private int userAccountId;
    private String name;
    private String email;
    private String password;
    private Date creationDate;



    protected UserAccount(){};

    public UserAccount(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.creationDate = new Date(System.currentTimeMillis());
        // TODO: Add default role
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
}
