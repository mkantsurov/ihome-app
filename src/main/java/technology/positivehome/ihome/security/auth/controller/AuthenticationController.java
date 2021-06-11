package technology.positivehome.ihome.security.auth.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import technology.positivehome.ihome.configuration.JwtSettings;
import technology.positivehome.ihome.security.auth.jwt.extractor.TokenExtractor;
import technology.positivehome.ihome.security.auth.jwt.verifier.TokenVerifier;
import technology.positivehome.ihome.security.exceptions.InvalidJwtToken;
import technology.positivehome.ihome.security.model.UserContext;
import technology.positivehome.ihome.security.model.token.AccessJwtToken;
import technology.positivehome.ihome.security.model.token.JwtTokenFactory;
import technology.positivehome.ihome.security.model.token.RawAccessJwtToken;
import technology.positivehome.ihome.security.model.token.RefreshToken;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/auth")
@Validated
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    private static final String X_CLIENT_SSL_DN = "X-CLIENT-SSL-DN";
    private final TokenExtractor tokenExtractor;
    private final JwtSettings jwtSettings;
    private final TokenVerifier tokenVerifier;
    private final UserService userService;
    private final JwtTokenFactory tokenFactory;

    @Autowired
    public AuthenticationController(TokenExtractor tokenExtractor,
                                    JwtSettings jwtSettings,
                                    TokenVerifier tokenVerifier,
                                    UserService userService,
                                    JwtTokenFactory tokenFactory) {
        this.tokenExtractor = tokenExtractor;
        this.jwtSettings = jwtSettings;
        this.tokenVerifier = tokenVerifier;
        this.userService = userService;
        this.tokenFactory = tokenFactory;
    }

    @PostMapping(value = "/refresh", produces={ MediaType.APPLICATION_JSON_VALUE })
    public AccessJwtToken refreshAuthenticationToken(
            @RequestHeader(value = X_CLIENT_SSL_DN, required = false) String dn,
            @RequestBody(required = false) String token) {

        RawAccessJwtToken rawToken = new RawAccessJwtToken(token);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(InvalidJwtToken::new);

        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtToken();
        }

        String subject = refreshToken.getSubject();
        String[] userIds = Optional.ofNullable(subject).orElseThrow().split("/(\\D+)");
        User user = userService.getById(Long.parseLong(userIds[1]));

        if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole().authority()))
                .collect(Collectors.toList());

        UserContext userContext = UserContext.create(user.getUsername(), authorities);
        return tokenFactory.createAccessJwtToken(userContext);
    }

}

