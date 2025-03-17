package ca.mcgill.ecse321.boardr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountResponseDTO;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountUpdateDTO;

import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;            
import java.util.stream.Collectors; 
@RestController
@RequestMapping("/users")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

   

    @PostMapping
    public UserAccountResponseDTO createUser(@RequestBody UserAccountCreationDTO userDTO) {
        UserAccount createdUser = userAccountService.createUser(userDTO);
        return new UserAccountResponseDTO (createdUser);
    }

    
    @GetMapping("/{id}")
    public UserAccountResponseDTO getUserById(@PathVariable int id) {
        UserAccount user = userAccountService.getUserById(id);
        return new UserAccountResponseDTO(user);
    }

    @GetMapping("/email/{email}")
    public UserAccountResponseDTO getUserByEmail(@PathVariable String email) {
        UserAccount user = userAccountService.getUserByEmail(email);
        return new UserAccountResponseDTO(user);
    }

   
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userAccountService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(
            @PathVariable int id, 
            @RequestBody UserAccountUpdateDTO userDto) {

        userAccountService.updateUser(
                id, 
                userDto.getName(), 
                userDto.getEmail(),
                userDto.getPassword()
        );

       
       
    }
    @GetMapping
    public List<UserAccountResponseDTO> getAllUsers() {
       
        List<UserAccount> allUsers = userAccountService.getAllUsers();

        
        return allUsers.stream()
            .map(UserAccountResponseDTO::new)
            .collect(Collectors.toList());
    }
}

