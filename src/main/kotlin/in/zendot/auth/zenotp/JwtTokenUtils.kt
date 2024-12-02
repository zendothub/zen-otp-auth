package `in`.zendot.auth.zenotp

import `in`.zendot.auth.zenotp.users.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Component
class JwtTokenUtils(
    @Value("\${interkashi.jwt.lifetime}") val jwtLifeDuration: Long,
    @Value("\${interkashi.refreshToken.lifetime}") val refreshTokenLifeDuration: Long,
    @Value("\${interkashi.jwt.secret}") val secretKey: String,
    @Value("\${interkashi.refreshToken.secret}") val refreshTokenSecretKey: String

) {

    fun generateToken(user: User): String = Jwts.builder()
        .claim("username", user.phoneNo)
        .subject(user.id)
        .id(UUID.randomUUID().toString())
        .issuedAt(Date())
        .expiration(Date.from(Instant.now().plusSeconds(jwtLifeDuration)))
        .signWith(getKey())
        .compact()

    fun generateRefreshToken(user: User): String = Jwts.builder()
        .claim("type", "Refresh Token")
        .subject(user.id)
        .id(UUID.randomUUID().toString())
        .issuedAt(Date())
        .expiration(Date.from(Instant.now().plusSeconds(refreshTokenLifeDuration)))
        .signWith(getRefreshTokenKey())
        .compact()

    fun getKey() = SecretKeySpec(
        Base64.getDecoder().decode(secretKey),
        "HmacSHA256"
    )

    fun parse(jwtString: String): Jws<Claims> {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(jwtString)
    }

    fun parseRefreshToken(jwtString: String): Jws<Claims> {
        return Jwts.parser()
            .verifyWith(getRefreshTokenKey())
            .build()
            .parseSignedClaims(jwtString)
    }

    private fun getRefreshTokenKey() = SecretKeySpec(
        Base64.getDecoder().decode(refreshTokenSecretKey),
        "HmacSHA256"
    )
}