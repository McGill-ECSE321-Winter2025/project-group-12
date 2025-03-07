package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.authentication.JwtService;
import ca.mcgill.ecse321.boardr.dto.Authorization.AuthRequestDTO;
import ca.mcgill.ecse321.boardr.dto.Authorization.AuthResponseDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationRequestDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationResponseDTO;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        UserAccount user = userAccountRepository.findByEmail(credentials.getEmail()).orElseThrow(() -> new RuntimeException("Account not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        return new AuthResponseDTO(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getName());
    }

    public RegistrationResponseDTO register(RegistrationRequestDTO userInfo) {

        UserAccount accountToRegister = new UserAccount(userInfo.getName(), userInfo.getEmail(), bCryptPasswordEncoder.encode(userInfo.getPassword()));
        UserAccountRepository.save(accountToRegister);
        
    }
}
