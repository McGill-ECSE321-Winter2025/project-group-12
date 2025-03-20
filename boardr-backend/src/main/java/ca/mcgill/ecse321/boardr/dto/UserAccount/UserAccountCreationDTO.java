package ca.mcgill.ecse321.boardr.dto.UserAccount;

import ca.mcgill.ecse321.boardr.model.UserAccount;

import java.sql.Date;
public class UserAccountCreationDTO {

    private String name;
    private String email;
    private String password; 
    private Date creationDate;
    
 
    
    // Default constructor (needed for JSON deserialization)
    public UserAccountCreationDTO() {
    }

    // Convenience constructor taking the entity
    public UserAccountCreationDTO(UserAccount userAccount) {
        if (userAccount == null) {
            return;
        }
        
        this.name = userAccount.getName();
        this.email = userAccount.getEmail();
        this.password = userAccount.getPassword();
        this.creationDate = userAccount.getCreationDate();

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password){
        this.password = password;
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

}