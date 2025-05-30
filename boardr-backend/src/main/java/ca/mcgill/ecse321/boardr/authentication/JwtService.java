// package ca.mcgill.ecse321.boardr.authentication;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Service;

// import java.security.Key;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.function.Function;

// @Service
// public class JwtService {

//     @Value("${security.jwt.secret-key}")
//     private String secretKey;

//     public String generateToken(String email) {
//         Map<String, Object> claims = new HashMap<>();
//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setSubject(email)
//                 .setIssuedAt(new Date(System.currentTimeMillis()))
//                 .setExpiration(new Date(System.currentTimeMillis() + 60 * 12 * 1000))
//                 .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                 .compact();

//     }

//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }


//     public boolean isValidToken(String token, UserDetails userDetails) {
//         final String username = extractUsername(token);
//         return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//     }

//     private boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }


//     private Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }


//     public <T> T extractClaim(String token, Function<Claims, T> claimsFunction) {
//         final Claims claims = extractAllClaims(token);
//         return claimsFunction.apply(claims);
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSignInKey())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     private Key getSignInKey() {
//         byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }

// }
