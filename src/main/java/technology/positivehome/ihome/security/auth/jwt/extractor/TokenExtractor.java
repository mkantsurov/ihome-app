package technology.positivehome.ihome.security.auth.jwt.extractor;

/**
 * Implementations of this interface should always return raw base-64 encoded
 * representation of JWT Token.
 *
 * @author vladimir.stankovic
 * <p>
 * Aug 5, 2016
 */
public interface TokenExtractor {
    String extract(String payload);
}
