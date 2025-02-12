package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_type", discriminatorType = DiscriminatorType.STRING)
public abstract class UserRole {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        Player,
        GameOwner;
    }

    public UserRole() {}

    public UserRole(User userAccount, Role role) {
        this.user = userAccount;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
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
