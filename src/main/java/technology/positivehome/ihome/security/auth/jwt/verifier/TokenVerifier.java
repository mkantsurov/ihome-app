package technology.positivehome.ihome.security.auth.jwt.verifier;

public interface TokenVerifier {
    boolean verify(String jti);
}
