// package ca.mcgill.ecse321.boardr.authentication;

// import ca.mcgill.ecse321.boardr.model.UserAccount;
// import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {
//    @Autowired
//     UserAccountRepository repository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         UserAccount userAccount = repository.findByEmail(username).orElse(null);

//         if(userAccount == null) {
//             System.out.println("User not found");
//             throw new UsernameNotFoundException("User not found");
//         }

//         return new CustomUserDetails(userAccount);
//     }
// }
