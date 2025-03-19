// package ca.mcgill.ecse321.boardr.controller;

// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RequestMapping("example/")
// @RestController
// public class ExampleController {

//     @GetMapping("username/")
//     public String getAuthenticatedUsername(@AuthenticationPrincipal UserDetails userDetails) {
//         return userDetails != null ? userDetails.getUsername() : "No authenticated user found";
//     }
// }
