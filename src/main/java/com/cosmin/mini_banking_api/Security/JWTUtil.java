package com.cosmin.mini_banking_api.Security;

import com.cosmin.mini_banking_api.Enum.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;



    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, Role role){
        return Jwts.builder()
                .setSubject(username)
                .claim("role" , role.name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token,String username){
        return username.equals(extractUsername(token)) && !isExpired(token);
    }

    private boolean isExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Role extractRole(String token){
            String role = extractAllClaims(token).get("role",String.class);
            return Role.valueOf(role);
    }

    private Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }


    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
