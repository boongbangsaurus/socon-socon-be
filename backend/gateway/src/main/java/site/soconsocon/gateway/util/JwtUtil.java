package site.soconsocon.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;
//    private final RedisTemplate<String, Object> redisBlackListTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessExpiration}")
    private long accessExpiration;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpiredTime(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public boolean isRefreshTokenExpired(String memberId) {
        String expireTimeString = redisTemplate.opsForValue().get(getRefreshTokenKey(memberId));
        if (expireTimeString == null) {
            // 만료 시간이 없는 경우 (리프레시 토큰이 없거나 만료되었음)
            return true; //true
        }
        long expireTime = Long.parseLong(expireTimeString);
        return System.currentTimeMillis() > expireTime;
    }

    private String getRefreshTokenKey(String memberId) {
        return "refreshToken:" + memberId;
    }


    public Map<String, Object> getUserParseInfo(String token) {
        Claims parseInfo = Jwts.parserBuilder().setSigningKey(getSigningKey(secretKey)).build().parseClaimsJws(token).getBody();
        Map<String, Object> result = new HashMap<>();
        result.put("memberId", parseInfo.get("memberId"));
        result.put("role", parseInfo.get("role", List.class));
        return result;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey(secretKey)).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);

        }
        return false;
    }


}
