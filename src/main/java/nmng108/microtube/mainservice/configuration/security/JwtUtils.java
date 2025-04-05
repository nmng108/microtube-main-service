package nmng108.microtube.mainservice.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.entity.User;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Configuration
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class JwtUtils {
    long usageDuration; // unit: millisecond
    String secretKey;

    public JwtUtils(@Value("${application.jwt.usage-duration}") long duration, @Value("${application.jwt.secret}") String secretKey) {
        this.usageDuration = duration;
        this.secretKey = secretKey;
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(String.format("%s", user.getUsername()))
                .issuer("app")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + usageDuration))
                .notBefore(new Date())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }

    public Optional<Jws<Claims>> parseSignedClaims(String token) {
        try {
            // Auth server is responsible for verifying tokens.
            // If a token is not valid, the "Authorization" header should be omitted at gateway before being passed to this service.
            return Optional.of(Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))).build()
//                    .verifyWith(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256")).build() // second way
                    .parseSignedClaims(token));
        } catch (ExpiredJwtException ex) {
            log.error("JWT expired");
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty or only whitespace");
        } catch (MalformedJwtException ex) {
            log.error("JWT is invalid ", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            log.error("Signature validation failed");
        }

        return Optional.empty();
    }

    public Optional<DecodedJWT> decode(String token) {
        try {
            // Auth server is responsible for verifying tokens.
            // If a token is not valid, the "Authorization" header should be omitted at gateway before being passed to this service.
            return Optional.of(JWT.decode(token));
        } catch (JWTDecodeException e) {
            log.error("Error while decoding JWT", e);
        }

        return Optional.empty();
    }

    public String getSubject(Jws<Claims> claimsJws) {
        return claimsJws.getPayload().getSubject();
    }

    public Date getExpiration(Jws<Claims> claimsJws) {
        return claimsJws.getPayload().getExpiration();
    }

    //    public String getSubject(String token) {
//        return getPayload(token).getSubject();
//    }
//
    public Date getExpiration(String token) {
        return getPayload(token).getExpiration();
    }

    public Claims getPayload(String token) {
        return parseSignedClaims(token).orElseThrow(UnauthorizedException::new).getPayload();
    }

//    @Bean
//    public LocatorAdapter<String> myLocator(AppKeyRepository appKeyRepository/*Environment env*/) {
//        return new LocatorAdapter<>() {
//            @Override
//            public String locate(ProtectedHeader header) { // a JwsHeader or JweHeader
////                return env.getProperty(header.getKeyId()); // get key from application.properties file
//                AppKey appKey = appKeyRepository.findById(header.getKeyId()).orElse(null);
//
//                if (appKey == null) {
//                    return "placeholder_key";
//                }
//
//                return switch (appKey.getType()) {
//                    case SECRET -> appKey.getValue();
//                    case PUB_KEY -> appKey.getValue().split("\\|")[1];
//                };
//            }
//        };
//    }
}
