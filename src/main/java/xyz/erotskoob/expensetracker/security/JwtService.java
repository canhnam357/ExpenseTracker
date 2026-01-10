package xyz.erotskoob.expensetracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Data
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    public String generateAccessToken(UserDetail userDetail, ZonedDateTime now) {
        return generateToken(new HashMap<>(), userDetail, accessTokenExpiration, "ACCESS", now);
    }

    public String generateRefreshToken(UserDetail userDetail, ZonedDateTime now) {
        return generateToken(new HashMap<>(), userDetail, refreshTokenExpiration, "REFRESH", now);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetail userDetail,
            long expirationTime,
            String tokenType,
            ZonedDateTime now
    ) {
        extraClaims.put("type", tokenType);

        Date issuedAt = Date.from(now.toInstant());
        Date expiration = Date.from(now.plusSeconds(expirationTime / 1000).toInstant());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetail.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isAccessTokenValid(String token, UserDetail userDetail) {
        return extractUsername(token).equals(userDetail.getUsername())
                && !isTokenExpired(token)
                && isAccessToken(token);
    }

    private boolean isAccessToken(String token) {
        return "ACCESS".equals(extractClaim(token, claims -> claims.get("type")));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}