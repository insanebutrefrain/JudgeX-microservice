package insane.common.utils;


import insane.common.common.ErrorCode;
import insane.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET_KEY = Arrays.toString(Base64.getEncoder().encode("wkyzanyt".getBytes())); // 安全密钥
    private static final long EXPIRATION_TIME = 30 * 24 * 60 * 60 * 1000L; // 一个月, 生成环境应该改小
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(@NotNull Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static Claims parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "缺少token字段");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "token过期");
            }
            return claims;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token过期");
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token错误");
        }
    }

    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
