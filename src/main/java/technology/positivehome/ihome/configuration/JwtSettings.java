package technology.positivehome.ihome.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtSettings {

    /**
     * {@link technology.positivehome.ihome.security.model.token.JwtToken} will expire after this time.
     */
    @Value("${ihome.security.jwt.tokenExpirationTime}")
    private Integer tokenExpirationTime;

    /**
     * Token issuer.
     */
    @Value("${ihome.security.jwt.tokenIssuer}")
    private String tokenIssuer;

    /**
     * Key is used to sign {@link technology.positivehome.ihome.security.model.token.JwtToken}.
     */
    @Value("${ihome.security.jwt.tokenSigningKey}")
    private String tokenSigningKey;

    /**
     * {@link technology.positivehome.ihome.security.model.token.JwtToken} can be refreshed during this timeframe.
     */
    @Value("${ihome.security.jwt.refreshTokenExpTime}")
    private Integer refreshTokenExpTime;

    public Integer getRefreshTokenExpTime() {
        return refreshTokenExpTime;
    }

    public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public Integer getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Integer tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getTokenSigningKey() {
        return tokenSigningKey;
    }

    public void setTokenSigningKey(String tokenSigningKey) {
        this.tokenSigningKey = tokenSigningKey;
    }
}
