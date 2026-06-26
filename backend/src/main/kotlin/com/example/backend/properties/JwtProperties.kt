package  com.example.backend.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.Key
import javax.crypto.SecretKey

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secretKey: String = "",
    val expiration: Long = 1000 * 60 * 60 * 24
)