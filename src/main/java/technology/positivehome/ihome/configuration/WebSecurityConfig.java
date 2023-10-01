package technology.positivehome.ihome.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import technology.positivehome.ihome.security.RestAuthenticationEntryPoint;
import technology.positivehome.ihome.security.auth.ajax.AjaxAuthenticationProvider;
import technology.positivehome.ihome.security.auth.ajax.AjaxLoginProcessingFilter;
import technology.positivehome.ihome.security.auth.jwt.JwtAuthenticationProvider;
import technology.positivehome.ihome.security.auth.jwt.JwtTokenAuthenticationProcessingFilter;
import technology.positivehome.ihome.security.auth.jwt.SkipPathRequestMatcher;
import technology.positivehome.ihome.security.auth.jwt.extractor.TokenExtractor;
import technology.positivehome.ihome.security.config.CustomCorsFilter;
import technology.positivehome.ihome.security.service.SecurityPermissionEvaluator;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String AUTHENTICATION_URL = "/auth/login";
    public static final String REFRESH_TOKEN_URL = "/auth/token";
    public static final String GUEST_API_URL = "/guest-api/v1/**";
    public static final String LISTENER_URL = "/wsmd/listener";
    public static final String API_ROOT_URL = "/api/**";

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final AjaxAuthenticationProvider ajaxAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final TokenExtractor tokenExtractor;
    private final ObjectMapper objectMapper;
    private final SecurityPermissionEvaluator securityPermissionEvaluator;

    @Autowired
    public WebSecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler,
                             AjaxAuthenticationProvider ajaxAuthenticationProvider, JwtAuthenticationProvider jwtAuthenticationProvider,
                             TokenExtractor tokenExtractor, ObjectMapper objectMapper,
                             SecurityPermissionEvaluator securityPermissionEvaluator) {

        this.authenticationEntryPoint = authenticationEntryPoint;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.ajaxAuthenticationProvider = ajaxAuthenticationProvider;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.tokenExtractor = tokenExtractor;
        this.objectMapper = objectMapper;
        this.securityPermissionEvaluator = securityPermissionEvaluator;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        List<String> permitAllEndpointList = Arrays.asList(
                AUTHENTICATION_URL,
                REFRESH_TOKEN_URL,
                LISTENER_URL,
                GUEST_API_URL
        );
        http
                .csrf(AbstractHttpConfigurer::disable) // We don't need CSRF for JWT based authentication
                .exceptionHandling((exceptionHandling) -> exceptionHandling.authenticationEntryPoint(this.authenticationEntryPoint))
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests.requestMatchers(permitAllEndpointList.toArray(new String[0])).permitAll()
                                .requestMatchers(API_ROOT_URL).authenticated().anyRequest().permitAll()
                )
                .addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildAjaxLoginProcessingFilter(http.getSharedObject(AuthenticationManager.class), AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(http.getSharedObject(AuthenticationManager.class), permitAllEndpointList, API_ROOT_URL), UsernamePasswordAuthenticationFilter.class)
                // disable page caching
                .headers((headers) ->
                        headers
                                .contentTypeOptions(withDefaults())
                                .xssProtection(withDefaults())
                                .cacheControl(withDefaults())
                                .httpStrictTransportSecurity(withDefaults())
                                .frameOptions(withDefaults()))
                .authenticationProvider(ajaxAuthenticationProvider)
                .authenticationProvider(jwtAuthenticationProvider);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(securityPermissionEvaluator);
        return (web) -> web.expressionHandler(handler);
    }

    protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(AuthenticationManager authenticationManager, String loginEntryPoint) throws Exception {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(loginEntryPoint, successHandler, failureHandler, objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(AuthenticationManager authenticationManager, List<String> pathsToSkip, String pattern) throws Exception {
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
        JwtTokenAuthenticationProcessingFilter filter
                = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

}
