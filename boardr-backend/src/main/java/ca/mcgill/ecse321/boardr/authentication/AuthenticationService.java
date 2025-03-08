package ca.mcgill.ecse321.boardr.authentication;

import ca.mcgill.ecse321.boardr.dto.Authorization.AuthRequestDTO;
import ca.mcgill.ecse321.boardr.dto.Authorization.AuthResponseDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationRequestDTO;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    JwtService jwtService;




    public AuthResponseDTO login(AuthRequestDTO credentials) {
        UserAccount user = userAccountRepository.findByEmail(credentials.getEmail())
                .orElseThrow(() ->  new IllegalArgumentException("Account not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword())
        );

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(token, user.getEmail(), user.getName());
    }

    public void register(RegistrationRequestDTO userInfo) {
        if (userInfo.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        if (userInfo.getEmail().trim().isEmpty()){
            throw new IllegalArgumentException("Email cannot be empty!");
        }

        if (userInfo.getPassword().trim().isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty!");
        }

        UserAccount existingUserAccount = userAccountRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (existingUserAccount != null) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        UserAccount accountToRegister = new UserAccount(userInfo.getName(), userInfo.getEmail(), bCryptPasswordEncoder.encode(userInfo.getPassword()));
        userAccountRepository.save(accountToRegister);
    }
}
