package com.senior.dreamteam.authentication.services;

import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.entities.Token;
import com.senior.dreamteam.entities.User;
import com.senior.dreamteam.services.TokenService;
import com.senior.dreamteam.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable {

    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_ID = "id";
    static final String CLAIM_KEY_ROLE = "role";
    static final String CLAIM_KEY_CREATED = "created";
    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

    @Value("${jwt.secret}")
    private String SECRET;


    public String generateJWT(User user, Long expiration, boolean isAccess) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ID, user.getId().toString());
        claims.put(CLAIM_KEY_USERNAME, user.getEmail());
        claims.put(CLAIM_KEY_ROLE, user.getSimpleAuthorities());
        claims.put(CLAIM_KEY_CREATED, new Date());

        Date expirationDate = generateExpirationDate(expiration);
        String token = generateToken(claims, expirationDate);
        tokenService.upsertToken(Token.builder().token(token).user(user).isRevoke(false).expiration(expirationDate).isAccess(isAccess).build());
        return token;
    }

    public String refreshJWT(String token, Long expiration) {
        Date expirationDate = generateExpirationDate(expiration);
        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, expirationDate);
    }

    public String generateToken(Map<String, Object> claims, Date expiratioDate) {
        final Key key = new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiratioDate)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        final Key key = new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getBody();
    }

    public Date generateExpirationDate(Long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getAuthoritiesFromToken(String token) {
        return userService.getUserByEmail(getUsernameFromToken(token)).getAuthorities();
    }

    public User getUserFromToken(String token) {
        return userService.findUserByEmail(getUsernameFromToken(token));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public Date getCreatedDateFromToken(String token) {
        return new Date((Long) getClaimsFromToken(token).get(CLAIM_KEY_CREATED));
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean isTokenValid(String token, User user) throws Exception {
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }

    public Boolean isTokenRevoked(String token) throws Exception {
        return tokenService.findTokenByToken(token).getIsRevoke();
    }

    public Boolean isAccessToken(String token) throws Exception {
        return tokenService.findTokenByToken(token).getIsAccess();
    }

    public Boolean isAdminToken(String token) throws Exception {
        return getAuthoritiesFromToken(token).stream().anyMatch(a -> a.equals(Roles.ADMIN.name()));
    }

    public Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return created.before(lastPasswordReset);
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return (!isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token));
    }

    public Token revokeToken(String tokenString) throws Exception {
        Token token = tokenService.findTokenByToken(tokenString);
        token.setIsRevoke(true);
        return tokenService.upsertToken(token);
    }
}