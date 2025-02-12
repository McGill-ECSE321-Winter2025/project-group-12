package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

@Entity
public class UserRole {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        Player,
        GameOwner;
    }

    public UserRole() {}

    public UserRole(UserAccount userAccount, Role role) {
        this.userAccount = userAccount;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void toggleRole() {
        if (role != null && role == Role.Player) {
            this.role = Role.GameOwner;
        } else {
            this.role = Role.Player;
        }
    }
}
