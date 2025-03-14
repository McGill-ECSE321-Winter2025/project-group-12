package ca.mcgill.ecse321.boardr.dto.UserAccount;

import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.UserRole;

import java.sql.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAccountDTO {

    private int userAccountId;
    private String name;
    private String email;
    private String password; 
    private Date creationDate;
    
    private Set<String> roles;
    
    // Default constructor (needed for JSON deserialization)
    public UserAccountDTO() {
    }

    // Convenience constructor taking the entity
    public UserAccountDTO(UserAccount userAccount) {
        if (userAccount == null) {
            return;
        }
        
        this.userAccountId = userAccount.getUserAccountId();
        this.name = userAccount.getName();
        this.email = userAccount.getEmail();
        this.password = userAccount.getPassword();
        this.creationDate = userAccount.getCreationDate();

        // Convert each UserRole into a string representation
        // e.g., "Player", "GameOwner", or even the 'role_type' from the discriminator
        if (userAccount.getUserRole() != null) {
            this.roles = userAccount.getUserRole().stream()
                .map(UserRole::getUserRole) // This returns the DiscriminatorValue (e.g. "PLAYER" or "GAMEOWNER")
                .collect(Collectors.toSet());
        }
    }

    // Getters and setters
    public int getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(int userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){
        return password;

    }  
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreationDate() {
        return creationDate;
    }
  
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
