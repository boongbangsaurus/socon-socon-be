package site.soconsocon.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import site.soconsocon.auth.domain.entity.jpa.Member;
import site.soconsocon.auth.security.MemberDetailService;
import site.soconsocon.auth.security.MemberDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private final MemberDetailService memberDetailService;

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
        return extractAllClaims(token).get("memberId", String.class);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public boolean isRefreshTokenExpired(String memberId) {
        String expireTimeString = redisTemplate.opsForValue().get(getRefreshTokenKey(memberId));
        if (expireTimeString == null) {
            // 만료 시간이 없는 경우 (리프레시 토큰이 없거나 만료되었음)
            return true;
        }
        long expireTime = Long.parseLong(expireTimeString);
        return System.currentTimeMillis() > expireTime;
    }

    private String getRefreshTokenKey(String memberId) {
        return "refreshToken:" + memberId;
    }

    public String generateToken(MemberDetails memberDetails) {
        return doGenerateToken(memberDetails, accessExpiration);
    }

    public String generateRefreshToken(MemberDetails memberDetails) {
        return doGenerateToken(memberDetails, refreshExpiration);
    }

    public String doGenerateToken(MemberDetails memberDetails, long expireTime) {
        Claims claims = Jwts.claims();
        String memberId = memberDetails.getMember().getId().toString(); //회원 PK
        claims.put("memberId", memberId); //memberId

        List<String> roleList = new ArrayList<>();
        for (GrantedAuthority role : memberDetails.getAuthorities()) {
            roleList.add(role.getAuthority());
        }
        claims.put("role", roleList); //role

        String jwt = Jwts.builder()
                .setSubject(memberId)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
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
