// package ca.mcgill.ecse321.boardr.authentication;

// import ca.mcgill.ecse321.boardr.model.UserAccount;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;

// import java.util.Collection;
// import java.util.List;

// public class CustomUserDetails implements UserDetails {

//     private final UserAccount userAccount;

//     public CustomUserDetails(UserAccount userAccount) {
//         this.userAccount = userAccount;
//     }

//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() {
//         return List.of();
//     }

//     @Override
//     public String getPassword() {
//         return userAccount.getPassword();
//     }

//     @Override
//     public String getUsername() {
//         return userAccount.getEmail();
//     }

//     @Override
//     public boolean isAccountNonExpired() {
//         return true;
//     }

//     @Override
//     public boolean isAccountNonLocked() {
//         return true;
//     }

//     @Override
//     public boolean isCredentialsNonExpired() {
//         return true;
//     }

//     @Override
//     public boolean isEnabled() {
//         return true;
//     }
// }
